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

package com.webank.wedatasphere.dss.workflow.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppInstanceConstants;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationResponse;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefImportService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AppConnRefFactoryUtils;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.NodeInfoMapper;
import com.webank.wedatasphere.dss.workflow.entity.AbstractAppConnNode;
import com.webank.wedatasphere.dss.workflow.entity.NodeGroup;
import com.webank.wedatasphere.dss.workflow.entity.NodeInfo;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowNodeServiceImpl  implements WorkflowNodeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NodeInfoMapper nodeInfoMapper;

    private Sender projectSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender();

    @Override
    public List<NodeGroup> listNodeGroups() {
        //cache
        return nodeInfoMapper.listNodeGroups();
    }


    @Override
    public Map<String, Object> createNode(String userName, AbstractAppConnNode node) throws ExternalOperationFailedException {
        NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(node.getNodeType());
        AppConn appConn = AppConnManager.getAppConnManager().getAppConn(nodeInfo.getAppConnName());

        DevelopmentIntegrationStandard developmentIntegrationStandard =((OnlyDevelopmentAppConn)appConn).getOrCreateDevelopmentStandard();
        if(null != developmentIntegrationStandard)
        {
            String label = node.getJobContent().get(DSSCommonUtils.DSS_LABELS_KEY).toString();
            AppInstance appInstance = getAppInstance(appConn, label);
            RefCreationOperation refCreationOperation = developmentIntegrationStandard.getRefCRUDService(appInstance).getRefCreationOperation();

            Workspace workspace = (Workspace) node.getJobContent().get("workspace");

            NodeRequestRef ref = null;
            try {
                ref = AppConnRefFactoryUtils
                        .newAppConnRef(NodeRequestRef.class, refCreationOperation.getClass().getClassLoader(), appConn.getAppDesc().getAppName().toLowerCase());
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

    private AppInstance getAppInstance(AppConn appConn, String label) {
        AppInstance appInstance = null;
        for (AppInstance instance : appConn.getAppDesc().getAppInstances()) {
            for (DSSLabel dssLabel : instance.getLabels()) {
                if(((EnvDSSLabel)dssLabel).getEnv().equalsIgnoreCase(label)){
                    appInstance = instance;
                    break;
                }
            }
        }
        return appInstance;
    }

    private Long parseProjectId(Long dssProjectId, String appconnName, String labels){
        DSSLabel dssLabel = new EnvDSSLabel(labels);
        ProjectRelationRequest projectRelationRequest = new ProjectRelationRequest(dssProjectId, appconnName, Lists.newArrayList(dssLabel));
        ProjectRelationResponse projectRelationResponse = (ProjectRelationResponse) projectSender.ask(projectRelationRequest);
        return projectRelationResponse.getAppInstanceProjectId();
    }

    @Override
    public void deleteNode(String userName, AbstractAppConnNode node) throws ExternalOperationFailedException {
        NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(node.getNodeType());
        AppConn appConn =  AppConnManager.getAppConnManager().getAppConn(nodeInfo.getAppConnName());
        DevelopmentIntegrationStandard developmentIntegrationStandard =((OnlyDevelopmentAppConn)appConn).getOrCreateDevelopmentStandard();
        if(null != developmentIntegrationStandard)
        {
            String label = node.getJobContent().get(DSSCommonUtils.DSS_LABELS_KEY).toString();
            AppInstance appInstance = getAppInstance(appConn, label);
            RefDeletionOperation refDeletionOperation = developmentIntegrationStandard.getRefCRUDService(appInstance).getRefDeletionOperation();
            Workspace workspace = (Workspace) node.getJobContent().get("workspace");
            NodeRequestRef ref = null;
            try {
                ref = AppConnRefFactoryUtils.newAppConnRefByPackageName(NodeRequestRef.class,
                        appConn.getClass().getClassLoader(), appConn.getClass().getPackage().getName());
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
    public Map<String, Object> updateNode(String userName, AbstractAppConnNode node) throws ExternalOperationFailedException {
        NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(node.getNodeType());
        AppConn appConn =  AppConnManager.getAppConnManager().getAppConn(nodeInfo.getAppConnName());
        DevelopmentIntegrationStandard developmentIntegrationStandard =((OnlyDevelopmentAppConn)appConn).getOrCreateDevelopmentStandard();
        if(null != developmentIntegrationStandard)
        {
            String label = node.getJobContent().get(DSSCommonUtils.DSS_LABELS_KEY).toString();
            AppInstance appInstance = getAppInstance(appConn, label);
            RefUpdateOperation refUpdateOperation = developmentIntegrationStandard.getRefCRUDService(appInstance).getRefUpdateOperation();
            Workspace workspace = (Workspace) node.getJobContent().get("workspace");
            NodeRequestRef ref = null;
            try {
                ref = AppConnRefFactoryUtils.newAppConnRefByPackageName(NodeRequestRef.class,
                        appConn.getClass().getClassLoader(), appConn.getClass().getPackage().getName());
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
    public Map<String, Object> importNode(String userName, AbstractAppConnNode node, Map<String, Object> resourceMap,
        Workspace workspace, String orcVersion) throws Exception {
        NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(node.getNodeType());
        AppConn appConn =  AppConnManager.getAppConnManager().getAppConn(nodeInfo.getAppConnName());
        EnvDSSLabel dssLabel = new EnvDSSLabel(DSSWorkFlowConstant.DSS_IMPORT_ENV.getValue());
        if (appConn != null) {
            DevelopmentIntegrationStandard devStand =((OnlyDevelopmentAppConn)appConn).getOrCreateDevelopmentStandard();
            if (null != devStand) {

                AppInstance appInstance = getAppInstance(appConn, dssLabel.getEnv());

                RefImportService refImportService =  devStand.getRefImportService(appInstance);
                ImportRequestRef requestRef = AppConnRefFactoryUtils.newAppConnRef(ImportRequestRef.class, appConn.getClass().getClassLoader(), appConn.getClass().getPackage().getName());
                //todo request param def
                requestRef.setParameter("jobContent", node.getJobContent());
                requestRef.setParameter("projectId", parseProjectId(node.getProjectId(), appConn.getAppDesc().getAppName(), dssLabel.getEnv()));
                requestRef.setParameter("nodeType", node.getNodeType());
                requestRef.setParameter("orcVersion", orcVersion);
                requestRef.setParameter("user", userName);
                requestRef.getParameters().putAll(resourceMap);
                requestRef.setWorkspace(workspace);
                if (null != refImportService) {
                    ResponseRef responseRef = refImportService.getRefImportOperation().importRef(requestRef);
                    return responseRef.toMap();
                }
            }
        }
        return null;
    }

    @Override
    public String getNodeJumpUrl(Map<String, Object> params, AbstractAppConnNode node) throws ExternalOperationFailedException {
        NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(node.getNodeType());
        AppConn appConn =  AppConnManager.getAppConnManager().getAppConn(nodeInfo.getAppConnName());
        DevelopmentIntegrationStandard developmentIntegrationStandard =((OnlyDevelopmentAppConn)appConn).getOrCreateDevelopmentStandard();
        if(null != developmentIntegrationStandard){
            String label = params.get(DSSCommonUtils.DSS_LABELS_KEY).toString();
            AppInstance appInstance = getAppInstance(appConn, label);
            RefQueryOperation refQueryOperation = developmentIntegrationStandard.getRefQueryService(appInstance).getRefQueryOperation();

            String redirectUrl = (String) appInstance.getConfig().get(AppInstanceConstants.REDIRECT_URL);
            OpenRequestRef ref = null;
            try {
                ref = AppConnRefFactoryUtils.newAppConnRefByPackageName(OpenRequestRef.class, appConn.getClass().getClassLoader(), appConn.getClass().getPackage().getName());
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
