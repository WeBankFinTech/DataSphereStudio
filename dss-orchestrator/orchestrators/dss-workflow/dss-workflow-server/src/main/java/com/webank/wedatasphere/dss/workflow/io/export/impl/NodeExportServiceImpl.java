/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.io.export.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationResponse;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.process.ProcessService;
import com.webank.wedatasphere.dss.standard.app.development.publish.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.desc.EnvironmentLabel;
import com.webank.wedatasphere.dss.standard.common.entity.ref.DefaultRefFactory;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.io.export.NodeExportService;
import com.webank.wedatasphere.dss.workflow.service.BMLService;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/03/09 03:07 PM
 */
@Service
public class NodeExportServiceImpl implements NodeExportService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BMLService bmlService;

    @Autowired
    private AppConnService appConnService;

    private RefFactory refFactory=new DefaultRefFactory();

    private Sender projectSender = Sender.getSender(DSSWorkFlowConstant.PROJECT_SERVER_NAME.getValue());


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
    public void downloadAppjointResourceToLocal(String userName, Long projectId, DSSNode dwsNode, String savePath, Workspace workspace) throws Exception {
        AppConn appConn = appConnService.getAppConnByNodeType(dwsNode.getNodeType());
        DSSLabel dssLabel = new EnvironmentLabel();
        dssLabel.setLabel(DSSWorkFlowConstant.DSS_EXPORT_ENV.getValue());
        List<DSSLabel> dssLabels = new ArrayList<>();
        dssLabels.add(dssLabel);
        if (appConn != null) {
            AppStandard devStandOption = appConn.getAppStandards().stream()
                    .filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard)
                    .findAny().orElse(null);
            if (null != devStandOption) {
                DevelopmentIntegrationStandard devStand = (DevelopmentIntegrationStandard) devStandOption;
                ProcessService processService = devStand.getProcessService(dssLabels);
                AppInstance appInstance = getAppInstance(devStand, dssLabel.getLabel());
                processService.setAppInstance(appInstance);
                RefExportService refExportService = (RefExportService) processService.getRefOperationService().stream()
                        .filter(refOperationService -> refOperationService instanceof RefExportService).findAny().orElse(null);
                refExportService.setDevelopmentService(processService);
                ExportRequestRef requestRef = (ExportRequestRef)refFactory.newRef(ExportRequestRef.class, refExportService.getClass().getClassLoader(), "com.webank.wedatasphere.dss.appconn." + appConn.getAppDesc().getAppName().toLowerCase());
                //todo request param def
                requestRef.setParameter("jobContent", dwsNode.getJobContent());
                requestRef.setParameter("projectId", parseProjectId(projectId, appConn.getAppDesc().getAppName(), dssLabel.getLabel()));
                requestRef.setParameter("nodeType", dwsNode.getNodeType());
                requestRef.setParameter("user", userName);
                requestRef.setWorkspace(workspace);
                if (null != refExportService) {
                    Map<String, Object> nodeExportContent = refExportService.createRefExportOperation().exportRef(requestRef).toMap();
                    if (nodeExportContent != null) {
                        String resourceId = nodeExportContent.get("resourceId").toString();
                        String version = nodeExportContent.get("version").toString();
                        String nodeResourcePath = savePath + File.separator + dwsNode.getId() + ".appjointre";
                        bmlService.downloadToLocalPath(userName, resourceId, version, nodeResourcePath);
                    } else {
                        LOGGER.error("nodeExportContent is null for projectId {}, dwsNode {}", projectId, dwsNode.getName());
                        DSSExceptionUtils.dealErrorException(61023, "nodeExportContent is null", DSSErrorException.class);
                    }
                }
            }
        }
    }

    private AppInstance getAppInstance(DevelopmentIntegrationStandard developmentIntegrationStandard, String label) {
        AppInstance appInstance = null;
        for (AppInstance instance : developmentIntegrationStandard.getAppDesc().getAppInstances()) {
            for (DSSLabel dssLabel : instance.getLabels()) {
                if(dssLabel.getLabel().equalsIgnoreCase(label)){
                    appInstance = instance;
                    break;
                }
            }
        }
        return appInstance;
    }

    private Long parseProjectId(Long dssProjectId, String appconnName, String labels){
        DSSLabel dssLabel = new DSSLabel(labels);
        ProjectRelationRequest projectRelationRequest = new ProjectRelationRequest(dssProjectId, appconnName, Lists.newArrayList(dssLabel));
        ProjectRelationResponse projectRelationResponse = (ProjectRelationResponse) projectSender.ask(projectRelationRequest);
        return projectRelationResponse.getAppInstanceProjectId();
    }
}
