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
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.*;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationCreationOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationDeletionOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationService;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationUpdateOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.DSSOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationResponseRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationUpdateRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.RefOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.utils.OrchestrationOperationUtils;
import com.webank.wedatasphere.dss.common.constant.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.protocol.project.*;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitCommitInfoBetweenRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitRemoveRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitCommitResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitHistoryResponse;
import com.webank.wedatasphere.dss.orchestrator.common.entity.*;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestQuertByAppIdOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestQueryByIdOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.type.DSSOrchestratorRelation;
import com.webank.wedatasphere.dss.orchestrator.core.type.DSSOrchestratorRelationManager;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorCopyJobMapper;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.server.constant.DSSOrchestratorConstant;
import com.webank.wedatasphere.dss.orchestrator.server.constant.OrchestratorStatusEnum;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.*;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorCopyHistory;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorCopyVo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorUnlockVo;
import com.webank.wedatasphere.dss.orchestrator.server.job.OrchestratorCopyEnv;
import com.webank.wedatasphere.dss.orchestrator.server.job.OrchestratorCopyJob;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorFrameworkService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorOperateService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import com.webank.wedatasphere.dss.workflow.dao.FlowMapper;
import com.webank.wedatasphere.dss.workflow.dao.LockMapper;
import com.webank.wedatasphere.dss.workflow.dao.NodeMetaMapper;
import com.webank.wedatasphere.dss.workflow.dto.NodeMetaDO;
import com.webank.wedatasphere.dss.workflow.lock.DSSFlowEditLockManager;
import com.webank.wedatasphere.dss.workflow.service.SaveFlowHook;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class OrchestratorFrameworkServiceImpl implements OrchestratorFrameworkService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private OrchestratorMapper orchestratorMapper;
    @Autowired
    private OrchestratorService orchestratorService;
    @Autowired
    private OrchestratorManager orchestratorManager;
    @Autowired
    private OrchestratorCopyJobMapper orchestratorCopyJobMapper;
    @Autowired
    private OrchestratorCopyEnv orchestratorCopyEnv;
    @Autowired
    private OrchestratorOperateService orchestratorOperateService;
    @Autowired
    private LockMapper lockMapper;

    @Autowired
    @Qualifier("workflowBmlService")
    private BMLService bmlService;
    @Autowired
    private FlowMapper flowMapper;
    @Autowired
    private NodeMetaMapper nodeMetaMapper;
    @Autowired
    private SaveFlowHook saveFlowHook;
    private static ContextService contextService = ContextServiceImpl.getInstance();

    private static final int MAX_DESC_LENGTH = 250;
    private static final int MAX_NAME_LENGTH = 128;

    private final ThreadFactory orchestratorCopyThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("dss-orchestrator—copy-thread-%d")
            .setDaemon(false)
            .build();

    private final ExecutorService orchestratorCopyThreadPool = new ThreadPoolExecutor(0, 200, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024), orchestratorCopyThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    protected Sender getOrchestratorSender() {
        return DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();
    }

    /**
     * 1.拿到的dss orchestrator的appconn
     * 2.然后创建
     *
     * @param orchestratorCreateRequest 创建参数
     * @return
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    public CommonOrchestratorVo createOrchestrator(String username, OrchestratorCreateRequest orchestratorCreateRequest,
                                                   Workspace workspace) throws Exception {
        //检查desc字段长度
        if (orchestratorCreateRequest.getDescription().length() > MAX_DESC_LENGTH) {
            DSSFrameworkErrorException.dealErrorException(60000, "描述过长，请限制在" + MAX_DESC_LENGTH + "以内");
        }
        if (orchestratorCreateRequest.getOrchestratorName().length() > MAX_NAME_LENGTH) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称过长，请限制在" + MAX_NAME_LENGTH + "以内");
        }
        //是否存在相同的编排名称
        orchestratorService.isExistSameNameBeforeCreate(orchestratorCreateRequest.getWorkspaceId(), orchestratorCreateRequest.getProjectId(), orchestratorCreateRequest.getOrchestratorName());
        //判断工程是否存在,并且取出工程名称和空间名称
        DSSProject dssProject = validateOperation(orchestratorCreateRequest.getProjectId(), username);
        //1.创建编排实体bean
        DSSOrchestratorRelation dssOrchestratorRelation = DSSOrchestratorRelationManager.getDSSOrchestratorRelationByMode(orchestratorCreateRequest.getOrchestratorMode());
        DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
        dssOrchestratorInfo.setType(dssOrchestratorRelation.getDSSOrchestratorName());
        dssOrchestratorInfo.setAppConnName(dssOrchestratorRelation.getBindingAppConnName());
        dssOrchestratorInfo.setDesc(orchestratorCreateRequest.getDescription());
        dssOrchestratorInfo.setCreateTime(new Date());
        dssOrchestratorInfo.setName(orchestratorCreateRequest.getOrchestratorName());
        dssOrchestratorInfo.setCreator(username);
        dssOrchestratorInfo.setProjectId(orchestratorCreateRequest.getProjectId());
        dssOrchestratorInfo.setComment(orchestratorCreateRequest.getDescription());
        dssOrchestratorInfo.setSecondaryType(orchestratorCreateRequest.getOrchestratorWays().toString());
        dssOrchestratorInfo.setUses(orchestratorCreateRequest.getUses());
        //new field
        dssOrchestratorInfo.setWorkspaceId(workspace.getWorkspaceId());
        dssOrchestratorInfo.setOrchestratorWay(OrchestratorUtils.getModeStr(orchestratorCreateRequest.getOrchestratorWays()));
        dssOrchestratorInfo.setOrchestratorMode(orchestratorCreateRequest.getOrchestratorMode());
        dssOrchestratorInfo.setOrchestratorLevel(orchestratorCreateRequest.getOrchestratorLevel());
        dssOrchestratorInfo.setIsDefaultReference(orchestratorCreateRequest.getIsDefaultReference());
        //1.去orchestratorFramework创建编排模式
        LOGGER.info("{} begins to create a orchestrator {}.", username, orchestratorCreateRequest);
        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(orchestratorCreateRequest.getLabels().getRoute()));
        //2.如果调度系统要求同步创建工作流，向调度系统发送创建工作流的请求
        OrchestrationResponseRef orchestrationResponseRef = tryOrchestrationOperation(dssLabels, true, username,
                dssProject.getName(), workspace, dssOrchestratorInfo,
                OrchestrationService::getOrchestrationCreationOperation,
                (structureOperation, structureRequestRef) -> ((OrchestrationCreationOperation) structureOperation)
                        .createOrchestration((DSSOrchestrationContentRequestRef) structureRequestRef), "create");
        OrchestratorVo orchestratorVo = orchestratorService.createOrchestrator(username, workspace, dssProject.getName(),
                dssOrchestratorInfo.getProjectId(), dssOrchestratorInfo.getDesc(), dssOrchestratorInfo, dssLabels);
        Long orchestratorId = orchestratorVo.getDssOrchestratorInfo().getId();
        Long orchestratorVersionId = orchestratorVo.getDssOrchestratorVersion().getId();
        // 初始化置为已提交
        if (dssProject.getAssociateGit() != null && dssProject.getAssociateGit()) {
            lockMapper.updateOrchestratorStatus(orchestratorId, OrchestratorRefConstant.FLOW_STATUS_SAVE);
        }
        LOGGER.info("created orchestration {} with orchestratorId is {}, and versionId is {}.", orchestratorCreateRequest.getOrchestratorName(), orchestratorId, orchestratorVersionId);
        //4.将工程和orchestrator的关系存储到的数据库中
        if (orchestrationResponseRef != null) {
            Long refProjectId = (Long) orchestrationResponseRef.toMap().get("refProjectId");
            orchestratorMapper.addOrchestratorRefOrchestration(new DSSOrchestratorRefOrchestration(orchestratorId, refProjectId, orchestrationResponseRef.getRefOrchestrationId()));
        }
        CommonOrchestratorVo commonOrchestratorVo = new CommonOrchestratorVo();
        commonOrchestratorVo.setOrchestratorId(orchestratorId);
        return commonOrchestratorVo;
    }

    private <K extends StructureRequestRef, V extends ResponseRef> V tryOrchestrationOperation(List<DSSLabel> dssLabels, Boolean askProjectSender, String userName, String projectName,
                                                                                               Workspace workspace, DSSOrchestratorInfo dssOrchestrator,
                                                                                               Function<OrchestrationService, StructureOperation> getOrchestrationOperation,
                                                                                               BiFunction<StructureOperation, K, V> responseRefConsumer, String operationName) {
        ImmutablePair<OrchestrationService, AppInstance> orchestrationPair = getOrchestrationService(dssOrchestrator, userName, workspace, dssLabels);
        Long refProjectId, refOrchestrationId;
        if (askProjectSender) {
            ProjectRefIdResponse projectRefIdResponse = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                    .ask(new ProjectRefIdRequest(orchestrationPair.getValue().getId(), dssOrchestrator.getProjectId())), ProjectRefIdResponse.class, ProjectRefIdRequest.class);
            refProjectId = projectRefIdResponse.getRefProjectId();
            refOrchestrationId = null;
        } else {
            DSSOrchestratorRefOrchestration refOrchestration = orchestratorMapper.getRefOrchestrationId(dssOrchestrator.getId());
            if (refOrchestration != null) {
                refProjectId = refOrchestration.getRefProjectId();
                refOrchestrationId = refOrchestration.getRefOrchestrationId();
            } else {
                refProjectId = null;
                refOrchestrationId = null;
            }
        }
        V orchestrationResponseRef = null;
        if (refProjectId != null && orchestrationPair.getKey() != null) {
            LOGGER.info("project {} try to {} a orchestration {} in SchedulerAppConn.", projectName, operationName, dssOrchestrator.getName());
            //这里无需进行重名判断，因为只在 SchedulerAppConn进行创建，一旦创建失败，会触发整个编排创建失败。
            orchestrationResponseRef = OrchestrationOperationUtils.tryOrchestrationOperation(orchestrationPair::getKey, getOrchestrationOperation,
                    dssOrchestrationContentRequestRef ->
                            dssOrchestrationContentRequestRef.setDSSOrchestration(dssOrchestrator).setProjectName(projectName).setRefProjectId(refProjectId),
                    refOrchestrationContentRequestRef ->
                            refOrchestrationContentRequestRef.setRefOrchestrationId(refOrchestrationId).setOrchestrationName(dssOrchestrator.getName())
                                    .setRefProjectId(refProjectId).setProjectName(projectName),
                    (structureOperation, structureRequestRef) -> {
                        structureRequestRef.setDSSLabels(dssLabels).setWorkspace(workspace).setUserName(userName);
                        return responseRefConsumer.apply(structureOperation, (K) structureRequestRef);
                    }, operationName + " orchestration " + dssOrchestrator.getName() + " in SchedulerAppConn");
            if (askProjectSender) {
                orchestrationResponseRef.toMap().put("refProjectId", refProjectId);
            }
        }
        return orchestrationResponseRef;
    }

    @Override
    public CommonOrchestratorVo modifyOrchestrator(String username, OrchestratorModifyRequest orchestratorModifyRequest, Workspace workspace) throws Exception {
        //检查desc字段长度
        if (orchestratorModifyRequest.getDescription().length() > MAX_DESC_LENGTH) {
            DSSFrameworkErrorException.dealErrorException(60000, "描述字段过长，请限制在" + MAX_DESC_LENGTH + "以内");
        }
        if (orchestratorModifyRequest.getOrchestratorName().length() > MAX_NAME_LENGTH) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称过长，请限制在" + MAX_NAME_LENGTH + "以内");
        }
        //判断工程是否存在,并且取出工程名称和空间名称
        DSSProject dssProject = validateOperation(orchestratorModifyRequest.getProjectId(), username);
        workspace.setWorkspaceName(dssProject.getWorkspaceName());
        //是否存在相同的编排名称 //todo 返回orchestratorInfo而不是id
        Long orchestratorId = orchestratorService.isExistSameNameBeforeUpdate(orchestratorModifyRequest, dssProject, username);
        LOGGER.info("{} begins to update a orchestrator {}.", username, orchestratorModifyRequest.getOrchestratorName());
        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(orchestratorModifyRequest.getLabels().getRoute()));
        DSSOrchestratorRelation dssOrchestratorRelation = DSSOrchestratorRelationManager.getDSSOrchestratorRelationByMode(orchestratorModifyRequest.getOrchestratorMode());
        DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
        dssOrchestratorInfo.setId(orchestratorId);
        dssOrchestratorInfo.setType(dssOrchestratorRelation.getDSSOrchestratorName());
        dssOrchestratorInfo.setDesc(orchestratorModifyRequest.getDescription());
        dssOrchestratorInfo.setUpdateTime(new Date(System.currentTimeMillis()));
        dssOrchestratorInfo.setAppConnName(dssOrchestratorRelation.getBindingAppConnName());
        dssOrchestratorInfo.setName(orchestratorModifyRequest.getOrchestratorName());
        dssOrchestratorInfo.setCreator(username);
        dssOrchestratorInfo.setProjectId(orchestratorModifyRequest.getProjectId());
        dssOrchestratorInfo.setComment(orchestratorModifyRequest.getDescription());
        dssOrchestratorInfo.setSecondaryType(orchestratorModifyRequest.getOrchestratorWays().toString());
        dssOrchestratorInfo.setUpdateUser(username);
        dssOrchestratorInfo.setOrchestratorMode(orchestratorModifyRequest.getOrchestratorMode());
        dssOrchestratorInfo.setOrchestratorWay(OrchestratorUtils.getModeStr(orchestratorModifyRequest.getOrchestratorWays()));
        dssOrchestratorInfo.setOrchestratorLevel(orchestratorModifyRequest.getOrchestratorLevel());
        dssOrchestratorInfo.setUses(orchestratorModifyRequest.getUses());
        dssOrchestratorInfo.setIsDefaultReference(orchestratorModifyRequest.getIsDefaultReference());
        //1.如果调度系统要求同步创建工作流，向调度系统发送更新工作流的请求
        tryOrchestrationOperation(dssLabels, false, username, dssProject.getName(), workspace, dssOrchestratorInfo,
                OrchestrationService::getOrchestrationUpdateOperation,
                (structureOperation, structureRequestRef) -> ((OrchestrationUpdateOperation) structureOperation)
                        .updateOrchestration((OrchestrationUpdateRequestRef) structureRequestRef), "update");
        orchestratorService.updateOrchestrator(username, workspace, dssOrchestratorInfo, dssLabels);
        //3.将工程和orchestrator的关系存储到的数据库中
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
        orchestratorVo.setOrchestratorId(orchestratorId);
        return orchestratorVo;
    }

    @Override
    public CommonOrchestratorVo deleteOrchestrator(String username, OrchestratorDeleteRequest orchestratorDeleteRequest, Workspace workspace) throws Exception {
        //判断工程是否存在,并且取出工程名称和空间名称
        DSSProject dssProject = validateOperation(orchestratorDeleteRequest.getProjectId(), username);
        DSSOrchestratorInfo orchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorDeleteRequest.getId());
        LOGGER.info("{} begins to delete a orchestrator {}.", username, orchestratorInfo.getName());
        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(orchestratorDeleteRequest.getLabels().getRoute()));
        if (orchestratorDeleteRequest.getDeleteSchedulerWorkflow()) {
            tryOrchestrationOperation(dssLabels, false, username, dssProject.getName(), workspace, orchestratorInfo,
                    OrchestrationService::getOrchestrationDeletionOperation,
                    (structureOperation, structureRequestRef) -> ((OrchestrationDeletionOperation) structureOperation)
                            .deleteOrchestration((RefOrchestrationContentRequestRef) structureRequestRef), "delete");
        }
        if (dssProject.getAssociateGit() != null && dssProject.getAssociateGit()) {
            try {
                // git删除成功之后再删除库表记录
                Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
                List<String> path = new ArrayList<>();
                path.add(orchestratorInfo.getName());
                GitRemoveRequest removeRequest = new GitRemoveRequest(workspace.getWorkspaceId(), dssProject.getName(), path, username);
                GitCommitResponse commitResponse = RpcAskUtils.processAskException(sender.ask(removeRequest), GitCommitResponse.class, GitRemoveRequest.class);
                lockMapper.updateOrchestratorStatus(orchestratorDeleteRequest.getId(), OrchestratorRefConstant.FLOW_STATUS_PUSH);
                DSSOrchestratorVersion versionById = orchestratorMapper.getLatestOrchestratorVersionById(orchestratorInfo.getId());
                if (versionById != null) {
                    lockMapper.updateOrchestratorVersionCommitId(commitResponse.getCommitId(), versionById.getAppId());
                }
            } catch (Exception e) {
                LOGGER.error("git remove failed, the reason is: ", e);
                lockMapper.updateOrchestratorStatus(orchestratorDeleteRequest.getId(), OrchestratorRefConstant.FLOW_STATUS_SAVE);
                throw new GitErrorException(800001, e.getMessage());
            }
        }
        orchestratorService.deleteOrchestrator(username, workspace, dssProject.getName(), orchestratorInfo.getId(), dssLabels);
        orchestratorOperateService.deleteTemplateOperate(orchestratorInfo.getId());
        LOGGER.info("delete orchestrator {} by orchestrator framework succeed.", orchestratorInfo.getName());
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
        orchestratorVo.setOrchestratorName(orchestratorInfo.getName());
        orchestratorVo.setOrchestratorId(orchestratorInfo.getId());
        return orchestratorVo;
    }

    @Override
    public OrchestratorUnlockVo unlockOrchestrator(String username, Workspace workspace, OrchestratorUnlockRequest request) throws DSSErrorException {
        //编辑权限校验
        DSSProject dssProject = validateOperation(request.getProjectId(), username);
        DSSOrchestratorInfo orchestratorInfo = orchestratorMapper.getOrchestrator(request.getId());
        LOGGER.info("{} begins to unlock orchestrator {}.", username, orchestratorInfo.getName());
        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(request.getLabels().getRoute()));
        OrchestratorUnlockVo orchestratorUnlockVo = orchestratorService.unlockOrchestrator(username, workspace,
                dssProject.getName(), orchestratorInfo.getId(), request.getConfirmDelete(), dssLabels);
        orchestratorUnlockVo.setOrchestratorName(orchestratorInfo.getName());
        orchestratorUnlockVo.setOrchestratorId(orchestratorInfo.getId());
        return orchestratorUnlockVo;
    }

    @Override
    public DSSOrchestratorVersion getLatestOrchestratorVersion(Long orchestratorId) {
        return orchestratorMapper.getLatestOrchestratorVersionById(orchestratorId);
    }

    @Override
    public String copyOrchestrator(String username, OrchestratorCopyRequest orchestratorCopyRequest, Workspace workspace) throws Exception {
        if (orchestratorCopyRequest.getTargetOrchestratorName().length() > MAX_NAME_LENGTH) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称过长，请限制在" + MAX_NAME_LENGTH + "以内");
        }
        //校验编排名是可用
        orchestratorService.isExistSameNameBeforeCreate(workspace.getWorkspaceId(), orchestratorCopyRequest.getTargetProjectId(), orchestratorCopyRequest.getTargetOrchestratorName());
        //判断用户对项目是否有权限
        DSSProject sourceProject = validateOperation(orchestratorCopyRequest.getSourceProjectId(), username);
        DSSProject targetProject = validateOperation(orchestratorCopyRequest.getTargetProjectId(), username);

        DSSOrchestratorInfo sourceOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorCopyRequest.getSourceOrchestratorId());
        if (sourceOrchestratorInfo == null) {
            LOGGER.error("orchestrator: {} not found.", orchestratorCopyRequest.getSourceOrchestratorName());
            DSSExceptionUtils.dealErrorException(6013, "orchestrator not found.", DSSOrchestratorErrorException.class);
        }
        if (StringUtils.isBlank(orchestratorCopyRequest.getWorkflowNodeSuffix())) {
            orchestratorCopyRequest.setWorkflowNodeSuffix("copy");
        } else if (orchestratorCopyRequest.getWorkflowNodeSuffix().length() > 10) {
            DSSExceptionUtils.dealErrorException(6014, "The node suffix length can not exceed 10. (节点后缀长度不能超过10)", DSSOrchestratorErrorException.class);
        }
        String dssLabel = null;
        if (orchestratorCopyRequest.getLabels() != null && orchestratorCopyRequest.getLabels().getRoute() != null) {
            dssLabel = orchestratorCopyRequest.getLabels().getRoute();
        } else {
            dssLabel = DSSCommonUtils.ENV_LABEL_VALUE_DEV;
        }
        OrchestratorCopyVo orchestratorCopyVo = new OrchestratorCopyVo.Builder(username, sourceProject.getId(), sourceProject.getName(), targetProject.getId(),
                targetProject.getName(), sourceOrchestratorInfo, orchestratorCopyRequest.getTargetOrchestratorName(),
                orchestratorCopyRequest.getWorkflowNodeSuffix(), new EnvDSSLabel(dssLabel),
                workspace, Sender.getThisInstance()).setCopyTaskId(null).build();
        OrchestratorCopyJob orchestratorCopyJob = new OrchestratorCopyJob();
        orchestratorCopyJob.setOrchestratorCopyVo(orchestratorCopyVo);
        orchestratorCopyJob.setOrchestratorCopyEnv(orchestratorCopyEnv);
        orchestratorCopyJob.getOrchestratorCopyInfo().setId(UUID.randomUUID().toString());

        orchestratorCopyThreadPool.submit(orchestratorCopyJob);

        return orchestratorCopyJob.getOrchestratorCopyInfo().getId();
    }

    @Override
    public Pair<Long, List<OrchestratorCopyHistory>> getOrchestratorCopyHistory(String username, Workspace workspace, Long orchestratorId, Integer currentPage, Integer pageSize) throws Exception {
        long total = 0L;

        List<DSSOrchestratorCopyInfo> orchestratorCopyInfoList = orchestratorCopyJobMapper.getOrchestratorCopyInfoList(orchestratorId);
        if (CollectionUtils.isEmpty(orchestratorCopyInfoList)) {
            return new Pair<>(total, Lists.newArrayList());
        }
        total = orchestratorCopyInfoList.size();

        List<OrchestratorCopyHistory> orchestratorCopyHistoryList = new ArrayList<>();
        OrchestratorCopyHistory orchestratorCopyHistory;
        for (DSSOrchestratorCopyInfo orchestratorCopyInfo : orchestratorCopyInfoList) {
            orchestratorCopyHistory = new OrchestratorCopyHistory(orchestratorCopyInfo.getId(), orchestratorCopyInfo.getUsername(), workspace.getWorkspaceName(),
                    orchestratorCopyInfo.getSourceOrchestratorName(), orchestratorCopyInfo.getTargetOrchestratorName(),
                    orchestratorCopyInfo.getSourceProjectName(), orchestratorCopyInfo.getTargetProjectName(), orchestratorCopyInfo.getWorkflowNodeSuffix(),
                    orchestratorCopyInfo.getMicroserverName(), orchestratorCopyInfo.getExceptionInfo(), orchestratorCopyInfo.getStatus(),
                    orchestratorCopyInfo.getIsCopying(), DTF.format(LocalDateTime.ofInstant(orchestratorCopyInfo.getStartTime().toInstant(), ZoneId.systemDefault())),
                    DTF.format(LocalDateTime.ofInstant(orchestratorCopyInfo.getEndTime().toInstant(), ZoneId.systemDefault())));
            orchestratorCopyHistoryList.add(orchestratorCopyHistory);
        }
        return new Pair<>(total, orchestratorCopyHistoryList);
    }

    @Override
    public Boolean getOrchestratorCopyStatus(Long sourceOrchestratorId) {
        return StringUtils.isNotBlank(orchestratorCopyJobMapper.getOrchestratorCopyStatus(sourceOrchestratorId));
    }

    @Override
    public DSSOrchestratorCopyInfo getOrchestratorCopyInfoById(String copyInfoId) {
        return orchestratorCopyJobMapper.getOrchestratorCopyInfoById(copyInfoId);
    }

    protected ImmutablePair<OrchestrationService, AppInstance> getOrchestrationService(DSSOrchestratorInfo dssOrchestratorInfo,
                                                                                       String user,
                                                                                       Workspace workspace,
                                                                                       List<DSSLabel> dssLabels) {
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(user, workspace.getWorkspaceName(), dssOrchestratorInfo.getType(), dssLabels);
        if (CollectionUtils.isEmpty(dssOrchestratorInfo.getLinkedAppConnNames())) {
            dssOrchestratorInfo.setLinkedAppConnNames(dssOrchestrator.getLinkedAppConn().stream().map(appConn -> appConn.getAppDesc().getAppName()).collect(Collectors.toList()));
        }
        SchedulerAppConn appConn = dssOrchestrator.getSchedulerAppConn();
        if (appConn == null) {
            throw new ExternalOperationWarnException(50322, "DSSOrchestrator " + dssOrchestrator.getName() + " has no SchedulerAppConn.");
        }
        AppInstance appInstance = appConn.getAppDesc().getAppInstances().get(0);
        return new ImmutablePair<>(appConn.getOrCreateStructureStandard().getOrchestrationService(appInstance), appInstance);
    }

    /**
     * Determine whether the project exists.
     * and validate user edit_auth for the project.
     *
     * @param projectId
     * @return
     * @throws DSSOrchestratorErrorException
     */
    public static DSSProject validateOperation(long projectId, String username) throws DSSOrchestratorErrorException {
        ProjectInfoRequest projectInfoRequest = new ProjectInfoRequest();
        projectInfoRequest.setProjectId(projectId);
        DSSProject dssProject = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                .ask(projectInfoRequest), DSSProject.class, ProjectInfoRequest.class);
        if (dssProject == null) {
            DSSExceptionUtils.dealErrorException(6003, "工程不存在", DSSOrchestratorErrorException.class);
        }
        if (!hasProjectEditPriv(projectId, username)) {
            DSSExceptionUtils.dealErrorException(6004, "用户没有工程编辑权限", DSSOrchestratorErrorException.class);
        }
        return dssProject;
    }

    private static boolean hasProjectEditPriv(Long projectId, String username) {
        ProjectUserAuthResponse projectUserAuthResponse = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance()
                .getProjectServerSender().ask(new ProjectUserAuthRequest(projectId, username)), ProjectUserAuthResponse.class, ProjectUserAuthRequest.class);
        boolean hasEditPriv = false;
        if (!CollectionUtils.isEmpty(projectUserAuthResponse.getPrivList())) {
            hasEditPriv = projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_EDIT.getRank()) ||
                    projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_RELEASE.getRank());
        }
        return hasEditPriv || projectUserAuthResponse.getProjectOwner().equals(username);
    }

    @Override
    public OrchestratorSubmitJob getOrchestratorStatus(Long orchestratorId) {
        return orchestratorMapper.selectSubmitJobStatus(orchestratorId);
    }

    @Override
    public GitHistoryResponse getHistory(Long workspaceId, Long orchestratorId, String projectName) {
        DSSOrchestratorInfo orchestrator = orchestratorMapper.getOrchestrator(orchestratorId);
        List<DSSOrchestratorVersion> versionByOrchestratorId = orchestratorMapper.getVersionByOrchestratorId(orchestratorId);
        if (CollectionUtils.isEmpty(versionByOrchestratorId)) {
            return new GitHistoryResponse();
        }
        String workflowName = orchestrator.getName();
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitHistoryResponse historyResponse = null;
        // 当前未有版本发布
        GitCommitInfoBetweenRequest commitInfoBetweenRequest = new GitCommitInfoBetweenRequest();
        String newCommitId = versionByOrchestratorId.get(0).getCommitId();
        commitInfoBetweenRequest.setNewCommitId(newCommitId);
        String oldCommitId = null;
        int i = 0;
        for (DSSOrchestratorVersion version : versionByOrchestratorId) {
            if (version.getCommitId() != null && i++ != 0) {
                oldCommitId = version.getCommitId();
                break;
            }
        }
        commitInfoBetweenRequest.setOldCommitId(oldCommitId);
        commitInfoBetweenRequest.setProjectName(projectName);
        commitInfoBetweenRequest.setDirName(workflowName);
        commitInfoBetweenRequest.setWorkspaceId(workspaceId);
        historyResponse = RpcAskUtils.processAskException(sender.ask(commitInfoBetweenRequest), GitHistoryResponse.class, GitCommitInfoBetweenRequest.class);

        return historyResponse;
    }


    @Override
    public void modifyOrchestratorMeta(String username, ModifyOrchestratorMetaRequest modifyOrchestratorMetaRequest, Workspace workspace, DSSOrchestratorVersion orchestratorVersion) throws Exception {

        //检查desc字段长度
        if (modifyOrchestratorMetaRequest.getDescription().length() > MAX_DESC_LENGTH) {
            DSSFrameworkErrorException.dealErrorException(60000, "描述字段过长，请限制在" + MAX_DESC_LENGTH + "以内");
        }
        if (modifyOrchestratorMetaRequest.getOrchestratorName().length() > MAX_NAME_LENGTH) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称过长，请限制在" + MAX_NAME_LENGTH + "以内");
        }
        //判断工程是否存在,并且取出工程名称和空间名称
        DSSProject dssProject = validateOperation(modifyOrchestratorMetaRequest.getProjectId(), username);

        OrchestratorMeta orchestratorMetaInfo = orchestratorService.getOrchestratorMetaInfo(modifyOrchestratorMetaRequest, dssProject, username);
        DSSOrchestratorRelation dssOrchestratorRelation = DSSOrchestratorRelationManager.getDSSOrchestratorRelationByMode(orchestratorMetaInfo.getOrchestratorMode());

        DSSOrchestratorInfo dssOrchestratorInfo = getDssOrchestratorInfo(orchestratorMetaInfo, dssOrchestratorRelation);

        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(DSSCommonUtils.ENV_LABEL_VALUE_DEV));
        //1.如果调度系统要求同步创建工作流，向调度系统发送更新工作流的请求
        tryOrchestrationOperation(dssLabels, false, username, dssProject.getName(), workspace, dssOrchestratorInfo,
                OrchestrationService::getOrchestrationUpdateOperation,
                (structureOperation, structureRequestRef) -> ((OrchestrationUpdateOperation) structureOperation)
                        .updateOrchestration((OrchestrationUpdateRequestRef) structureRequestRef), "update");

        updateBmlResource(orchestratorMetaInfo, username, orchestratorVersion);

        orchestratorService.updateOrchestrator(username, workspace, dssOrchestratorInfo, dssLabels);

    }

    private static DSSOrchestratorInfo getDssOrchestratorInfo(OrchestratorMeta orchestratorMetaInfo, DSSOrchestratorRelation dssOrchestratorRelation) {
        DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
        dssOrchestratorInfo.setId(orchestratorMetaInfo.getOrchestratorId());
        dssOrchestratorInfo.setName(orchestratorMetaInfo.getOrchestratorName());
        dssOrchestratorInfo.setUpdateUser(orchestratorMetaInfo.getUpdateUser());
        dssOrchestratorInfo.setUpdateTime(orchestratorMetaInfo.getUpdateTime());
        dssOrchestratorInfo.setDesc(orchestratorMetaInfo.getDescription());
        dssOrchestratorInfo.setCreator(orchestratorMetaInfo.getCreator());
        dssOrchestratorInfo.setProjectId(orchestratorMetaInfo.getProjectId());
        dssOrchestratorInfo.setComment(orchestratorMetaInfo.getDescription());
        dssOrchestratorInfo.setIsDefaultReference(orchestratorMetaInfo.getIsDefaultReference());
        dssOrchestratorInfo.setType(dssOrchestratorRelation.getDSSOrchestratorName());
        dssOrchestratorInfo.setAppConnName(dssOrchestratorRelation.getBindingAppConnName());
        dssOrchestratorInfo.setSecondaryType(orchestratorMetaInfo.getOrchestratorWay());
        dssOrchestratorInfo.setOrchestratorMode(orchestratorMetaInfo.getOrchestratorMode());
        dssOrchestratorInfo.setOrchestratorWay(orchestratorMetaInfo.getOrchestratorWay());
        dssOrchestratorInfo.setOrchestratorLevel(orchestratorMetaInfo.getOrchestratorLevel());
        dssOrchestratorInfo.setUses(orchestratorMetaInfo.getUses());
        return dssOrchestratorInfo;
    }


    @Override
    public List<OrchestratorMeta> getAllOrchestratorMeta(OrchestratorMetaRequest orchestratorMetaRequest, List<Long> total) {
        List<OrchestratorMeta> orchestratorMetaList = orchestratorMapper.getAllOrchestratorMeta(orchestratorMetaRequest);
        List<OrchestratorMeta> orchestratorMetaInfo = new ArrayList<>();
        if (CollectionUtils.isEmpty(orchestratorMetaList)) {
            total.add(0L);
            return orchestratorMetaInfo;
        }

        List<Long> orchestratorIdList = orchestratorMetaList.stream().map(OrchestratorMeta::getOrchestratorId).collect(Collectors.toList());
        List<OrchestratorReleaseVersionInfo> releaseVersionInfos = orchestratorMapper.getOrchestratorReleaseVersionInfo(orchestratorIdList);
        List<OrchestratorReleaseVersionInfo> releaseVersionList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(releaseVersionInfos)) {
            // 分组排序 获取编排最新的第一条记录信息
            releaseVersionList = releaseVersionInfos.stream()
                    .collect(Collectors.groupingBy(OrchestratorReleaseVersionInfo::getOrchestratorId)).values()
                    .stream()
                    .flatMap(v -> Stream.of(v.stream().max(Comparator.comparing(OrchestratorReleaseVersionInfo::getId)).get()))
                    .collect(Collectors.toList());
        }
        // 获取模板名称
        List<OrchestratorTemplateInfo> templateInfos = orchestratorMapper.getOrchestratorDefaultTemplateInfo(orchestratorIdList);
        Map<Long, String> templateMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(templateInfos)) {
            // 分组 拼接模板名称
            templateMap = templateInfos.stream().collect(Collectors.groupingBy(OrchestratorTemplateInfo::getOrchestratorId,
                    Collectors.mapping(OrchestratorTemplateInfo::getTemplateName, Collectors.joining(","))));
        }


        for (OrchestratorMeta orchestratorMeta : orchestratorMetaList) {

            OrchestratorReleaseVersionInfo releaseVersion = releaseVersionList.stream().filter(releaseVersionInfo ->
                            releaseVersionInfo.getOrchestratorId().equals(orchestratorMeta.getOrchestratorId()))
                    .findFirst().orElse(new OrchestratorReleaseVersionInfo());

            orchestratorMeta.setVersion(releaseVersion.getVersion());
            orchestratorMeta.setUpdateTime(releaseVersion.getUpdateTime());
            orchestratorMeta.setUpdateUser(releaseVersion.getUpdater());

            /*
             * 对于接入git的项目下，并且当前状态为save的编排，dss_orchestrator_submit_job_info根据编排Id获取最新的提交记录，
             * 如果之前提交失败，就把原因返回到前端展示，状态-待提交
             * **/
            if (orchestratorMeta.getAssociateGit() != null && orchestratorMeta.getAssociateGit()
                    && OrchestratorRefConstant.FLOW_STATUS_SAVE.equalsIgnoreCase(orchestratorMeta.getStatus())) {

                OrchestratorSubmitJob orchestratorSubmitJob = orchestratorMapper.selectSubmitJobStatus(orchestratorMeta.getOrchestratorId());

                if(orchestratorSubmitJob !=null){

                    if(OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED.equalsIgnoreCase(orchestratorSubmitJob.getStatus())){

                        orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_SAVE);
                        orchestratorMeta.setErrorMsg(orchestratorSubmitJob.getErrorMsg());

                    }else if(OrchestratorRefConstant.FLOW_STATUS_PUSHING.equalsIgnoreCase(orchestratorSubmitJob.getStatus())){
                        // 提交中
                        orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUSHING);
                    }

                }

            } else if (StringUtils.isBlank(orchestratorMeta.getStatus())
                    || OrchestratorRefConstant.FLOW_STATUS_PUSH.equalsIgnoreCase(orchestratorMeta.getStatus())) {

                // 对于当前状态为push或者为空的，查询 dss_release_task，根据编排Id获取，状态-待发布
                if (OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED.equalsIgnoreCase(releaseVersion.getStatus())) {
                    orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUSH);
                    orchestratorMeta.setErrorMsg(releaseVersion.getErrorMsg());

                } else if (OrchestratorRefConstant.FLOW_STATUS_PUSHING.equalsIgnoreCase(releaseVersion.getStatus())) {
                    // 发布中状态
                    orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUBLISHING);
                }

            }


            if (templateMap.containsKey(orchestratorMeta.getOrchestratorId())) {
                orchestratorMeta.setTemplateName(templateMap.get(orchestratorMeta.getOrchestratorId()));
            }

            // 若状态为NULL, 则置为 无状态
            if (StringUtils.isBlank(orchestratorMeta.getStatus())) {
                orchestratorMeta.setStatus(OrchestratorStatusEnum.STATELESS.getStatus());
            }

        }

        if (!StringUtils.isBlank(orchestratorMetaRequest.getStatus())) {
            // 状态过滤
            orchestratorMetaList = orchestratorMetaList.stream().filter(orchestratorMeta ->
                    orchestratorMeta.getStatus().equalsIgnoreCase(orchestratorMetaRequest.getStatus())).collect(Collectors.toList());
        }

        total.add((long) orchestratorMetaList.size());

        // 排序
        if (DSSOrchestratorConstant.ASCEND.equalsIgnoreCase(orchestratorMetaRequest.getOrderBy())) {
            orchestratorMetaList = orchestratorMetaList.stream().sorted(Comparator.comparing(OrchestratorMeta::getUpdateTime)).collect(Collectors.toList());
        } else {
            orchestratorMetaList = orchestratorMetaList.stream().sorted(Comparator.comparing(OrchestratorMeta::getUpdateTime).reversed()).collect(Collectors.toList());
        }

        // 分页处理
        Integer page = orchestratorMetaRequest.getPageNow() >= 1 ? orchestratorMetaRequest.getPageNow() : 1;
        Integer pageSize = orchestratorMetaRequest.getPageSize() >= 1 ? orchestratorMetaRequest.getPageSize() : 10;
        Integer start = (page - 1) * pageSize;
        Integer end = page * pageSize > orchestratorMetaList.size() ? orchestratorMetaList.size() : page * pageSize;

        for (int i = start; i < end; i++) {
            OrchestratorMeta orchestratorMeta = orchestratorMetaList.get(i);
            orchestratorMeta.setStatusName(OrchestratorStatusEnum.getEnum(orchestratorMeta.getStatus()).getName());
            orchestratorMetaInfo.add(orchestratorMeta);
        }

        return orchestratorMetaInfo;
    }


    public void updateBmlResource(OrchestratorMeta orchestratorMeta, String username, DSSOrchestratorVersion orchestratorVersion) {
        // 查询dss工作流信息
        DSSFlow dssFlow = flowMapper.selectFlowByID(orchestratorVersion.getAppId());
        if (StringUtils.isEmpty(dssFlow.getCreator())) {
            dssFlow.setCreator(username);
        }
        String creator = dssFlow.getCreator();
        // 从Bml中获取json信息
        String flowJsonOld = getFlowJson(creator, orchestratorMeta.getProjectName(), dssFlow);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(flowJsonOld).getAsJsonObject();
        String proxyUser = orchestratorMeta.getProxyUser();
        // 传入的代理用户和数据库的代理用户不为NULL，才更新代理用户信息
        if (!StringUtils.isEmpty(proxyUser)) {
            // 更新user.to.proxy用户和proxyuser用户 信息
            JsonObject scheduleParams = jsonObject.getAsJsonObject("scheduleParams");
            scheduleParams.addProperty("proxyuser", proxyUser);

            JsonArray props = jsonObject.getAsJsonArray("props");
            // JsonArray 转list，是否包含 user.to.proxy key
            List<Map<String, Object>> propList = DSSCommonUtils.COMMON_GSON.fromJson(props,
                    new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
            int size = propList.stream().filter(map -> map.containsKey("user.to.proxy")).collect(Collectors.toList()).size();
            if (size == 0) {
                JsonObject element = new JsonObject();
                element.addProperty("user.to.proxy", proxyUser);
                props.add(element);
            } else {
                for (JsonElement prop : props) {
                    if (prop.getAsJsonObject().keySet().contains("user.to.proxy")) {
                        prop.getAsJsonObject().addProperty("user.to.proxy", proxyUser);
                    }
                }
            }
        }


        String jsonFlow = jsonObject.toString();
        String resourceId = dssFlow.getResourceId();
        Long parentFlowID = flowMapper.getParentFlowID(dssFlow.getId());
        // 这里不要检查ContextID具体版本等，只要存在就不创建 2020-0423
        jsonFlow = contextService.checkAndCreateContextID(jsonFlow, dssFlow.getBmlVersion(),
                orchestratorMeta.getWorkspaceName(), orchestratorMeta.getProjectName(), dssFlow.getName(), creator, false);

        if (isEqualTwoJson(flowJsonOld, jsonFlow)) {
            LOGGER.info("modifyOrchestratorMeta is not change");
            return;
        }

        saveFlowHook.beforeSave(jsonFlow, dssFlow, parentFlowID);
        Map<String, Object> bmlReturnMap = bmlService.update(creator, resourceId, jsonFlow);
        dssFlow.setHasSaved(true);
        dssFlow.setResourceId(bmlReturnMap.get("resourceId").toString());
        dssFlow.setBmlVersion(bmlReturnMap.get("version").toString());

        // 更新NodeMeta代理用户信息
        NodeMetaDO nodeMetaByOrchestrator = nodeMetaMapper.getNodeMetaByOrchestratorId(orchestratorMeta.getOrchestratorId());
        nodeMetaByOrchestrator.setProxyUser(orchestratorMeta.getProxyUser());
        nodeMetaMapper.updateNodeMeta(nodeMetaByOrchestrator);

        // 数据库增加版本更新
        flowMapper.updateFlowInputInfo(dssFlow);

        try {
            contextService.checkAndSaveContext(jsonFlow, String.valueOf(parentFlowID));
        } catch (DSSErrorException e) {
            LOGGER.error("Failed to saveContext: ", e);
            throw new DSSRuntimeException(e.getErrCode(), "保存ContextId失败，您可以尝试重新发布工作流！原因：" + ExceptionUtils.getRootCauseMessage(e), e);
        }
        saveFlowHook.afterSave(jsonFlow, dssFlow, parentFlowID);

        String version = bmlReturnMap.get("version").toString();
        // 对子工作流,需更新父工作流状态，以便提交
        Long updateFlowId = parentFlowID == null ? dssFlow.getId() : parentFlowID;
        updateTOSaveStatus(dssFlow.getProjectId(), updateFlowId);

    }

    public String getFlowJson(String userName, String projectName, DSSFlow dssFlow) {
        String flowExportSaveBasePath = IoUtils.generateIOPath(userName, projectName, "");
        String savePath = flowExportSaveBasePath + File.separator + dssFlow.getName() + File.separator + dssFlow.getName() + ".json";
        String flowJson = bmlService.downloadAndGetText(userName, dssFlow.getResourceId(), dssFlow.getBmlVersion(), savePath);
        return flowJson;
    }


    private void updateTOSaveStatus(Long projectId, Long flowID) {
        try {
            DSSProject projectInfo = DSSFlowEditLockManager.getProjectInfo(projectId);
            //仅对接入Git的项目 更新状态为 保存
            if (projectInfo.getAssociateGit() != null && projectInfo.getAssociateGit()) {
                Long rootFlowId = getRootFlowId(flowID);
                if (rootFlowId != null) {
                    OrchestratorVo orchestratorVo = RpcAskUtils.processAskException(getOrchestratorSender().ask(new RequestQuertByAppIdOrchestrator(rootFlowId)),
                            OrchestratorVo.class, RequestQueryByIdOrchestrator.class);
                    lockMapper.updateOrchestratorStatus(orchestratorVo.getDssOrchestratorInfo().getId(), OrchestratorRefConstant.FLOW_STATUS_SAVE);
                }
            }
        } catch (DSSErrorException e) {
            LOGGER.error("getProjectInfo failed by:", e);
            throw new DSSRuntimeException(e.getErrCode(), "更新工作流状态失败，您可以尝试重新保存工作流！原因：" + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    public Long getRootFlowId(Long flowId) {
        if (flowId == null) {
            return null;
        }
        DSSFlow dssFlow = flowMapper.selectFlowByID(flowId);
        if (dssFlow == null) {
            return null;
        }
        if (dssFlow.getRootFlow()) {
            return dssFlow.getId();
        } else {
            Long parentFlowID = flowMapper.getParentFlowID(flowId);
            return getRootFlowId(parentFlowID);
        }
    }

    public boolean isEqualTwoJson(String oldJsonNode, String newJsonNode) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(oldJsonNode).getAsJsonObject();
        jsonObject.remove("updateTime");
        jsonObject.remove("comment");
        String tempOldJson = gson.toJson(jsonObject);

        JsonObject jsonObject2 = parser.parse(newJsonNode).getAsJsonObject();
        jsonObject2.remove("updateTime");
        jsonObject2.remove("comment");
        String tempNewJson = gson.toJson(jsonObject2);
        return tempOldJson.equals(tempNewJson);
    }

}
