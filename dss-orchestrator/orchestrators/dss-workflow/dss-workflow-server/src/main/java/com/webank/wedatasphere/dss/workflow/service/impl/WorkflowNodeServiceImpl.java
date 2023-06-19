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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlySSOAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectRelationResponse;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.*;
import com.webank.wedatasphere.dss.standard.app.development.ref.*;
import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.app.development.utils.DevelopmentOperationUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.NoSuchAppInstanceException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.FlowMapper;
import com.webank.wedatasphere.dss.workflow.dao.NodeInfoMapper;
import com.webank.wedatasphere.dss.workflow.entity.AbstractAppConnNode;
import com.webank.wedatasphere.dss.workflow.entity.CommonAppConnNode;
import com.webank.wedatasphere.dss.workflow.entity.NodeGroup;
import com.webank.wedatasphere.dss.workflow.entity.NodeInfo;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.cs.client.utils.SerializeHelper;
import org.apache.linkis.cs.common.entity.source.LinkisHAWorkFlowContextID;
import org.apache.linkis.cs.common.utils.CSCommonUtils;
import org.apache.linkis.rpc.Sender;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class WorkflowNodeServiceImpl implements WorkflowNodeService {

    @Autowired
    private NodeInfoMapper nodeInfoMapper;
    @Autowired
    private FlowMapper flowMapper;
    @Autowired
    private WorkFlowParser workFlowParser;
    @Autowired
    private DSSFlowService dssFlowService;

    private Sender projectSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender();

    private static final Logger logger = LoggerFactory.getLogger(WorkflowNodeServiceImpl.class);

    @Override
    public List<NodeGroup> listNodeGroups() {
        //cache
        return nodeInfoMapper.listNodeGroups();
    }

    private RefCRUDService getRefCRUDService(AppConn appConn, List<DSSLabel> dssLabels) {
        return getDevelopmentService(appConn, dssLabels, DevelopmentIntegrationStandard::getRefCRUDService);
    }

    private <T extends DevelopmentService> T getDevelopmentService(AppConn appConn, List<DSSLabel> dssLabels,
                                                                   BiFunction<DevelopmentIntegrationStandard, AppInstance, T> getDevelopmentService) {
        DevelopmentIntegrationStandard developmentIntegrationStandard = ((OnlyDevelopmentAppConn) appConn).getOrCreateDevelopmentStandard();
        if (developmentIntegrationStandard == null) {
            throw new ExternalOperationFailedException(50020, appConn.getAppDesc().getAppName() + " does not exists development standard, please ask admin to check the AppConn.");
        }
        AppInstance appInstance;
        try {
            appInstance = appConn.getAppDesc().getAppInstancesByLabels(dssLabels).get(0);
        } catch (NoSuchAppInstanceException e) {
            if (dssLabels.get(0).getStringValue() == null){
                throw new ExternalOperationFailedException(50020, "未正确退出生产中心或流失生产中，请刷新页面后重试！");
            }
            throw new ExternalOperationFailedException(50020, "Cannot find the appInstance with label " + dssLabels.get(0).getStringValue() +
                    ". (在" + dssLabels.get(0).getStringValue() + "中心找不到" + appConn.getAppDesc().getAppName() + "实例)");
        }
        return getDevelopmentService.apply(developmentIntegrationStandard, appInstance);
    }

    @Override
    public Map<String, Object> createNode(String userName, CommonAppConnNode node) throws ExternalOperationFailedException {
        RefJobContentResponseRef responseRef = tryNodeOperation(userName, node, this::getRefCRUDService,
                developmentService -> ((RefCRUDService) developmentService).getRefCreationOperation(),
                (developmentOperation, developmentRequestRef) ->
                        ((RefCreationOperation) developmentOperation).createRef((DSSJobContentRequestRef) developmentRequestRef)
                , (developmentRequestRef, refJobContentResponseRef) -> {
                    if (developmentRequestRef instanceof ProjectRefRequestRef) {
                        Long projectRefId = ((ProjectRefRequestRef) developmentRequestRef).getRefProjectId();
                        refJobContentResponseRef.getRefJobContent().put(DSSWorkFlowConstant.REF_PROJECT_ID_KEY, projectRefId);
                    }
                }, "create");
        return responseRef.getRefJobContent();
    }

    private <K extends DevelopmentRequestRef, V extends ResponseRef> V tryNodeOperation(String userName, CommonAppConnNode node,
                                                                                        BiFunction<AppConn, List<DSSLabel>, DevelopmentService> developmentServiceFunction,
                                                                                        Function<DevelopmentService, DevelopmentOperation> developmentOperationFunction,
                                                                                        BiFunction<DevelopmentOperation, K, V> requestRefOperationFunction,
                                                                                        BiConsumer<K, V> responseRefConsumer,
                                                                                        String operation) {
        NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(node.getNodeType());
        if(nodeInfo==null){
            String msg = String.format("%s note type not exist,please check appconn install successfully", node.getNodeType());
            logger.error(msg);
            throw new DSSRuntimeException(msg);
        }
        AppConn appConn = AppConnManager.getAppConnManager().getAppConn(nodeInfo.getAppConnName());
        if(appConn==null){
            String msg = String.format("%s appconn not exist,please check appconn install successfully", node.getNodeType());
            logger.error(msg);
            throw new DSSRuntimeException(msg);
        }
        String name;
        if (StringUtils.isBlank(node.getName())) {
            name = node.getJobContent().get(DSSWorkFlowConstant.TITLE_KEY).toString();
        } else {
            name = node.getName();
        }
        if(node.getProjectId() == null || node.getProjectId() <= 0) {
            DSSFlow dssFlow = dssFlowService.getFlow(node.getFlowId());
            node.setProjectId(dssFlow.getProjectId());
        }
        return DevelopmentOperationUtils.tryDevelopmentOperation(() -> developmentServiceFunction.apply(appConn, node.getDssLabels()),
                developmentOperationFunction,
                dssJobContentRequestRef -> {
                    dssJobContentRequestRef.setDSSJobContent(new HashMap<>());
                    String orcVersion;
                    try {
                        orcVersion = getOrcVersion(node);
                    } catch (Exception e) {
                        throw new ExternalOperationFailedException(50205, "Get workflow version failed." + e.getMessage(), e);
                    }
                    if(node.getParams() != null) {
                        dssJobContentRequestRef.getDSSJobContent().putAll(node.getParams());
                    }
                    dssJobContentRequestRef.getDSSJobContent().put(DSSJobContentConstant.ORC_VERSION_KEY, orcVersion);
                    dssJobContentRequestRef.getDSSJobContent().put(DSSJobContentConstant.ORCHESTRATION_ID, node.getFlowId());
                    dssJobContentRequestRef.getDSSJobContent().put(DSSJobContentConstant.ORCHESTRATION_NAME, node.getFlowName());
                },
                refJobContentRequestRef -> {
                    refJobContentRequestRef.setRefJobContent(node.getJobContent());
                    if ("query".equals(operation)) {
                        try {
                            Map runtimeParams = getRuntimeParams(node);
                            refJobContentRequestRef.getRefJobContent().put("runtime",runtimeParams);
                        } catch (IOException e) {
                            throw new ExternalOperationFailedException(50205, "Get workflow runtimeParams failed." + e.getMessage(), e);
                        }
                    }

                    if (refJobContentRequestRef instanceof QueryJumpUrlRequestRef) {
                        ((QueryJumpUrlRequestRef) refJobContentRequestRef).setSSOUrlBuilderOperation(getSSOUrlBuilderOperation(appConn, node.getWorkspace()));
                    }
                },
                dssContextRequestRef -> dssContextRequestRef.setContextId(node.getContextId()),
                projectRefRequestRef -> {
                    Long refProjectId;
                    //todo 第一次导入时用的是dev的refProjectId
//                    if (node.getJobContent().containsKey(DSSWorkFlowConstant.REF_PROJECT_ID_KEY)) {
//                        refProjectId = DSSCommonUtils.parseToLong(node.getJobContent().get(DSSWorkFlowConstant.REF_PROJECT_ID_KEY));
//                    } else {
                    refProjectId = parseProjectId(node.getProjectId(), appConn.getAppDesc().getAppName(), node.getDssLabels());
//                    }
                    projectRefRequestRef.setDSSProjectId(node.getProjectId()).setRefProjectId(refProjectId).setProjectName(node.getProjectName());
                },
                (developmentOperation, developmentRequestRef) -> {
                    developmentRequestRef.setDSSLabels(node.getDssLabels()).setUserName(userName).setWorkspace(node.getWorkspace()).setName(name).setType(node.getNodeType());
                    return requestRefOperationFunction.apply(developmentOperation, (K) developmentRequestRef);
                }, responseRefConsumer, appConn.getAppDesc().getAppName() + " try to " + operation + " workflow node " + name);
    }

    @SuppressWarnings("all")
    private Map getRuntimeParams(CommonAppConnNode commonAppConnNode) throws IOException{
        if (commonAppConnNode.getFlowId() == null) {
            return new LinkedHashMap();
        }
        DSSFlow dssFlow = dssFlowService.getFlow(commonAppConnNode.getFlowId());
        Map<String, Object> flowJsonObject = BDPJettyServerHelper.jacksonJson().readValue(dssFlow.getFlowJson(), Map.class);
        final ArrayList<LinkedHashMap> nodes = (ArrayList<LinkedHashMap>) flowJsonObject.get("nodes");
        final Optional<LinkedHashMap> first = nodes.stream().filter(map -> commonAppConnNode.getJobContent().get("title").toString().equals(map.get("title"))).findFirst();
        if (!first.isPresent()) return new LinkedHashMap();
        final LinkedHashMap node = first.get();
        final LinkedHashMap params = (LinkedHashMap) node.get("params");
        if (params == null) return new LinkedHashMap();
        final LinkedHashMap configuration = (LinkedHashMap) params.get("configuration");
        if (configuration == null) return new LinkedHashMap();
        final LinkedHashMap runtime = (LinkedHashMap) configuration.get("runtime");
        return runtime == null ? new LinkedHashMap() : runtime;
    }

    private Long parseProjectId(Long dssProjectId, String appConnName, List<DSSLabel> dssLabels) {
        ProjectRelationRequest projectRelationRequest = new ProjectRelationRequest(dssProjectId, appConnName, dssLabels);
        ProjectRelationResponse projectRelationResponse = RpcAskUtils.processAskException(projectSender.ask(projectRelationRequest), ProjectRelationResponse.class, ProjectRelationRequest.class);
        return projectRelationResponse.getAppInstanceProjectId();
    }

    @Override
    public void deleteNode(String userName, CommonAppConnNode node) throws ExternalOperationFailedException {
        tryNodeOperation(userName, node, this::getRefCRUDService, developmentService -> ((RefCRUDService) developmentService).getRefDeletionOperation(),
                (developmentOperation, developmentRequestRef) -> ((RefDeletionOperation) developmentOperation).deleteRef((RefJobContentRequestRef) developmentRequestRef), null, "delete");
    }

    @Override
    public Map<String, Object> updateNode(String userName, CommonAppConnNode node) throws ExternalOperationFailedException {
        tryNodeOperation(userName, node, this::getRefCRUDService, developmentService -> ((RefCRUDService) developmentService).getRefUpdateOperation(),
                (developmentOperation, developmentRequestRef) -> ((RefUpdateOperation) developmentOperation).updateRef((UpdateRequestRef) developmentRequestRef), null, "update");
        return node.getJobContent();
    }

    @Override
    public Map<String, Object> refresh(String userName, CommonAppConnNode node) {
        return null;
    }

    @Override
    public Map<String, Object> copyNode(String userName, CommonAppConnNode newNode,
                                        CommonAppConnNode oldNode, String orcVersion) throws IOException, DSSErrorException {
        if (StringUtils.isBlank(orcVersion)) {
            orcVersion = getOrcVersion(oldNode);
        }
        String finalOrcVersion = orcVersion;
        RefJobContentResponseRef responseRef = tryNodeOperation(userName, oldNode,
                this::getRefCRUDService, developmentService -> ((RefCRUDService) developmentService).getRefCopyOperation(),
                (developmentOperation, developmentRequestRef) -> {
                    CopyRequestRef copyRequestRef = (CopyRequestRef) developmentRequestRef;
                    copyRequestRef.setNewVersion(finalOrcVersion).setName(newNode.getName());
                    return ((RefCopyOperation) developmentOperation).copyRef(copyRequestRef);
                }, null, "copy");
        return responseRef.getRefJobContent();
    }

    @Override
    public void setNodeReadOnly(String userName, CommonAppConnNode node) {

    }

    @Override
    public List<AbstractAppConnNode> listNodes(String userName, CommonAppConnNode node) {
        return null;
    }

    @Override
    public ExportResponseRef exportNode(String userName, CommonAppConnNode node) {
        return tryNodeOperation(userName,
                node,
                (appConn, dssLabels) -> getDevelopmentService(appConn, dssLabels, DevelopmentIntegrationStandard::getRefExportService),
                developmentService -> ((RefExportService) developmentService).getRefExportOperation(),
                (developmentOperation, developmentRequestRef) -> ((RefExportOperation) developmentOperation).exportRef((RefJobContentRequestRef) developmentRequestRef),
                null,
                "export");
    }

    @Override
    public Map<String, Object> importNode(String userName, CommonAppConnNode node,
                                          Supplier<Map<String, Object>> getBmlResourceMap,
                                          Supplier<Map<String, Object>> getStreamResourceMap,
                                          String orcVersion) {
        RefJobContentResponseRef responseRef = tryNodeOperation(userName, node,
                (appConn, dssLabels) -> getDevelopmentService(appConn, dssLabels, DevelopmentIntegrationStandard::getRefImportService),
                developmentService -> ((RefImportService) developmentService).getRefImportOperation(),
                (developmentOperation, developmentRequestRef) -> {
                    ImportRequestRef importRequestRef = (ImportRequestRef) developmentRequestRef;
                    if (importRequestRef.isLinkisBMLResources()) {
                        importRequestRef.setResourceMap(getBmlResourceMap.get());
                    } else {
                        importRequestRef.setResourceMap(getStreamResourceMap.get());
                    }
                    importRequestRef.setNewVersion(orcVersion).setName(node.getName());
                    return ((RefImportOperation) developmentOperation).importRef(importRequestRef);
                }, null, "import");
        return responseRef.getRefJobContent();
    }

    @Override
    public String getNodeJumpUrl(Map<String, Object> params, CommonAppConnNode node, String userName) throws ExternalOperationFailedException {
        ResponseRef responseRef = tryNodeOperation(userName, node,
                (appConn, dssLabels) -> getDevelopmentService(appConn, dssLabels, DevelopmentIntegrationStandard::getRefQueryService),
                developmentService -> ((RefQueryService) developmentService).getRefQueryOperation(),
                (developmentOperation, developmentRequestRef) ->
                        ((RefQueryOperation) developmentOperation).query((RefJobContentRequestRef) developmentRequestRef)
                , null, "query");
        if (responseRef instanceof QueryJumpUrlResponseRef) {
            return ((QueryJumpUrlResponseRef) responseRef).getJumpUrl();
        } else {
            throw new ExternalOperationFailedException(50025, "AppConn " + node.getNodeType() + " don't support to get jumpUrl!");
        }
    }

    private String getOrcVersion(CommonAppConnNode node) throws IOException {
        if (node.getFlowId() == null) {
            throw new NullPointerException("The flowId is null, please ask admin for help!");
        }
        DSSFlow dssFlow = dssFlowService.getFlow(node.getFlowId());
        if (StringUtils.isBlank(node.getFlowName())) {
            node.setFlowName(dssFlow.getName());
        }
        String version = workFlowParser.getValueWithKey(dssFlow.getFlowJson(), DSSJobContentConstant.ORC_VERSION_KEY);
        //兼容老的flowJson外层没有orcVersion字段的情况，需要从contextId中获取
        if (version == null) {
            try {
                JsonObject jsonObject = new Gson().fromJson(dssFlow.getFlowJson(), JsonObject.class);
                LinkisHAWorkFlowContextID contextID = (LinkisHAWorkFlowContextID) SerializeHelper
                        .deserializeContextID(jsonObject.get(CSCommonUtils.CONTEXT_ID_STR).getAsString());
                version = contextID.getVersion();
            } catch (ErrorException e) {
                logger.error("Invalid contextID, please contact with administrator: ", e);
            }
        }
        return version;
    }

    private SSOUrlBuilderOperation getSSOUrlBuilderOperation(AppConn appConn, Workspace workspace) {
        if (!(appConn instanceof OnlySSOAppConn)) {
            return null;
        }
        SSOUrlBuilderOperation ssoUrlBuilderOperation = ((OnlySSOAppConn) appConn).getOrCreateSSOStandard().getSSOBuilderService().createSSOUrlBuilderOperation();
        ssoUrlBuilderOperation.setAppName(appConn.getAppDesc().getAppName());
        SSOHelper.setSSOUrlBuilderOperation(ssoUrlBuilderOperation, workspace);
        return ssoUrlBuilderOperation;
    }
}
