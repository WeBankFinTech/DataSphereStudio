package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.protocol.RequestQueryWorkFlow;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestImportOrchestrator;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.publish.entity.OrchestratorExportResult;
import com.webank.wedatasphere.dss.orchestrator.publish.utils.OrchestrationDevelopmentOperationUtils;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorCopyVo;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseQueryWorkflow;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.*;

public class OrchestratorCopyJob implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorCopyJob.class);

    private OrchestratorCopyVo orchestratorCopyVo;

    protected OrchestratorCopyEnv orchestratorCopyEnv;

    private DSSOrchestratorCopyInfo orchestratorCopyInfo = new DSSOrchestratorCopyInfo(UUID.randomUUID().toString());

    private final Sender workflowSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender();

    @Override
    public void run() {
        try {
            copyOrchestrator();
        } catch (Exception e) {
            LOGGER.error("Copy {} for {} error ", orchestratorCopyVo.getOrchestrator().getName(), e);
        }
    }

    private void copyOrchestrator() {
        //开始写入复制信息到编排复制任务历史表
        DSSOrchestratorInfo sourceOrchestrator = orchestratorCopyVo.getOrchestrator();
        orchestratorCopyInfo = new DSSOrchestratorCopyInfo(orchestratorCopyInfo.getId(), orchestratorCopyVo.getUsername(), sourceOrchestrator.getType(), orchestratorCopyVo.getWorkspace().getWorkspaceId(),
                sourceOrchestrator.getId(), sourceOrchestrator.getName(), orchestratorCopyVo.getTargetOrchestratorName(),
                orchestratorCopyVo.getSourceProjectName(), orchestratorCopyVo.getTargetProjectName(), orchestratorCopyVo.getWorkflowNodeSuffix(),
                "Orchestrator server", 1, new Date(), new Date());
        orchestratorCopyEnv.getOrchestratorCopyJobMapper().insertOrchestratorCopyInfo(orchestratorCopyInfo);
        DSSOrchestratorVersion latestOrcVersion = orchestratorCopyEnv.getOrchestratorMapper().getLatestOrchestratorVersionById(sourceOrchestrator.getId());
        Long appId = latestOrcVersion.getAppId();
        DSSOrchestratorInfo newOrchestrator = new DSSOrchestratorInfo();
        BeanUtils.copyProperties(sourceOrchestrator, newOrchestrator);
        newOrchestrator.setId(null);
        newOrchestrator.setName(orchestratorCopyVo.getTargetOrchestratorName());
        newOrchestrator.setProjectId(orchestratorCopyVo.getTargetProjectId());
        newOrchestrator.setCreateTime(new Date());
        newOrchestrator.setCreator(orchestratorCopyVo.getUsername());
        newOrchestrator.setUUID(UUID.randomUUID().toString());
        newOrchestrator.setDesc("copy from " + sourceOrchestrator.getName());
        newOrchestrator.setUpdateTime(null);
        newOrchestrator.setUpdateUser(null);

        try {
            doOrchestratorCopy(orchestratorCopyVo.getUsername(), orchestratorCopyVo.getWorkspace(), newOrchestrator,
                    orchestratorCopyVo.getTargetProjectName(), Lists.newArrayList(orchestratorCopyVo.getDssLabel()), appId);
        } catch (Exception e) {
            //保存错误信息
            String errorMsg = "CopyOrcError: " + e.getMessage();
            if (errorMsg.length() > 1000) {
                errorMsg = errorMsg.substring(0, 999);
            }
            orchestratorCopyInfo.setIsCopying(0);
            orchestratorCopyInfo.setEndTime(new Date());
            orchestratorCopyInfo.setSuccessNode(Lists.newArrayList("ZERO"));
            orchestratorCopyInfo.setStatus(0);
            orchestratorCopyInfo.setExceptionInfo(errorMsg);
            orchestratorCopyEnv.getOrchestratorCopyJobMapper().updateErrorMsgById(orchestratorCopyInfo);
            LOGGER.error("copy orc error: sourceProjectName:{},targetProjectName:{}, sourceOrchestratorName:{}, targetOrchestratorName:{}. Exception:",
                    orchestratorCopyVo.getSourceProjectName(), orchestratorCopyVo.getTargetProjectName(), sourceOrchestrator.getName(), orchestratorCopyVo.getTargetOrchestratorName(), e);
            throw new RuntimeException("error happend when copying orc.", e);
        }
        orchestratorCopyInfo.setIsCopying(0);
        orchestratorCopyInfo.setEndTime(new Date());
        orchestratorCopyInfo.setSuccessNode(Lists.newArrayList("All"));
        orchestratorCopyInfo.setStatus(1);
        orchestratorCopyEnv.getOrchestratorCopyJobMapper().updateCopyStatus(orchestratorCopyInfo);
    }

    private void doOrchestratorCopy(String userName,
                                    Workspace workspace,
                                    DSSOrchestratorInfo dssOrchestratorInfo,
                                    String projectName,
                                    List<DSSLabel> dssLabels, Long appId) throws DSSErrorException {
        String copyInitVersion = OrchestratorUtils.generateNewCopyVersion(orchestratorCopyVo.getWorkflowNodeSuffix());
        String contextId = orchestratorCopyEnv.getContextService().createContextID(workspace.getWorkspaceName(), projectName, dssOrchestratorInfo.getName(), copyInitVersion, userName);
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setComment("create with orchestrator copy");
        dssOrchestratorVersion.setProjectId(orchestratorCopyVo.getTargetProjectId());
        dssOrchestratorVersion.setSource("Orchestrator copy");
        dssOrchestratorVersion.setUpdater(userName);
        dssOrchestratorVersion.setVersion(copyInitVersion);
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setValidFlag(1);
        dssOrchestratorVersion.setFormatContextId(contextId);
        LOGGER.info("Create a new ContextId {} for orchestrator copy operation with name:{},version{}.", contextId, dssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion());
        DSSOrchestrator dssOrchestrator = orchestratorCopyEnv.getOrchestratorManager().getOrCreateOrchestrator(userName, workspace.getWorkspaceName(), dssOrchestratorInfo.getType(),
                dssLabels);
        RefJobContentResponseRef responseRef = OrchestrationDevelopmentOperationUtils.tryOrchestrationOperation(dssOrchestratorInfo, dssOrchestrator, userName,
                workspace, dssLabels, DevelopmentIntegrationStandard::getRefCRUDService,
                developmentService -> ((RefCRUDService) developmentService).getRefCopyOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setProjectName(projectName).setRefProjectId(dssOrchestratorVersion.getProjectId()),
                (developmentOperation, developmentRequestRef) -> {
                    CopyRequestRef requestRef = (CopyRequestRef) developmentRequestRef;
                    Map<String, Object> refJobContent = MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, appId,
                            OrchestratorRefConstant.ORCHESTRATION_DESCRIPTION, dssOrchestratorVersion.getComment());
                    refJobContent.put(OrchestratorRefConstant.ORCHESTRATION_NAME, dssOrchestratorInfo.getName());
                    refJobContent.put("nodeSuffix", orchestratorCopyVo.getWorkflowNodeSuffix());
                    requestRef.setNewVersion(dssOrchestratorVersion.getVersion()).setRefJobContent(refJobContent);
                    return ((RefCopyOperation) developmentOperation).copyRef(requestRef);
                }, "copy");
        dssOrchestratorVersion.setAppId((Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY));
        dssOrchestratorVersion.setContent((String) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY));

        orchestratorCopyEnv.getOrchestratorMapper().addOrchestrator(dssOrchestratorInfo);
        dssOrchestratorVersion.setOrchestratorId(dssOrchestratorInfo.getId());
        orchestratorCopyEnv.getOrchestratorMapper().addOrchestratorVersion(dssOrchestratorVersion);
    }

    private OrchestratorExportResult exportOrc() throws Exception {
        Long targetProjectId = orchestratorCopyVo.getTargetProjectId();
        String sourceProjectName = orchestratorCopyVo.getSourceProjectName();
        String targetProjectName = orchestratorCopyVo.getTargetProjectName();
        DSSOrchestratorInfo orchestratorInfo = orchestratorCopyVo.getOrchestrator();
        Long sourceOrchestratorId = orchestratorInfo.getId();
        Workspace workspace = orchestratorCopyVo.getWorkspace();
        String username = orchestratorCopyVo.getUsername();
        String targetOrchestratorName = orchestratorCopyVo.getTargetOrchestratorName();

        LOGGER.info("Start to export orchestrator {} of project {}", orchestratorInfo.getName(), sourceProjectName);
        //先判断工作流是否含有节点,如果没有节点直接调用接口创建即可
        DSSOrchestratorVersion sourceOrchestratorVersion = orchestratorCopyEnv.getOrchestratorMapper().getLatestOrchestratorVersionById(sourceOrchestratorId);
        if (isExistNodeInFlow(sourceOrchestratorVersion, username)) {

            OrchestratorCreateRequest orchestratorCreateRequest = new OrchestratorCreateRequest(targetOrchestratorName,
                    Lists.newArrayList(orchestratorInfo.getOrchestratorWay()), orchestratorInfo.getOrchestratorLevel(),
                    Lists.newArrayList(orchestratorCopyVo.getDssLabel().toString()), orchestratorInfo.getUses(), orchestratorInfo.getDescription(),
                    targetProjectName, workspace.getWorkspaceName());
            orchestratorCreateRequest.setProjectId(targetProjectId);
            orchestratorCreateRequest.setOrchestratorMode(orchestratorInfo.getOrchestratorMode());
            orchestratorCreateRequest.setWorkspaceId(workspace.getWorkspaceId());
            orchestratorCopyEnv.getOrchestratorFrameworkService().createOrchestrator(username, orchestratorCreateRequest, workspace);

            return null;
        }
        //如果是含有节点的工作流
        try {
            return orchestratorCopyEnv.getExportDSSOrchestratorPlugin()
                    .exportOrchestrator(username, sourceOrchestratorId, sourceOrchestratorVersion.getId(),
                            sourceProjectName, Lists.newArrayList(new EnvDSSLabel(DSSCommonUtils.ENV_LABEL_VALUE_DEV)), false, workspace);
        } catch (Exception e) {
            //保存错误信息
            String errorMsg = "ExportOrcError: " + e.getMessage();
            if (errorMsg.length() > 1000) {
                errorMsg = errorMsg.substring(0, 999);
            }

            orchestratorCopyInfo.setIsCopying(0);
            orchestratorCopyInfo.setEndTime(new Date());
            orchestratorCopyInfo.setSuccessNode(Lists.newArrayList("ZERO"));
            orchestratorCopyInfo.setStatus(0);
            orchestratorCopyInfo.setExceptionInfo(errorMsg);
            orchestratorCopyEnv.getOrchestratorCopyJobMapper().updateErrorMsgById(orchestratorCopyInfo);
            LOGGER.error("ExportOrcError: sourceProjectName:{},targetProjectName:{}, sourceOrchestratorName:{}, targetOrchestratorName:{}. Exception:",
                    sourceProjectName, targetProjectName, orchestratorInfo.getName(), targetOrchestratorName, e);
            throw new RuntimeException("Export Orc error when copying orc.", e);
        }
    }

    /**
     * 导入编排模式
     *
     * @param exportResult
     * @throws ErrorException
     */
    private void importOrc(OrchestratorExportResult exportResult) throws ErrorException {
        LOGGER.info("Begin to import orc {} for project {} and orc resource is {}", orchestratorCopyVo.getOrchestrator().getName(), orchestratorCopyVo.getSourceProjectName(), exportResult.getBmlResource());
        List<DSSLabel> dssLabelList = Collections.singletonList(orchestratorCopyVo.getDssLabel());
        DSSOrchestratorInfo orchestrator = orchestratorCopyVo.getOrchestrator();
        Workspace workspace = orchestratorCopyVo.getWorkspace();
        BmlResource bmlResource = exportResult.getBmlResource();

        try {
            LOGGER.info("CopyOrchestrator Start: sourceOrchestratorName:{}, targetOrchestratorName:{}", orchestrator.getName(), orchestratorCopyVo.getTargetOrchestratorName());

            // 此处orchestraName使用targetName
            RequestImportOrchestrator importRequest = new RequestImportOrchestrator(orchestratorCopyVo.getUsername(),
                    orchestratorCopyVo.getSourceProjectName(), orchestratorCopyVo.getSourceProjectId(), bmlResource.getResourceId(),
                    bmlResource.getVersion(), orchestratorCopyVo.getTargetOrchestratorName(), dssLabelList, workspace,
                    orchestratorCopyVo.getTargetProjectId(), orchestratorCopyVo.getTargetProjectName());
            Long targetOrchestratorId = orchestratorCopyEnv.getImportDSSOrchestratorPlugin().importCopyOrchestrator(importRequest, orchestratorCopyVo.getWorkflowNodeSuffix());

            LOGGER.info("Copy Orchestrator Success: sourceOrchestratorName:{}, targetOrchestratorName:{}, targetOrchestratorId:{}", orchestrator.getName(), orchestratorCopyVo.getTargetOrchestratorName(), targetOrchestratorId);
        } catch (Exception e) {
            //保存错误信息
            String errorMsg = "ImportOrcError: " + e.getMessage();
            if (errorMsg.length() > 1000) {
                errorMsg = errorMsg.substring(0, 999);
            }

            orchestratorCopyInfo.setIsCopying(0);
            orchestratorCopyInfo.setEndTime(new Date());
            orchestratorCopyInfo.setSuccessNode(Lists.newArrayList("ZERO"));
            orchestratorCopyInfo.setStatus(0);
            orchestratorCopyInfo.setExceptionInfo(errorMsg);

            orchestratorCopyEnv.getOrchestratorCopyJobMapper().updateErrorMsgById(orchestratorCopyInfo);
            LOGGER.error("ImportOrcError: sourceProjectName:{},targetProjectName:{}, sourceOrchestratorName:{}, targetOrchestratorName:{}. Exception:",
                    orchestratorCopyVo.getSourceProjectName(), orchestratorCopyVo.getTargetProjectName(), orchestrator.getName(), orchestratorCopyVo.getTargetOrchestratorName(), e);
            throw new RuntimeException("Import Orc error when copying orc.", e);
        }

        LOGGER.info("Import orchestrator {} of project {} to orchestrator {} of project {}", orchestrator.getName(), orchestratorCopyVo.getSourceProjectName(),
                orchestratorCopyVo.getTargetOrchestratorName(), orchestratorCopyVo.getTargetProjectName());
    }

    private Boolean isExistNodeInFlow(DSSOrchestratorVersion sourceOrchestratorVersion, String username) {

        Long appId = sourceOrchestratorVersion.getAppId();
        RequestQueryWorkFlow requestQueryWorkFlow = new RequestQueryWorkFlow(username, appId);
        ResponseQueryWorkflow responseQueryWorkflow = (ResponseQueryWorkflow) workflowSender.ask(requestQueryWorkFlow);
        Map<String, Object> query = orchestratorCopyEnv.getBmlService().query(username, responseQueryWorkflow.getDssFlow().getResourceId(), responseQueryWorkflow.getDssFlow().getBmlVersion());
        String flowJson = query.get("string").toString();
        List<String> workFlowNodesJsonList = DSSCommonUtils.getWorkFlowNodesJson(flowJson);
        return CollectionUtils.isEmpty(workFlowNodesJsonList);

    }

    public OrchestratorCopyVo getOrchestratorCopyVo() {
        return orchestratorCopyVo;
    }

    public void setOrchestratorCopyVo(OrchestratorCopyVo orchestratorCopyVo) {
        this.orchestratorCopyVo = orchestratorCopyVo;
    }

    public OrchestratorCopyEnv getOrchestratorCopyEnv() {
        return orchestratorCopyEnv;
    }

    public void setOrchestratorCopyEnv(OrchestratorCopyEnv orchestratorCopyEnv) {
        this.orchestratorCopyEnv = orchestratorCopyEnv;
    }

    public DSSOrchestratorCopyInfo getOrchestratorCopyInfo() {
        return orchestratorCopyInfo;
    }

    public void setOrchestratorCopyInfo(DSSOrchestratorCopyInfo orchestratorCopyInfo) {
        this.orchestratorCopyInfo = orchestratorCopyInfo;
    }
}
