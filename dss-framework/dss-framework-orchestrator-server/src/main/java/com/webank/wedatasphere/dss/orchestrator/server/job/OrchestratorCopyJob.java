package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.publish.utils.OrchestrationDevelopmentOperationUtils;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorCopyVo;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.*;

public class OrchestratorCopyJob implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorCopyJob.class);

    private OrchestratorCopyVo orchestratorCopyVo;

    protected OrchestratorCopyEnv orchestratorCopyEnv;

    private DSSOrchestratorCopyInfo orchestratorCopyInfo = new DSSOrchestratorCopyInfo(UUID.randomUUID().toString());


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
                "Orchestrator server", 1, new Date(), new Date(), orchestratorCopyVo.getInstanceName());
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
            if (errorMsg.length() > 128) {
                errorMsg = errorMsg.substring(0, 127);
            }
            orchestratorCopyInfo.setIsCopying(0);
            orchestratorCopyInfo.setEndTime(new Date());
            orchestratorCopyInfo.setSuccessNode(Lists.newArrayList("ZERO"));
            orchestratorCopyInfo.setStatus(0);
            orchestratorCopyInfo.setExceptionInfo(errorMsg);
            orchestratorCopyEnv.getOrchestratorCopyJobMapper().updateErrorMsgById(orchestratorCopyInfo);
            LOGGER.error("copy orc error: sourceProjectName:{},targetProjectName:{}, sourceOrchestratorName:{}, targetOrchestratorName:{}. Exception:",
                    orchestratorCopyVo.getSourceProjectName(), orchestratorCopyVo.getTargetProjectName(), sourceOrchestrator.getName(), orchestratorCopyVo.getTargetOrchestratorName(), e);
            throw new RuntimeException("error happened when copying orc.", e);
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
                    refJobContent.put(OrchestratorRefConstant.ORCHESTRATION_NODE_SUFFIX, orchestratorCopyVo.getWorkflowNodeSuffix());
                    requestRef.setNewVersion(dssOrchestratorVersion.getVersion()).setRefJobContent(refJobContent);
                    return ((RefCopyOperation) developmentOperation).copyRef(requestRef);
                }, "copy");
        dssOrchestratorVersion.setAppId((Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY));
        dssOrchestratorVersion.setContent((String) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY));
        List<String[]> paramConfTemplateIds=(List<String[]>) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_FLOWID_PARAMCONF_TEMPLATEID_TUPLES_KEY);
        orchestratorCopyEnv.getOrchestratorMapper().addOrchestrator(dssOrchestratorInfo);
        dssOrchestratorVersion.setOrchestratorId(dssOrchestratorInfo.getId());
        orchestratorCopyEnv.getOrchestratorMapper().addOrchestratorVersion(dssOrchestratorVersion);
        orchestratorCopyEnv.getAddOrchestratorVersionHook().afterAdd(dssOrchestratorVersion, Collections.singletonMap(OrchestratorRefConstant.ORCHESTRATION_FLOWID_PARAMCONF_TEMPLATEID_TUPLES_KEY,paramConfTemplateIds));
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
