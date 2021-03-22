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

package com.webank.wedatasphere.dss.workflow.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationResponse;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.crud.*;
import com.webank.wedatasphere.dss.standard.app.development.process.DevProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProcessService;
import com.webank.wedatasphere.dss.standard.app.development.publish.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportService;
import com.webank.wedatasphere.dss.standard.app.development.query.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.desc.EnvironmentLabel;
import com.webank.wedatasphere.dss.standard.common.entity.ref.DefaultRefFactory;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.NodeInfoMapper;
import com.webank.wedatasphere.dss.workflow.entity.AbstractAppConnNode;
import com.webank.wedatasphere.dss.workflow.entity.NodeGroup;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/11/19 10:34
 */
@Service
public class WorkflowNodeServiceImpl  implements WorkflowNodeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NodeInfoMapper nodeInfoMapper;

    @Autowired
    private AppConnService appConnService;

    private Sender projectSender = Sender.getSender(DSSWorkFlowConstant.PROJECT_SERVER_NAME.getValue());


    private RefFactory refFactory=new DefaultRefFactory();

    @Override
    public List<NodeGroup> listNodeGroups() {
        //cache
        return nodeInfoMapper.listNodeGroups();
    }


    @Override
    public Map<String, Object> createNode(String userName, AbstractAppConnNode node) throws ExternalOperationFailedException, InstantiationException, IllegalAccessException {
        AppConn appConn =  appConnService.getAppConnByNodeType(node.getNodeType());
        DevelopmentIntegrationStandard developmentIntegrationStandard =(DevelopmentIntegrationStandard)appConn.getAppStandards().stream().filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard).findAny().orElse(null);
        if(null != developmentIntegrationStandard)
        {
            DevProcessService devProcessService = (DevProcessService) developmentIntegrationStandard.getProcessServices().stream().filter(processService -> processService instanceof DevProcessService).findAny().orElse(null);
            RefCreationOperation refCreationOperation = devProcessService.getRefCRUDService().createTaskCreationOperation();
            String label = node.getJobContent().get("labels").toString();
            Workspace workspace = (Workspace) node.getJobContent().get("workspace");
            AppInstance appInstance = getAppInstance(developmentIntegrationStandard, label);
            devProcessService.setAppInstance(appInstance);
            CreateNodeRequestRef ref = null;
            try {
                ref = (CreateNodeRequestRef) refFactory.newRef(CreateNodeRequestRef.class, refCreationOperation.getClass().getClassLoader(), "com.webank.wedatasphere.dss.appconn." + appConn.getAppDesc().getAppName().toLowerCase());
            } catch (Exception e) {
                logger.error("Failed to create CreateNodeRequestRef", e);
            }
            //todo set create node params
            ref.setUserName(userName);
            ref.setWorkspace(workspace);
            ref.setJobContent(node.getJobContent());
            ref.setName(node.getName());
            if(ref.getName() == null){
                ref.setName(node.getJobContent().get("title").toString());
            }
            ref.setOrcId(node.getFlowId());
            ref.setOrcName(node.getFlowName());

            // parse to external ProjectId

            ref.setProjectId(parseProjectId(node.getProjectId(), appConn.getAppDesc().getAppName(), label));
            ref.setProjectName(node.getProjectName());
            ref.setNodeType(node.getNodeType());

            ResponseRef responseRef =  refCreationOperation.createRef(ref);
            return responseRef.toMap();
        }
        return null;
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

    @Override
    public void deleteNode(String userName, AbstractAppConnNode node) throws ExternalOperationFailedException, InstantiationException, IllegalAccessException {
        AppConn appConn =  appConnService.getAppConnByNodeType(node.getNodeType());
        DevelopmentIntegrationStandard developmentIntegrationStandard =(DevelopmentIntegrationStandard)appConn.getAppStandards().stream().filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard).findAny().orElse(null);
        if(null != developmentIntegrationStandard)
        {
            DevProcessService devProcessService   = (DevProcessService) developmentIntegrationStandard.getProcessServices().stream().filter(processService -> processService instanceof DevProcessService).findAny().orElse(null);
            RefDeletionOperation refDeletionOperation = devProcessService.getRefCRUDService().createRefDeletionOperation();
            String label = node.getJobContent().get("labels").toString();
            Workspace workspace = (Workspace) node.getJobContent().get("workspace");
            AppInstance appInstance = getAppInstance(developmentIntegrationStandard, label);
            devProcessService.setAppInstance(appInstance);
            DeleteNodeRequestRef ref = null;
            try {
                ref = (DeleteNodeRequestRef)refFactory.newRef(DeleteNodeRequestRef.class, refDeletionOperation.getClass().getClassLoader(), "com.webank.wedatasphere.dss.appconn." + appConn.getAppDesc().getAppName().toLowerCase());
            } catch (Exception e) {
                logger.error("Failed to create DeleteNodeRequestRef", e);
            }
            ref.setUserName(userName);
            ref.setWorkspace(workspace);
            ref.setJobContent(node.getJobContent());
            ref.setName(node.getName());
            ref.setOrcId(node.getFlowId());
            ref.setOrcName(node.getFlowName());
            ref.setProjectId(node.getProjectId());
            ref.setProjectName(node.getProjectName());
            ref.setNodeType(node.getNodeType());
            refDeletionOperation.deleteRef(ref);
        }

    }

    @Override
    public Map<String, Object> updateNode(String userName, AbstractAppConnNode node) throws ExternalOperationFailedException, InstantiationException, IllegalAccessException {
        AppConn appConn =  appConnService.getAppConnByNodeType(node.getNodeType());
        DevelopmentIntegrationStandard developmentIntegrationStandard =(DevelopmentIntegrationStandard)appConn.getAppStandards().stream().filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard).findAny().orElse(null);
        if(null != developmentIntegrationStandard)
        {
            DevProcessService devProcessService = (DevProcessService) developmentIntegrationStandard.getProcessServices().stream().filter(processService -> processService instanceof DevProcessService).findAny().orElse(null);
            RefUpdateOperation refUpdateOperation = devProcessService.getRefCRUDService().createRefUpdateOperation();
            String label = node.getJobContent().get("labels").toString();
            Workspace workspace = (Workspace) node.getJobContent().get("workspace");
            AppInstance appInstance = getAppInstance(developmentIntegrationStandard, label);
            devProcessService.setAppInstance(appInstance);
            UpdateNodeRequestRef ref = null;
            try {
                ref = (UpdateNodeRequestRef)refFactory.newRef(UpdateNodeRequestRef.class, refUpdateOperation.getClass().getClassLoader(), "com.webank.wedatasphere.dss.appconn." + appConn.getAppDesc().getAppName().toLowerCase());
            } catch (Exception e) {
                logger.error("Failed to create UpdateNodeRequestRef", e);
            }
            ref.setUserName(userName);
            ref.setWorkspace(workspace);
            ref.setJobContent(node.getJobContent());
            ref.setName(node.getName());
            ref.setOrcId(node.getFlowId());
            ref.setOrcName(node.getFlowName());
            ref.setProjectId(node.getProjectId());
            ref.setProjectName(node.getProjectName());
            ref.setNodeType(node.getNodeType());
            ResponseRef responseRef = refUpdateOperation.updateRef(ref);
            return responseRef.toMap();
        }else {
            return null;
        }

    }

    @Override
    public Map<String, Object> refresh(String userName, AbstractAppConnNode node) {
        return null;
    }

    @Override
    public void copyNode(String userName, AbstractAppConnNode newNode, AbstractAppConnNode oldNode) {

    }

    @Override
    public void setNodeReadOnly(String userName, AbstractAppConnNode node) {

    }

    @Override
    public List<AbstractAppConnNode> listNodes(String userName, AbstractAppConnNode node) {
        return null;
    }

    @Override
    public Map<String, Object> exportNode(String userName, AbstractAppConnNode node) {
        return null;
    }

    @Override
    public Map<String, Object> importNode(String userName, AbstractAppConnNode node, Map<String, Object> resourceMap, Workspace workspace, String orcVersion) throws Exception {
        AppConn appConn = appConnService.getAppConnByNodeType(node.getNodeType());
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
                RefImportService refImportService = (RefImportService) processService.getRefOperationService().stream()
                        .filter(refOperationService -> refOperationService instanceof RefImportService).findAny().orElse(null);
                refImportService.setDevelopmentService(processService);
                ImportRequestRef requestRef = (ImportRequestRef)refFactory.newRef(ImportRequestRef.class, refImportService.getClass().getClassLoader(), "com.webank.wedatasphere.dss.appconn." + appConn.getAppDesc().getAppName().toLowerCase());
                //todo request param def
                requestRef.setParameter("jobContent", node.getJobContent());
                requestRef.setParameter("projectId", parseProjectId(node.getProjectId(), appConn.getAppDesc().getAppName(), dssLabel.getLabel()));
                requestRef.setParameter("nodeType", node.getNodeType());
                requestRef.setParameter("orcVersion", orcVersion);
                requestRef.setParameter("user", userName);
                requestRef.getParameters().putAll(resourceMap);
                requestRef.setWorkspace(workspace);
                if (null != refImportService) {
                    ResponseRef responseRef = refImportService.createRefImportOperation().importRef(requestRef);
                    return responseRef.toMap();
                }
            }
        }
        return null;
    }

    @Override
    public String getNodeJumpUrl(Map<String, Object> params, AbstractAppConnNode node) throws ExternalOperationFailedException {
        AppConn appConn = appConnService.getAppConnByNodeType(node.getNodeType());
        DevelopmentIntegrationStandard developmentIntegrationStandard =(DevelopmentIntegrationStandard)appConn.getAppStandards().stream().filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard).findAny().orElse(null);
        if(null != developmentIntegrationStandard){
            DevProcessService devProcessService = (DevProcessService) developmentIntegrationStandard.getProcessServices().stream().filter(processService -> processService instanceof DevProcessService).findAny().orElse(null);
            RefQueryOperation refQueryOperation = devProcessService.getRefQueryService().getRefQueryOperation();
            String label = params.get("labels").toString();
            AppInstance appInstance = getAppInstance(developmentIntegrationStandard, label);
            String redirectUrl = appConnService.getRedirectUrl(node.getNodeType(), appInstance);
            devProcessService.setAppInstance(appInstance);
            OpenRequestRef ref = null;
            try {
                ref = (OpenRequestRef)refFactory.newRef(OpenRequestRef.class, refQueryOperation.getClass().getClassLoader(), "com.webank.wedatasphere.dss.appconn." + appConn.getAppDesc().getAppName().toLowerCase());
            } catch (Exception e) {
                logger.error("Failed to create UpdateNodeRequestRef", e);
            }
            ref.setParameter("params", params);
            ref.setParameter("projectId", parseProjectId(node.getProjectId(), appConn.getAppDesc().getAppName(), label));
            try {
                ref.setParameter("node", BDPJettyServerHelper.jacksonJson().readValue(BDPJettyServerHelper.jacksonJson().writeValueAsString(node), Map.class));
            } catch (Exception e) {
                logger.error("Failed to convert node to map", e);
            }
            ref.setParameter("redirectUrl", redirectUrl);
            ResponseRef responseRef = refQueryOperation.query(ref);
            return responseRef.getValue("jumpUrl").toString();
        }
        return null;
    }
}
