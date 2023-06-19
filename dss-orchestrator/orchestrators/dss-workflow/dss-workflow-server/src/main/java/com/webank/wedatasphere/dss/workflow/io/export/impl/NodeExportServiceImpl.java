/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.io.export.impl;

import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.dao.NodeInfoMapper;
import com.webank.wedatasphere.dss.workflow.entity.CommonAppConnNode;
import com.webank.wedatasphere.dss.workflow.io.export.NodeExportService;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


@Service
public class NodeExportServiceImpl implements NodeExportService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("workflowBmlService")
    private BMLService bmlService;
    @Autowired
    private NodeInfoMapper nodeInfoMapper;
    @Autowired
    private WorkflowNodeService workflowNodeService;

    @Override
    public void downloadNodeResourceToLocal(String userName, DSSNode dwsNode, String savePath) {
        List<Resource> resources = dwsNode.getResources();
        if (resources != null) {
            resources.forEach(x -> {
                // TODO: 2020/6/9 防止前台传的 resources：{{}],后期要去掉
                if (x.getResourceId() != null && x.getFileName() != null && x.getVersion() != null) {
                    String nodeResourcePath = savePath + File.separator + x.getResourceId() + "_" + x.getVersion() + ".re";
                    bmlService.downloadToLocalPath(userName, x.getResourceId(), x.getVersion(), nodeResourcePath);
                } else {
                    LOGGER.warn("Illegal resource information");
                    LOGGER.warn("username:{},nodeId:{},nodeName:{},fileName:{},version:{},resourceId:{}", userName, dwsNode.getId(), dwsNode.getName(), x.getFileName(), x.getVersion(), x.getResourceId());
                }
            });
        }
    }

    @Override
    public void downloadAppConnResourceToLocal(String userName, Long projectId, String projectName, DSSNode dwsNode,
                                               String savePath, Workspace workspace, List<DSSLabel> dssLabels) throws Exception {
        CommonAppConnNode node = new CommonAppConnNode();
        node.setJobContent(dwsNode.getJobContent());
        node.setProjectId(projectId);
        node.setWorkspace(workspace);
        node.setDssLabels(dssLabels);
        node.setNodeType(dwsNode.getNodeType());
        node.setName(dwsNode.getName());
        node.setId(dwsNode.getId());
        ExportResponseRef responseRef = workflowNodeService.exportNode(userName, node);
        Map<String, Object> resourceMap = responseRef.getResourceMap();
        String nodeResourcePath = savePath + File.separator + dwsNode.getId() + ".appconnre";
        if(responseRef.isLinkisBMLResources()) {
            String resourceId = resourceMap.get(ImportRequestRef.RESOURCE_ID_KEY).toString();
            String version = resourceMap.get(ImportRequestRef.RESOURCE_VERSION_KEY).toString();
            bmlService.downloadToLocalPath(userName, resourceId, version, nodeResourcePath);
        } else {
            InputStream inputStream = (InputStream) resourceMap.get(ImportRequestRef.INPUT_STREAM_KEY);
            Closeable closeable = (Closeable) resourceMap.get(ImportRequestRef.CLOSEABLE_KEY);
            try {
                FileUtils.copyInputStreamToFile(inputStream, new File(nodeResourcePath));
            } finally {
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(closeable);
            }
        }
    }

}
