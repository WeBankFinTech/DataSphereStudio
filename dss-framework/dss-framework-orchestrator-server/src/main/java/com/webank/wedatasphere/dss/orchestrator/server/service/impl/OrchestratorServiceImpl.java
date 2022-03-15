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

import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationCreationOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationService;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.DSSOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationResponseRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.RefOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.utils.OrchestrationOperationUtils;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.DSSLabelUtil;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestProjectUpdateOrcVersion;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.publish.utils.OrchestrationDevelopmentOperationUtils;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.*;
import com.webank.wedatasphere.dss.standard.app.development.ref.*;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefQueryService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.NoSuchAppInstanceException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class OrchestratorServiceImpl implements OrchestratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorServiceImpl.class);
    @Autowired
    private OrchestratorManager orchestratorManager;
    @Autowired
    private OrchestratorMapper orchestratorMapper;
    @Autowired
    private ContextService contextService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrchestratorVo createOrchestrator(String userName,
                                             String workspaceName,
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
        //1.往数据库插入一条DSS编排模式
        orchestratorMapper.addOrchestrator(dssOrchestratorInfo);

        //2.如果调度系统要求同步创建工作流，向调度系统发送创建工作流的请求
        OrchestrationResponseRef orchestrationResponseRef = tryOrchestrationOperation(dssLabels, dssOrchestratorInfo.getProjectId(), projectInfoVo.getProjectName(), workspace, dssOrchestratorInfo,
                OrchestrationService::getOrchestrationCreationOperation, null,
                (structureOperation, structureRequestRef) -> ((OrchestrationCreationOperation) structureOperation).createOrchestration((DSSOrchestrationContentRequestRef) structureRequestRef),
                "create");

        if(orchestrationResponseRef != null) {
            dssOrchestratorInfo.setRefOrchestrationId(orchestrationResponseRef.getRefOrchestrationId());
        }

        String version = OrchestratorUtils.generateNewVersion();
        String contextId = contextService.createContextID(workspaceName, projectName, dssOrchestratorInfo.getName(), version, userName);
        LOGGER.info("Create a new ContextId: {} for new orchestrator {}.", contextId, dssOrchestratorInfo.getName());
        //3. 访问DSS工作流微模块创建工作流
        RefJobContentResponseRef appRef = tryRefOperation(dssOrchestratorInfo, userName,
                SSOHelper.getWorkspace(workspaceName), dssLabels,
                developmentService -> ((RefCRUDService) developmentService).getRefCreationOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setProjectName(projectName).setProjectRefId(projectId),
                (developmentOperation, developmentRequestRef) -> {
                    DSSJobContentRequestRef requestRef = (DSSJobContentRequestRef) developmentRequestRef;
                    requestRef.setDSSJobContent(MapUtils.newCommonMap(OrchestratorRefConstant.DSS_ORCHESTRATOR_INFO_KEY, dssOrchestratorInfo,
                            OrchestratorRefConstant.ORCHESTRATOR_VERSION_KEY, version));
                    return ((RefCreationOperation) developmentOperation).createRef(requestRef);
                }, "create");

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
        dssOrchestratorVersion.setValidFlag(1);
        dssOrchestratorVersion.setFormatContextId(contextId);
        // 4. 持久化编排的版本信息
        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
        orchestratorVo.setDssOrchestratorInfo(dssOrchestratorInfo);
        orchestratorVo.setDssOrchestratorVersion(dssOrchestratorVersion);

        return orchestratorVo;
    }

    private <K extends StructureRequestRef, V extends ResponseRef> V  tryOrchestrationOperation(List<DSSLabel> dssLabels, Long dssProjectId, String projectName, Workspace workspace, DSSOrchestration dssOrchestrator,
                                                                                                Function<OrchestrationService, StructureOperation> getOrchestrationOperation,
                                                                                                Consumer<RefOrchestrationContentRequestRef> refOrchestrationContentRequestRefConsumer,
                                                                                                BiFunction<StructureOperation, K, V> responseRefConsumer, String operationName) {
        ImmutablePair<OrchestrationService, AppInstance> orchestrationPair = getOrchestrationService(dssLabels);
        Long refProjectId = projectService.getAppConnProjectId(orchestrationPair.getValue().getId(), dssProjectId);
        V orchestrationResponseRef = null;
        if(refProjectId != null && orchestrationPair.getKey() != null) {
            LOGGER.info("try to {} a orchestration {} in SchedulerAppConn.", operationName, dssOrchestrator.getName());
            //这里无需进行重名判断，因为只在 SchedulerAppConn进行创建，一旦创建失败，会触发整个编排创建失败。
            orchestrationResponseRef = OrchestrationOperationUtils.tryOrchestrationOperation(() -> orchestrationPair.getKey(), getOrchestrationOperation,
                    dssOrchestrationContentRequestRef ->
                            dssOrchestrationContentRequestRef.setDSSOrchestration(dssOrchestrator).setProjectName(projectName).setRefProjectId(refProjectId),
                    refOrchestrationContentRequestRef -> {
                        refOrchestrationContentRequestRef.setRefProjectId(refProjectId).setProjectName(projectName);
                        refOrchestrationContentRequestRefConsumer.accept(refOrchestrationContentRequestRef);
                    },
                    (structureOperation, structureRequestRef) -> {
                        structureRequestRef.setDSSLabels(dssLabels).setWorkspace(workspace);
                        return responseRefConsumer.apply(structureOperation, (K) structureRequestRef);
                    }, operationName + " orchestration " + dssOrchestrator.getName() + " in SchedulerAppConn");
        }
        return orchestrationResponseRef;
    }

    protected ImmutablePair<OrchestrationService, AppInstance> getOrchestrationService(List<DSSLabel> dssLabels) {
        SchedulerAppConn appConn = AppConnManager.getAppConnManager().getAppConn(SchedulerAppConn.class);
        if(appConn == null) {
            return null;
        }
        AppInstance appInstance;
        try {
            appInstance = appConn.getAppDesc().getAppInstancesByLabels(dssLabels).get(0);
        } catch (NoSuchAppInstanceException e) {
            throw new ExternalOperationWarnException(60028, e.getDesc());
        }
        return new ImmutablePair<>(appConn.getOrCreateStructureStandard().getOrchestrationService(appInstance), appInstance);
    }

    private <K extends DevelopmentRequestRef, V extends ResponseRef> V tryRefOperation(DSSOrchestratorInfo dssOrchestratorInfo,
                                                   String userName,
                                                   Workspace workspace,
                                                   List<DSSLabel> dssLabels,
                                                   Function<DevelopmentService, DevelopmentOperation> getOperation,
                                                   Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                   Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                   BiFunction<DevelopmentOperation, K, V> responseRefConsumer,
                                                   String operationName) throws DSSErrorException {
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspace.getWorkspaceName(), dssOrchestratorInfo.getType(), dssOrchestratorInfo.getAppConnName(), dssLabels);
        return OrchestrationDevelopmentOperationUtils.tryOrchestrationOperation(dssOrchestratorInfo, dssOrchestrator,
                userName, workspace, dssLabels, getOperation, contextRequestRefConsumer, projectRefRequestRefConsumer,
                responseRefConsumer, operationName);
    }

    @Override
    public void updateOrchestrator(String userName,
                                   String workspaceName,
                                   DSSOrchestratorInfo dssOrchestratorInfo,
                                   List<DSSLabel> dssLabels) throws Exception {
        orchestratorMapper.updateOrchestrator(dssOrchestratorInfo);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(dssOrchestratorInfo.getId(),1);

        tryRefOperation(dssOrchestratorInfo, userName,
                SSOHelper.getWorkspace(workspaceName), dssLabels,
                developmentService -> ((RefCRUDService) developmentService).getRefUpdateOperation(),
                null,
                projectRefRequestRef -> projectRefRequestRef.setProjectRefId(dssOrchestratorInfo.getProjectId()),
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
                                   String workspaceName,
                                   String projectName,
                                   Long orchestratorInfoId,
                                   List<DSSLabel> dssLabels) throws Exception {

        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorInfoId);
        if(null == dssOrchestratorInfo){
            LOGGER.error("Not exists orchestration {}.", orchestratorInfoId);
            DSSExceptionUtils.dealErrorException(61123,
                    String.format("Not exists orchestration %s.", orchestratorInfoId),
                    DSSErrorException.class);
        }
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orchestratorInfoId,1);
        //todo 是否需要删除版本信息

        //todo 删除版本信息对应的工作流信息

        tryRefOperation(dssOrchestratorInfo, userName,
                SSOHelper.getWorkspace(workspaceName), dssLabels,
                developmentService -> ((RefCRUDService) developmentService).getRefDeletionOperation(),
                null, projectRefRequestRef -> projectRefRequestRef.setProjectRefId(dssOrchestratorInfo.getProjectId()),
                (developmentOperation, developmentRequestRef) -> {
                    RefJobContentRequestRef requestRef = (RefJobContentRequestRef) developmentRequestRef;
                    Map<String, Object> refJobContent = MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dssOrchestratorVersion.getAppId(),
                            OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, orchestratorInfoId);
                    requestRef.setRefJobContent(refJobContent);
                    return ((RefDeletionOperation) developmentRequestRef).deleteRef(requestRef);
                }, "delete");
        orchestratorMapper.deleteOrchestrator(orchestratorInfoId);
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
    public String openOrchestrator(String userName, String workspaceName, Long orchestratorId, List<DSSLabel> dssLabels) throws Exception {
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orchestratorId,1);
        if (null == dssOrchestratorInfo || null == dssOrchestratorVersion) {
            throw new DSSOrchestratorErrorException(1000856, "can not find orc from db for orcId: " + orchestratorId);
        }
        QueryJumpUrlResponseRef responseRef = (QueryJumpUrlResponseRef) tryRefOperation(dssOrchestratorInfo, userName,
                SSOHelper.getWorkspace(workspaceName), dssLabels,
                developmentService -> ((RefQueryService) developmentService).getRefQueryOperation(),
                null,
                projectRefRequestRef -> projectRefRequestRef.setProjectRefId(dssOrchestratorInfo.getProjectId()),
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
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orchestratorId,1);

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
        String latestVersion = orchestratorMapper.getLatestVersion(orchestratorId,1);
        List<DSSLabel> labels = new ArrayList<>();
        labels.add(dssLabel);
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
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
        RefJobContentResponseRef responseRef = tryRefOperation(dssOrchestratorInfo, userName, workspace, Collections.singletonList(dssLabel),
                developmentService -> ((RefCRUDService) developmentService).getRefCopyOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setProjectRefId(dssOrchestratorInfo.getProjectId()).setProjectName(projectName),
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
        dssOrchestratorVersion.setFormatContextId(contextId);
        //update appConn node contextId
        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
        synProjectOrchestratorVersionId(dssOrchestratorVersion,labels);
        return dssOrchestratorVersion.getVersion();
    }


    public void synProjectOrchestratorVersionId(DSSOrchestratorVersion dssOrchestratorVersion,List<DSSLabel> dssLabels){
        //Is dev environment
        if(DSSLabelUtil.isDevEnv(dssLabels)){
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

}