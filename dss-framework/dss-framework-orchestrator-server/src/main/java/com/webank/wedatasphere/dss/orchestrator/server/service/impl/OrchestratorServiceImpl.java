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

package com.webank.wedatasphere.dss.orchestrator.server.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.common.constant.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.DSSLabelUtil;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectUserAuthRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectUserAuthResponse;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitCommitRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitRevertRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitRevertResponse;
import com.webank.wedatasphere.dss.git.common.protocol.util.UrlUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestOrchestratorInfos;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestProjectUpdateOrcVersion;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOrchestratorInfos;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.db.hook.AddOrchestratorVersionHook;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.publish.utils.OrchestrationDevelopmentOperationUtils;
import com.webank.wedatasphere.dss.orchestrator.server.conf.OrchestratorConf;
import com.webank.wedatasphere.dss.orchestrator.server.constant.DSSOrchestratorConstant;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorSubmitRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorUnlockVo;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorPluginService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.*;
import com.webank.wedatasphere.dss.standard.app.development.ref.*;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefQueryService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;
import com.webank.wedatasphere.dss.workflow.common.protocol.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.linkis.cs.client.ContextClient;
import org.apache.linkis.cs.client.builder.ContextClientFactory;
import org.apache.linkis.rpc.Sender;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrchestratorServiceImpl implements OrchestratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorServiceImpl.class);
    @Autowired
    private OrchestratorManager orchestratorManager;
    @Autowired
    private OrchestratorMapper orchestratorMapper;
    @Autowired
    private ContextService contextService;
    @Autowired
    AddOrchestratorVersionHook addOrchestratorVersionHook;
    @Autowired
    private OrchestratorPluginService orchestratorPluginService;

    private static final int VALID_FLAG = 1;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrchestratorVo createOrchestrator(String userName,
                                             Workspace workspace,
                                             String projectName,
                                             Long projectId,
                                             String description,
                                             DSSOrchestratorInfo dssOrchestratorInfo,
                                             List<DSSLabel> dssLabels) throws Exception {
        OrchestratorVo orchestratorVo = new OrchestratorVo();
        //todo 增加校验
        String uuid = UUID.randomUUID().toString();

        //作为Orchestrator的唯一标识，包括跨环境导入导出也不发生变化。
        dssOrchestratorInfo.setUUID(uuid);

        String version = OrchestratorUtils.generateNewVersion();
        String contextId = contextService.createContextID(workspace.getWorkspaceName(), projectName, dssOrchestratorInfo.getName(), version, userName);
        LOGGER.info("Create a new ContextId: {} for new orchestrator {}.", contextId, dssOrchestratorInfo.getName());
        //1. 访问DSS工作流微模块创建工作流
        RefJobContentResponseRef appRef = tryRefOperation(dssOrchestratorInfo, userName, workspace, dssLabels, null,
                developmentService -> ((RefCRUDService) developmentService).getRefCreationOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setProjectName(projectName).setRefProjectId(projectId),
                (developmentOperation, developmentRequestRef) -> {
                    DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                            workspace.getWorkspaceName(), dssOrchestratorInfo.getType(), dssLabels);
                    Map<String, Object> dssJobContent = MapUtils.newCommonMapBuilder()
                            .put(OrchestratorRefConstant.DSS_ORCHESTRATOR_INFO_KEY, dssOrchestratorInfo)
                            .put(OrchestratorRefConstant.ORCHESTRATOR_VERSION_KEY, version)
                            .put(OrchestratorRefConstant.ORCHESTRATION_SCHEDULER_APP_CONN, dssOrchestrator.getSchedulerAppConn().getAppDesc().getAppName()).build();
                    DSSJobContentRequestRef requestRef = (DSSJobContentRequestRef) developmentRequestRef;
                    requestRef.setDSSJobContent(dssJobContent);
                    return ((RefCreationOperation) developmentOperation).createRef(requestRef);
                }, "create");

        //2.往数据库插入一条DSS编排模式
        orchestratorMapper.addOrchestrator(dssOrchestratorInfo);

        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setOrchestratorId(dssOrchestratorInfo.getId());
        dssOrchestratorVersion.setAppId((Long) appRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY));
        dssOrchestratorVersion.setContent((String) appRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY));
        dssOrchestratorVersion.setComment(description);
        dssOrchestratorVersion.setProjectId(projectId);
        dssOrchestratorVersion.setSource("Orchestrator create");
        dssOrchestratorVersion.setUpdater(userName);
        dssOrchestratorVersion.setVersion(version);
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setValidFlag(VALID_FLAG);
        dssOrchestratorVersion.setFormatContextId(contextId);
        // 4. 持久化编排的版本信息
        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
        orchestratorVo.setDssOrchestratorInfo(dssOrchestratorInfo);
        orchestratorVo.setDssOrchestratorVersion(dssOrchestratorVersion);

        return orchestratorVo;
    }


    private <K extends DevelopmentRequestRef, V extends ResponseRef> V tryRefOperation(DSSOrchestratorInfo dssOrchestratorInfo,
                                                                                       String userName,
                                                                                       Workspace workspace,
                                                                                       List<DSSLabel> dssLabels,
                                                                                       BiFunction<DevelopmentIntegrationStandard, AppInstance, DevelopmentService> getDevelopmentService,
                                                                                       Function<DevelopmentService, DevelopmentOperation> getOperation,
                                                                                       Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                                                       Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                                                       BiFunction<DevelopmentOperation, K, V> responseRefConsumer,
                                                                                       String operationName) throws DSSErrorException {
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspace.getWorkspaceName(), dssOrchestratorInfo.getType(), dssLabels);
        dssOrchestratorInfo.setAppConnName(dssOrchestrator.getAppConn().getAppDesc().getAppName());
        if (getDevelopmentService == null) {
            getDevelopmentService = DevelopmentIntegrationStandard::getRefCRUDService;
        }
        return OrchestrationDevelopmentOperationUtils.tryOrchestrationOperation(dssOrchestratorInfo, dssOrchestrator,
                userName, workspace, dssLabels, getDevelopmentService, getOperation, contextRequestRefConsumer, projectRefRequestRefConsumer,
                responseRefConsumer, operationName);
    }

    @Override
    public void updateOrchestrator(String userName,
                                   Workspace workspace,
                                   DSSOrchestratorInfo dssOrchestratorInfo,
                                   List<DSSLabel> dssLabels) throws Exception {
        orchestratorMapper.updateOrchestrator(dssOrchestratorInfo);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(dssOrchestratorInfo.getId(), VALID_FLAG);

        tryRefOperation(dssOrchestratorInfo, userName, workspace, dssLabels, null,
                developmentService -> ((RefCRUDService) developmentService).getRefUpdateOperation(),
                null,
                projectRefRequestRef -> projectRefRequestRef.setRefProjectId(dssOrchestratorInfo.getProjectId()),
                (developmentOperation, developmentRequestRef) -> {
                    UpdateRequestRef requestRef = (UpdateRequestRef) developmentRequestRef;
                    Map<String, Object> dssJobContent = MapUtils.newCommonMapBuilder()
                            .put(OrchestratorRefConstant.ORCHESTRATION_NAME, dssOrchestratorInfo.getName())
                            .put(OrchestratorRefConstant.ORCHESTRATION_DESCRIPTION, dssOrchestratorInfo.getComment())
                            .put(OrchestratorRefConstant.ORCHESTRATION_USES, dssOrchestratorInfo.getUses()).build();
                    requestRef.setDSSJobContent(dssJobContent);
                    requestRef.setRefJobContent(MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dssOrchestratorVersion.getAppId(),
                            OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, dssOrchestratorInfo.getId()));
                    return ((RefUpdateOperation) developmentOperation).updateRef(requestRef);
                }, "update");
    }

    @Override
    public void deleteOrchestrator(String userName,
                                   Workspace workspace,
                                   String projectName,
                                   Long orchestratorInfoId,
                                   List<DSSLabel> dssLabels) throws Exception {
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorInfoId);
        if (null == dssOrchestratorInfo) {
            LOGGER.error("Not exists orchestration {} in project {}.", orchestratorInfoId, projectName);
            DSSExceptionUtils.dealErrorException(61123,
                    String.format("Not exists orchestration %s in project %s.", orchestratorInfoId, projectName),
                    DSSErrorException.class);
        }
        orchestratorMapper.getOrchestratorVersions(dssOrchestratorInfo.getProjectId(), orchestratorInfoId).forEach(dssOrchestratorVersion -> {
            LOGGER.info("user {} try to delete the DSS project {} orchestration(such as DSS workflow) {} of orchestrator {} in version {}.",
                    userName, projectName, dssOrchestratorVersion.getAppId(), dssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion());
            try {
                tryRefOperation(dssOrchestratorInfo, userName, workspace, dssLabels, null,
                        developmentService -> ((RefCRUDService) developmentService).getRefDeletionOperation(),
                        null, projectRefRequestRef -> projectRefRequestRef.setRefProjectId(dssOrchestratorInfo.getProjectId()),
                        (developmentOperation, developmentRequestRef) -> {
                            RefJobContentRequestRef requestRef = (RefJobContentRequestRef) developmentRequestRef;
                            Map<String, Object> refJobContent = MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dssOrchestratorVersion.getAppId(),
                                    OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, orchestratorInfoId);
                            requestRef.setRefJobContent(refJobContent);
                            return ((RefDeletionOperation) developmentOperation).deleteRef(requestRef);
                        }, "delete");
            } catch (DSSErrorException e) {
                throw new ExternalOperationWarnException(e.getErrCode(), e.getMessage(), e);
            }
            orchestratorMapper.deleteOrchestratorVersion(dssOrchestratorVersion.getId());
        });
        orchestratorMapper.deleteOrchestrator(orchestratorInfoId);
    }

    @Override
    public OrchestratorUnlockVo unlockOrchestrator(String userName,
                                                   Workspace workspace,
                                                   String projectName,
                                                   Long orchestratorInfoId,
                                                   Boolean confirmDelete,
                                                   List<DSSLabel> dssLabels) throws DSSErrorException {
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorInfoId);
        if (null == dssOrchestratorInfo) {
            LOGGER.error("Not exists orchestration {} in project {}.", orchestratorInfoId, projectName);
            DSSExceptionUtils.dealErrorException(61123,
                    String.format("Not exists orchestration %s in project %s.", orchestratorInfoId, projectName), DSSErrorException.class);
        }
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orchestratorInfoId, VALID_FLAG);
        LOGGER.info("user {} try to unlock the project {} 's orchestration(such as DSS workflow) {} of orchestrator {} in version {}.",
                userName, projectName, dssOrchestratorVersion.getAppId(), dssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion());
        RequestUnlockWorkflow requestUnlockWorkflow = new RequestUnlockWorkflow(userName, dssOrchestratorVersion.getAppId(), confirmDelete);
        ResponseUnlockWorkflow responseUnlockWorkflow = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance()
                .getWorkflowSender(dssLabels).ask(requestUnlockWorkflow), ResponseUnlockWorkflow.class, RequestUnlockWorkflow.class);
        switch (responseUnlockWorkflow.getUnlockStatus()) {
            case ResponseUnlockWorkflow.NONEED_UNLOCK:
                //DSSExceptionUtils.dealErrorException(62001, String.format("解锁失败，当前工作流未被锁定：%s", dssOrchestratorInfo.getName()), DSSErrorException.class);
                return new OrchestratorUnlockVo(null, null, 0);
            case ResponseUnlockWorkflow.NEED_SECOND_CONFIRM:
                String lockOwner = responseUnlockWorkflow.getLockOwner();
                String confirmMsg = String.format("当前工作流已被%s锁定编辑，强制解锁工作流会导致%s已编辑的内容无法保存，请与%s确认后再解锁工作流。", lockOwner, lockOwner, lockOwner);
                return new OrchestratorUnlockVo(lockOwner, confirmMsg, 1);
            case ResponseUnlockWorkflow.UNLOCK_SUCCESS:
                return new OrchestratorUnlockVo(null, "解锁成功", 0);
            default:
                DSSExceptionUtils.dealErrorException(62003, "unknown unlockStatus", DSSErrorException.class);
                return null;
        }
    }

    @Override
    public List<OrchestratorVo> getOrchestratorVoList(List<Long> orchestratorIds) {
        List<OrchestratorVo> orchestratorVoList = new ArrayList<>();
        orchestratorIds.forEach(orchestratorId -> {
            OrchestratorVo orchestratorVo = getOrchestratorVoById(orchestratorId);
            orchestratorVoList.add(orchestratorVo);
        });
        return orchestratorVoList;
    }

    @Override
    public String openOrchestrator(String userName, Workspace workspace, Long orchestratorId, List<DSSLabel> dssLabels) throws Exception {
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orchestratorId, VALID_FLAG);
        if (null == dssOrchestratorInfo || null == dssOrchestratorVersion) {
            throw new DSSOrchestratorErrorException(1000856, "can not find orc from db for orcId: " + orchestratorId);
        }
        QueryJumpUrlResponseRef responseRef = (QueryJumpUrlResponseRef) tryRefOperation(dssOrchestratorInfo, userName,
                workspace, dssLabels, DevelopmentIntegrationStandard::getRefQueryService,
                developmentService -> ((RefQueryService) developmentService).getRefQueryOperation(),
                null,
                projectRefRequestRef -> projectRefRequestRef.setRefProjectId(dssOrchestratorInfo.getProjectId()),
                (developmentOperation, developmentRequestRef) -> {
                    RefJobContentRequestRef requestRef = (RefJobContentRequestRef) developmentRequestRef;
                    requestRef.setRefJobContent(MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dssOrchestratorVersion.getAppId(),
                            OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, orchestratorId));
                    return ((RefQueryOperation) developmentOperation).query(requestRef);
                }, "open");

        return responseRef.getJumpUrl();
    }

    @Override
    public OrchestratorVo getOrchestratorVoById(Long orchestratorId) {

        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orchestratorId, VALID_FLAG);

        OrchestratorVo orchestratorVo = new OrchestratorVo();
        orchestratorVo.setDssOrchestratorInfo(dssOrchestratorInfo);
        orchestratorVo.setDssOrchestratorVersion(dssOrchestratorVersion);
        return orchestratorVo;
    }

    @Override
    public OrchestratorVo getOrchestratorVoByIdAndOrcVersionId(Long orchestratorId, Long orcVersionId) {
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getOrcVersionByIdAndOrcVersionId(orchestratorId, orcVersionId, VALID_FLAG);

        OrchestratorVo orchestratorVo = new OrchestratorVo();
        orchestratorVo.setDssOrchestratorInfo(dssOrchestratorInfo);
        orchestratorVo.setDssOrchestratorVersion(dssOrchestratorVersion);
        return orchestratorVo;
    }

    @Override
    public List<DSSOrchestratorVersion> getVersionByOrchestratorId(Long orchestratorId) {
        return orchestratorMapper.getVersionByOrchestratorId(orchestratorId);
    }

    @Override
    public List<DSSOrchestratorVersion> getOrchestratorVersions(String username, Long projectId, Long orchestratorId) {
        LOGGER.info("user {} wants to get orc versions in projectId {} for orcId {}", username, projectId, orchestratorId);
        List<DSSOrchestratorVersion> orchestratorVersions = orchestratorMapper.getOrchestratorVersions(projectId, orchestratorId);
        LOGGER.info("projectId is {} , orcId is {}, orcVersions are {}", projectId, orchestratorId, orchestratorVersions);
        return orchestratorVersions;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String rollbackOrchestrator(String userName, Long projectId, String projectName,
                                       Long orchestratorId, String version, DSSLabel dssLabel, Workspace workspace) throws Exception {
        //1.新建一个版本
        //2.然后将version的版本内容进行去workflow进行cp
        //3.然后把生产的内容进行update到数据库
        DSSOrchestratorVersion oldOrcVersion=orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orchestratorId, 1);
        String latestVersion = oldOrcVersion.getVersion();
        List<DSSLabel> labels = new ArrayList<>();
        labels.add(dssLabel);
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        //git回滚
        if (!StringUtils.isEmpty(oldOrcVersion.getCommitId())) {
            Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
            GitRevertRequest gitRevertRequest = new GitRevertRequest(workspace.getWorkspaceId(), projectName, oldOrcVersion.getCommitId(), dssOrchestratorInfo.getName(), userName);
            GitRevertResponse gitRevertResponse = RpcAskUtils.processAskException(sender.ask(gitRevertRequest), GitRevertResponse.class, GitRevertRequest.class);
        }

        String newVersion = OrchestratorUtils.increaseVersion(latestVersion);
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        String comment = "回滚工作流到版本:" + version;
        dssOrchestratorVersion.setOrchestratorId(orchestratorId);
        dssOrchestratorVersion.setVersion(newVersion);
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setProjectId(projectId);
        dssOrchestratorVersion.setUpdater(userName);
        dssOrchestratorVersion.setComment(comment);
        dssOrchestratorVersion.setSource("rollback from version :" + version);
        dssOrchestratorVersion.setValidFlag(1);
        DSSOrchestratorVersion dbOrcVersion = orchestratorMapper.getVersionByOrchestratorIdAndVersion(orchestratorId, version);
        if (dbOrcVersion == null) {
            DSSExceptionUtils.dealErrorException(60070, "Rollback version " + version + " is not exist.", DSSOrchestratorErrorException.class);
        }
        String contextId = contextService.createContextID(workspace.getWorkspaceName(), projectName, dssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion(), userName);
        dssOrchestratorVersion.setContextId(contextId);
        LOGGER.info("Create a new ContextId {} for rollback the orchestration {} to version {}.", contextId, dssOrchestratorInfo.getName(), version);
        RefJobContentResponseRef responseRef = tryRefOperation(dssOrchestratorInfo, userName, workspace, Collections.singletonList(dssLabel), null,
                developmentService -> ((RefCRUDService) developmentService).getRefCopyOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setRefProjectId(dssOrchestratorInfo.getProjectId()).setProjectName(projectName),
                (developmentOperation, developmentRequestRef) -> {
                    CopyRequestRef copyRequestRef = (CopyRequestRef) developmentRequestRef;
                    Map<String, Object> refJobContent = MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dbOrcVersion.getAppId(),
                            OrchestratorRefConstant.ORCHESTRATION_DESCRIPTION, comment);
                    copyRequestRef.setNewVersion(dssOrchestratorVersion.getVersion())
                            .setRefJobContent(refJobContent);
                    return ((RefCopyOperation) developmentOperation).copyRef(copyRequestRef);
                }, "copy");
        dssOrchestratorVersion.setAppId((Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY));
        dssOrchestratorVersion.setContent((String) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY));
        List<String[]> paramConfTemplateIds=(List<String[]>) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_FLOWID_PARAMCONF_TEMPLATEID_TUPLES_KEY);
        dssOrchestratorVersion.setFormatContextId(contextId);
        //update appConn node contextId
        addOrchestratorVersionHook.beforeAdd(oldOrcVersion, Collections.emptyMap());
        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
        addOrchestratorVersionHook.afterAdd(dssOrchestratorVersion, Collections.singletonMap(OrchestratorRefConstant.ORCHESTRATION_FLOWID_PARAMCONF_TEMPLATEID_TUPLES_KEY,paramConfTemplateIds));
//        synProjectOrchestratorVersionId(dssOrchestratorVersion, labels);

        //若之前版本未进行git回滚，则自动提交回滚后的工作流至git
        if (StringUtils.isEmpty(oldOrcVersion.getCommitId())) {
            Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
            OrchestratorSubmitRequest submitRequest = new OrchestratorSubmitRequest();
            submitRequest.setFlowId(dssOrchestratorVersion.getAppId());
            submitRequest.setOrchestratorId(dssOrchestratorVersion.getOrchestratorId());
            submitRequest.setProjectName(projectName);
            submitRequest.setComment("rollback workflow: " + dssOrchestratorInfo.getName());
            orchestratorPluginService.submitFlow(submitRequest, userName, workspace);
        }

        return dssOrchestratorVersion.getVersion();
    }


    public void synProjectOrchestratorVersionId(DSSOrchestratorVersion dssOrchestratorVersion, List<DSSLabel> dssLabels) {
        //Is dev environment
        if (DSSLabelUtil.isDevEnv(dssLabels)) {
            RequestProjectUpdateOrcVersion updateOrchestratorVersion = new RequestProjectUpdateOrcVersion();
            updateOrchestratorVersion.setOrchestratorId(dssOrchestratorVersion.getOrchestratorId());
            updateOrchestratorVersion.setVersionId(dssOrchestratorVersion.getId());
            updateOrchestratorVersion.setDssLabels(dssLabels);
            updateOrchestratorVersion.setProjectId(dssOrchestratorVersion.getProjectId());
            //将最新的编排版本ID更新到工程编排表
            DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                    .ask(updateOrchestratorVersion);
        }
    }

    //新建前是否存在相同的编排名称
    @Override
    public void isExistSameNameBeforeCreate(Long workspaceId, Long projectId, String arrangeName) throws DSSFrameworkErrorException {
        List<DSSOrchestratorInfo> retList = orchestratorMapper.getByNameAndProjectId(projectId, arrangeName);
        if (CollectionUtils.isNotEmpty(retList)) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称已经存在");
        }
    }

    //是否存在相同的编排名称,如果不存在相同的编排名称則返回编排id
    @Override
    public Long isExistSameNameBeforeUpdate(OrchestratorModifyRequest orchestratorModifRequest) throws DSSFrameworkErrorException {
        DSSOrchestratorInfo orchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorModifRequest.getId());
        if (orchestratorInfo == null) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排模式ID=" + orchestratorModifRequest.getId() + "不存在");
        }
        //若修改了编排名称，检查是否存在相同的编排名称
        if (!orchestratorModifRequest.getOrchestratorName().equals(orchestratorInfo.getName())) {
            isExistSameNameBeforeCreate(orchestratorModifRequest.getWorkspaceId(), orchestratorModifRequest.getProjectId(), orchestratorModifRequest.getOrchestratorName());
        }
        return orchestratorInfo.getId();
    }

    /**
     * 查询编排模式
     *
     * @param orchestratorRequest request of front-end
     * @param username            username
     * @return list of OrchestratorBaseInfo
     */
    @Override
    public List<OrchestratorBaseInfo> getOrchestratorInfos(OrchestratorRequest orchestratorRequest, String username) {
        List<DSSOrchestratorInfo> list = orchestratorMapper.queryOrchestratorInfos(new HashMap<String, Object>() {{
            put("workspace_id", orchestratorRequest.getWorkspaceId());
            put("project_id", orchestratorRequest.getProjectId());
            put("orchestrator_mode", orchestratorRequest.getOrchestratorMode());
        }});
        List<OrchestratorBaseInfo> retList = new ArrayList<>(list.size());
        if (!CollectionUtils.isEmpty(list)) {
            //todo Is used in front-end?
            ProjectUserAuthResponse projectUserAuthResponse = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance()
                            .getProjectServerSender().ask(new ProjectUserAuthRequest(orchestratorRequest.getProjectId(), username)),
                    ProjectUserAuthResponse.class, ProjectUserAuthRequest.class);
            boolean isReleasable = false, isEditable = false;
            if (!CollectionUtils.isEmpty(projectUserAuthResponse.getPrivList())) {
                isReleasable = projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_RELEASE.getRank());
                isEditable = projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_EDIT.getRank());
            }
            isReleasable = isReleasable || projectUserAuthResponse.getProjectOwner().equals(username);
            isEditable = isEditable || projectUserAuthResponse.getProjectOwner().equals(username);

            for (DSSOrchestratorInfo dssOrchestratorInfo : list) {
                OrchestratorBaseInfo orchestratorBaseInfo = new OrchestratorBaseInfo();
                BeanUtils.copyProperties(dssOrchestratorInfo, orchestratorBaseInfo);
                orchestratorBaseInfo.setOrchestratorWays(OrchestratorUtils.convertList(dssOrchestratorInfo.getOrchestratorWay()));
                orchestratorBaseInfo.setOrchestratorName(dssOrchestratorInfo.getName());
                orchestratorBaseInfo.setCreateUser(dssOrchestratorInfo.getCreator());
                orchestratorBaseInfo.setOrchestratorId(dssOrchestratorInfo.getId());
                orchestratorBaseInfo.setEditable(isEditable || isReleasable);
                orchestratorBaseInfo.setReleasable(isReleasable);
                orchestratorBaseInfo.setIsDefaultReference(dssOrchestratorInfo.getIsDefaultReference());
                if (!StringUtils.isEmpty(dssOrchestratorInfo.getStatus()) && dssOrchestratorInfo.getStatus().equals(OrchestratorRefConstant.FLOW_STATUS_PUSHING)
                        && dssOrchestratorInfo.getStatus().equals(OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED)) {
                    orchestratorBaseInfo.setStatus(OrchestratorRefConstant.FLOW_STATUS_SAVE);
                } else {
                    orchestratorBaseInfo.setStatus(dssOrchestratorInfo.getStatus());
                }

                retList.add(orchestratorBaseInfo);
            }
        }
        return retList;
    }

    @Override
    public ResponseOrchestratorInfos queryOrchestratorInfos(RequestOrchestratorInfos requestOrchestratorInfos) {
        List<DSSOrchestratorInfo> orchestratorInfos = orchestratorMapper.queryOrchestratorInfos(new HashMap<String, Object>() {{
            put("workspace_id", requestOrchestratorInfos.getWorkspaceId());
            put("project_id", requestOrchestratorInfos.getProjectId());
            put("name", requestOrchestratorInfos.getName());
            put("orchestrator_mode", requestOrchestratorInfos.getOrchestratorMode());
        }});
        return new ResponseOrchestratorInfos(orchestratorInfos);
    }

    @Override
    public void batchClearContextId() {
        LOGGER.info("--------------------{} start clear old contextId------------------------", LocalDateTime.now());
        try {
            // 1、先去查询dss_orchestrator_version_info表，筛选出发布过n次及以上的编排，并获取老的发布记录。
            //为了不影响正常使用，需要频繁下载工作流bml文件，每次只拿50条工作流进行清理
            List<DSSOrchestratorVersion> historyOrcVersionList = orchestratorMapper.getHistoryOrcVersion(OrchestratorConf.DSS_PUBLISH_MAX_VERSION.getValue());
            while (historyOrcVersionList.size()>0) {
                LOGGER.info("Clear historyOrcVersionList size is "+ historyOrcVersionList.size());
                if (historyOrcVersionList == null || historyOrcVersionList.isEmpty()) {
                    LOGGER.info("--------------------{} end clear old contextId------------------------", LocalDateTime.now());
                    return;
                }
                List<String> contextIdList = historyOrcVersionList.stream().map(DSSOrchestratorVersion::getContextId).collect(Collectors.toList());

                // 2、根据appIds去查询子工作流contextId,必须保证标签绝对正确，否则可能清理了错误的工作流上下文ID
                List<DSSLabel> dssLabels = Lists.newArrayList(new EnvDSSLabel(OrchestratorConf.DSS_CS_CLEAR_ENV.getValue()));
                Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender(dssLabels);
                List<Long> workflowIdList = historyOrcVersionList.stream().map(orcInfo -> orcInfo.getAppId()).collect(Collectors.toList());
                ResponseSubFlowContextIds response = RpcAskUtils.processAskException(sender.ask(new RequestSubFlowContextIds(workflowIdList)),
                        ResponseSubFlowContextIds.class, RequestSubFlowContextIds.class);
                if (response != null) {
                    List<String> subContextIdList = response.getContextIdList();
                    if (subContextIdList != null && subContextIdList.size() > 0) {
                        contextIdList.addAll(response.getContextIdList());
                    }
                }
                LOGGER.info("Clear contextIdList size is "+ contextIdList.size());
                // 3、调用linkis接口批量删除contextId
                ContextClient contextClient = ContextClientFactory.getOrCreateContextClient();
                // 每次处理1000条数据
                if (contextIdList.size() < DSSOrchestratorConstant.MAX_CLEAR_SIZE) {
                    LOGGER.info("clear old contextId, contextIds：{}", contextIdList.toString());
                    contextClient.batchClearContextByHAID(contextIdList);
                } else {
                    int len = DSSOrchestratorConstant.MAX_CLEAR_SIZE;
                    int size = contextIdList.size();
                    int count = (size + len - 1) / len;
                    for (int i = 0; i < count; i++) {
                        List<String> subList = contextIdList.subList(i * len, (Math.min((i + 1) * len, size)));
                        LOGGER.info("clear old contextId by batch, {} batch, contextIds：{}", i + 1, subList.toString());
                        contextClient.batchClearContextByHAID(subList);
                        Thread.sleep(2000);
                    }
                }
                LOGGER.info("--------------------{} end clear old contextId------------------------", LocalDateTime.now());

                orchestratorMapper.batchUpdateOrcInfo(historyOrcVersionList);
                Thread.sleep(5000);
                historyOrcVersionList=orchestratorMapper.getHistoryOrcVersion(OrchestratorConf.DSS_PUBLISH_MAX_VERSION.getValue());
            }

        } catch (Exception e) {
            LOGGER.warn("execute linkis batch clear csId failed", e);
        }
    }

    @Override
    public String getAuthenToken(String gitUrlPre, String gitUsername, String gitPassword) throws ExecutionException {
        // 启动chromedriver
        WebDriver driver = generateChromeDriver(this.getClass().getClassLoader().getResource(DSSOrchestratorConstant.CHROME_DRIVER_PATH).getPath(), null);
        String token = "";
        try {
            //设置超时时间
            driver.manage().timeouts().implicitlyWait(Long.parseLong(GitServerConfig.GIT_TIME.getValue()), TimeUnit.SECONDS);
            driver.manage().window().maximize();
            driver.manage().window().setSize(new Dimension(1920, 1080));
            driver.get(UrlUtils.normalizeIp(gitUrlPre));
            WebElement elementUserName = driver.findElement(By.id(GitServerConfig.GIT_USER.getValue()));
            WebElement elementPassWord = driver.findElement(By.id(GitServerConfig.GIT_PASSWD.getValue()));
            WebElement elementBtn = driver.findElement(By.cssSelector(GitServerConfig.GIT_SUBMIT.getValue()));
            elementUserName.sendKeys(gitUsername);
            elementPassWord.sendKeys(gitPassword);
            elementBtn.submit();
            driver.navigate().refresh();
            LOGGER.info("for user getting... " + UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue()));
            Set<Cookie> cookies = driver.manage().getCookies();
            LOGGER.info("cookies： {}", cookies.toString());
            for (Cookie cookie:cookies) {
                if (cookie.getName().equals("_gitlab_session"))
                {
                    token = cookie.getValue();
                    break;
                }
            }
            LOGGER.info("driver cookies: {}", token);
        } catch (Exception e) {
            LOGGER.info("error bescause: ", e);
        } finally {
            driver.manage().deleteAllCookies();
            driver.quit();
        }
        return token;
    }


    private WebDriver generateChromeDriver(String path, Proxy seleniumProxy) throws ExecutionException {
        File file = new File(path);
        if (!file.canExecute() && !file.setExecutable(true)) {
            throw new ExecutionException(new Exception(path + "is not executable!"));
        }

        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, path);

        ChromeOptions options = new ChromeOptions();
        options.setProxy(seleniumProxy);
        options.addArguments("headless");
        options.addArguments("no-sandbox");
        options.addArguments("disable-gpu");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("disable-features=NetworkService");
        options.addArguments("ignore-certificate-errors");
        options.addArguments("silent");
        options.addArguments("--disable-application-cache");

        options.addArguments("disable-dev-shm-usage");
        options.addArguments("remote-debugging-port=9012");



        return new ChromeDriver(options);
    }

}