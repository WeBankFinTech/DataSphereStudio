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

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationResponse;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefExportService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import com.webank.wedatasphere.dss.workflow.dao.NodeInfoMapper;
import com.webank.wedatasphere.dss.workflow.entity.NodeInfo;
import com.webank.wedatasphere.dss.workflow.io.export.NodeExportService;
import com.webank.wedatasphere.dss.workflow.service.BMLService;
import com.webank.wedatasphere.linkis.rpc.Sender;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NodeExportServiceImpl implements NodeExportService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BMLService bmlService;
    @Autowired
    private NodeInfoMapper nodeInfoMapper;

    private Sender projectSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender();

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
    public void downloadAppConnResourceToLocal(String userName, Long projectId, DSSNode dwsNode, String savePath, Workspace workspace,List<DSSLabel> dssLabels) throws Exception {
        NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(dwsNode.getNodeType());
        AppConn appConn = AppConnManager.getAppConnManager().getAppConn(nodeInfo.getAppConnName());
        if (appConn != null) {
            DevelopmentIntegrationStandard devStand = ((OnlyDevelopmentAppConn)appConn).getOrCreateDevelopmentStandard();

            if (null != devStand) {
                if (appConn.getAppDesc().getAppInstancesByLabels(dssLabels).size() > 0) {
                    AppInstance appInstance = appConn.getAppDesc().getAppInstancesByLabels(dssLabels).get(0);
                    RefExportService refExportService = devStand.getRefExportService(appInstance);
                    ExportRequestRef requestRef = RefFactory.INSTANCE.newRef(ExportRequestRef.class, refExportService.getClass().getClassLoader(), "com.webank.wedatasphere.dss.appconn." + appConn.getAppDesc().getAppName().toLowerCase());
                    //todo request param def
                    requestRef.setParameter("jobContent", dwsNode.getJobContent());
                    requestRef.setParameter("projectId", parseProjectId(projectId, appConn.getAppDesc().getAppName(), dssLabels));
                    requestRef.setParameter("nodeType", dwsNode.getNodeType());
                    requestRef.setParameter("user", userName);
                    requestRef.setWorkspace(workspace);
                    if (null != refExportService) {
                        Map<String, Object> nodeExportContent = refExportService.getRefExportOperation().exportRef(requestRef).toMap();
                        if (nodeExportContent != null) {
                            String resourceId = nodeExportContent.get("resourceId").toString();
                            String version = nodeExportContent.get("version").toString();
                            String nodeResourcePath = savePath + File.separator + dwsNode.getId() + ".appconnre";
                            bmlService.downloadToLocalPath(userName, resourceId, version, nodeResourcePath);
                        } else {
                            LOGGER.error("nodeExportContent is null for projectId {}, dwsNode {}", projectId, dwsNode.getName());
                            DSSExceptionUtils.dealErrorException(61023, "nodeExportContent is null", DSSErrorException.class);
                        }
                    }
                }
            }else{
                DSSExceptionUtils.dealErrorException(61024, "Failed to get AppInstance", DSSErrorException.class);
            }
        }
    }


    private Long parseProjectId(Long dssProjectId, String appconnName, List<DSSLabel> dssLabels){
        ProjectRelationRequest projectRelationRequest = new ProjectRelationRequest(dssProjectId, appconnName, dssLabels);
        ProjectRelationResponse projectRelationResponse = (ProjectRelationResponse) projectSender.ask(projectRelationRequest);
        return projectRelationResponse.getAppInstanceProjectId();
    }
}
