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

import cn.hutool.core.collection.CollUtil;
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
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.common.protocol.project.*;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitCommitInfoBetweenRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitRemoveRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitRenameRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitCommitResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitHistoryResponse;
import com.webank.wedatasphere.dss.orchestrator.common.entity.*;
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

import com.webank.wedatasphere.dss.workflow.dao.*;
import com.webank.wedatasphere.dss.workflow.dto.NodeContentDO;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.SaveFlowHook;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
    @Autowired
    private NodeContentMapper nodeContentMapper;
    @Autowired
    private NodeContentUIMapper nodeContentUIMapper;
    private static ContextService contextService = ContextServiceImpl.getInstance();
    @Autowired
    private DSSFlowService flowService;
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
        Integer dssProjectWorkspaceId = dssProject.getWorkspaceId();
        long workspaceId = workspace.getWorkspaceId();
        if(dssProjectWorkspaceId == null || !String.valueOf(dssProjectWorkspaceId).equals(String.valueOf(workspaceId))){
            LOGGER.error("createOrchestrator projectId is {},project workspaceId is {}, workspace params is {}",dssProject.getId(),dssProject.getName(), workspaceId);
            DSSFrameworkErrorException.dealErrorException(60000, "无法在当前工作空间创建工作流,"+dssProject.getName()+"项目不属于"+workspace.getWorkspaceName()+"工作空间");
        }
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
        dssOrchestratorInfo.setWorkspaceId(workspaceId);
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
        if(dssProject.getWorkspaceId() == null || !String.valueOf(dssProject.getWorkspaceId()).equals(String.valueOf(workspace.getWorkspaceId()))){
            LOGGER.error("modifyOrchestrator projectId is {},project workspaceId is {}, workspace params is {}",dssProject.getId(),dssProject.getName(),workspace.getWorkspaceId());
            DSSFrameworkErrorException.dealErrorException(60000, "无法在当前工作空间修改工作流,"+dssProject.getName()+"项目不属于"+workspace.getWorkspaceName()+"工作空间");
        }
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
        // 清空BML
        orchestratorMapper.updateOrchestratorBmlVersion(orchestratorId, null, null);
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
        Long orchestratorInfoId = orchestratorInfo.getId();
        DSSOrchestratorVersion versionById = orchestratorMapper.getLatestOrchestratorVersionById(orchestratorInfoId);
        if (dssProject.getAssociateGit() != null && dssProject.getAssociateGit()) {
            try {
                // git删除成功之后再删除库表记录
                Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
                List<String> path = new ArrayList<>();
                path.add(orchestratorInfo.getName());
                GitRemoveRequest removeRequest = new GitRemoveRequest(workspace.getWorkspaceId(), dssProject.getName(), path, username);
                GitCommitResponse commitResponse = RpcAskUtils.processAskException(sender.ask(removeRequest), GitCommitResponse.class, GitRemoveRequest.class);
                lockMapper.updateOrchestratorStatus(orchestratorDeleteRequest.getId(), OrchestratorRefConstant.FLOW_STATUS_PUSH);
                if (versionById != null) {
                    lockMapper.updateOrchestratorVersionCommitId(commitResponse.getCommitId(), versionById.getAppId());
                }
            } catch (Exception e) {
                LOGGER.error("git remove failed, the reason is: ", e);
                lockMapper.updateOrchestratorStatus(orchestratorDeleteRequest.getId(), OrchestratorRefConstant.FLOW_STATUS_SAVE);
                throw new GitErrorException(800001, e.getMessage());
            }
        }
        orchestratorService.deleteOrchestrator(username, workspace, dssProject.getName(), orchestratorInfoId, dssLabels);
        orchestratorOperateService.deleteTemplateOperate(orchestratorInfoId);
        LOGGER.info("delete orchestrator {} by orchestrator framework succeed.", orchestratorInfo.getName());
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
        orchestratorVo.setOrchestratorName(orchestratorInfo.getName());
        orchestratorVo.setOrchestratorId(orchestratorInfoId);

        // 同步更新flowJson
        List<NodeContentDO> contentDOS = nodeContentMapper.getContentListByOrchestratorIdAndFlowId(orchestratorInfoId, versionById.getAppId());
        if (CollectionUtils.isEmpty(contentDOS)) {
            return orchestratorVo;
        }
        List<Long> contentIdList = contentDOS.stream().map(NodeContentDO::getId).collect(Collectors.toList());
        nodeContentMapper.deleteNodeContentByOrchestratorIdAndFlowId(orchestratorInfoId, versionById.getAppId());
        if (CollectionUtils.isNotEmpty(contentIdList)) {
            nodeContentUIMapper.deleteNodeContentUIByContentList(contentIdList);
        }
        nodeMetaMapper.deleteNodeMetaByOrchestratorId(orchestratorInfoId);
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
        orchestratorCopyJob.setTargetProject(targetProject);
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

        if(dssProject.getWorkspaceId() == null || !String.valueOf(dssProject.getWorkspaceId()).equals(String.valueOf(workspace.getWorkspaceId()))){
            LOGGER.error("modifyOrchestratorMeta projectId is {},project workspaceId is {}, workspace params is {}",dssProject.getId(),dssProject.getName(),workspace.getWorkspaceId());
            DSSFrameworkErrorException.dealErrorException(60000, "无法在当前工作空间修改工作流,"+dssProject.getName()+"项目不属于"+workspace.getWorkspaceName()+"工作空间");
        }

        workspace.setWorkspaceName(dssProject.getWorkspaceName());

        OrchestratorModifyRequest orchestratorModifyRequest = new OrchestratorModifyRequest();
        orchestratorModifyRequest.setOrchestratorName(modifyOrchestratorMetaRequest.getOrchestratorName());
        orchestratorModifyRequest.setProjectId(modifyOrchestratorMetaRequest.getProjectId());
        orchestratorModifyRequest.setId(modifyOrchestratorMetaRequest.getOrchestratorId());
        orchestratorModifyRequest.setWorkspaceId(workspace.getWorkspaceId());

        orchestratorService.isExistSameNameBeforeUpdate(orchestratorModifyRequest, dssProject, username);

        OrchestratorMeta orchestratorMetaInfo = getOrchestratorMetaInfo(modifyOrchestratorMetaRequest, dssProject, username);

        DSSOrchestratorRelation dssOrchestratorRelation = DSSOrchestratorRelationManager.getDSSOrchestratorRelationByMode(orchestratorMetaInfo.getOrchestratorMode());

        DSSOrchestratorInfo dssOrchestratorInfo = getDssOrchestratorInfo(orchestratorMetaInfo, dssOrchestratorRelation);

        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(DSSCommonUtils.ENV_LABEL_VALUE_DEV));
        //1.如果调度系统要求同步创建工作流，向调度系统发送更新工作流的请求
        tryOrchestrationOperation(dssLabels, false, username, dssProject.getName(), workspace, dssOrchestratorInfo,
                OrchestrationService::getOrchestrationUpdateOperation,
                (structureOperation, structureRequestRef) -> ((OrchestrationUpdateOperation) structureOperation)
                        .updateOrchestration((OrchestrationUpdateRequestRef) structureRequestRef), "update");

        updateBmlResource(orchestratorMetaInfo, username, orchestratorVersion,dssProject);

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
        if(StringUtils.isEmpty(orchestratorMetaInfo.getIsDefaultReference())){
            dssOrchestratorInfo.setIsDefaultReference(null);
        }else{
            dssOrchestratorInfo.setIsDefaultReference(orchestratorMetaInfo.getIsDefaultReference());
        }
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
    public List<OrchestratorMeta> getAllOrchestratorMeta(OrchestratorMetaRequest orchestratorMetaRequest, List<Long> total, String username) {
        List<OrchestratorMeta> orchestratorMetaList = orchestratorMapper.getAllOrchestratorMeta(orchestratorMetaRequest);
        List<OrchestratorMeta> orchestratorMetaInfo = new ArrayList<>();
        if (CollectionUtils.isEmpty(orchestratorMetaList)) {
            total.add(0L);
            return orchestratorMetaInfo;
        }

        List<Long> orchestratorIdList = orchestratorMetaList.stream().map(OrchestratorMeta::getOrchestratorId).collect(Collectors.toList());
        List<OrchestratorReleaseVersionInfo> releaseVersionInfos = orchestratorMapper.getOrchestratorReleaseVersionInfo(orchestratorIdList);

        Map<Long,OrchestratorReleaseVersionInfo> versionMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(releaseVersionInfos)) {
            // 根据orchestratorId 分组
            Map<Long,List<OrchestratorReleaseVersionInfo>>  map = releaseVersionInfos.stream()
                    .collect(Collectors.groupingBy(OrchestratorReleaseVersionInfo::getOrchestratorId));
            // 取编排的第一条记录
            for(Long orchestratorId: map.keySet()){

                OrchestratorReleaseVersionInfo  orchestratorReleaseVersionInfo = map.get(orchestratorId).stream()
                        .max(Comparator.comparing(OrchestratorReleaseVersionInfo::getReleaseTaskId)).orElse(null);

                if(orchestratorReleaseVersionInfo == null){
                    continue;
                }

                versionMap.put(orchestratorId,orchestratorReleaseVersionInfo);
            }

        }
        // 获取模板名称
        List<OrchestratorTemplateInfo> templateInfos = orchestratorMapper.getOrchestratorDefaultTemplateInfo(orchestratorIdList);
        Map<Long, String> templateMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(templateInfos)) {
            // 分组 拼接模板名称
            templateMap = templateInfos.stream().collect(Collectors.groupingBy(OrchestratorTemplateInfo::getOrchestratorId,
                    Collectors.mapping(OrchestratorTemplateInfo::getTemplateName, Collectors.joining(","))));
        }

        List<OrchestratorSubmitJob> orchestratorSubmitJobList = orchestratorMapper.getSubmitJobStatus(orchestratorIdList);
        Map<Long,OrchestratorSubmitJob> submitJobMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(orchestratorSubmitJobList)) {
            // 分组排序 获取编排最新的第一条记录信息
            submitJobMap = orchestratorSubmitJobList.stream()
                    .collect(Collectors.groupingBy(OrchestratorSubmitJob::getOrchestratorId)).values()
                    .stream()
                    .flatMap(v -> Stream.of(v.stream().max(Comparator.comparing(OrchestratorSubmitJob::getId)).get()))
                    .collect(Collectors.toMap(OrchestratorSubmitJob::getOrchestratorId, orchestratorSubmitJob -> orchestratorSubmitJob));

        }

        for (OrchestratorMeta orchestratorMeta : orchestratorMetaList) {

            OrchestratorReleaseVersionInfo releaseVersion = versionMap.getOrDefault(orchestratorMeta.getOrchestratorId(),
                    new OrchestratorReleaseVersionInfo());

            OrchestratorSubmitJob orchestratorSubmitJob =  submitJobMap.get(orchestratorMeta.getOrchestratorId());

            orchestratorMeta.setVersion(releaseVersion.getVersion());
            orchestratorMeta.setUpdateTime(releaseVersion.getReleaseTime());
            orchestratorMeta.setUpdateUser(releaseVersion.getReleaseUser());
            orchestratorStatus(orchestratorMeta,orchestratorSubmitJob,releaseVersion);

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
        try {
            if (DSSOrchestratorConstant.ASCEND.equalsIgnoreCase(orchestratorMetaRequest.getOrderBy())) {
                orchestratorMetaList = orchestratorMetaList.stream().sorted(Comparator.comparing(OrchestratorMeta::getUpdateTime, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
            } else {
                orchestratorMetaList = orchestratorMetaList.stream().sorted(Comparator.comparing(OrchestratorMeta::getUpdateTime, Comparator.nullsFirst(Comparator.naturalOrder())).reversed()).collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("排序失败， 原因为：", e);
        }

        // 分页处理
        int page = orchestratorMetaRequest.getPageNow() >= 1 ? orchestratorMetaRequest.getPageNow() : 1;
        int pageSize = orchestratorMetaRequest.getPageSize() >= 1 ? orchestratorMetaRequest.getPageSize() : 10;
        int start = (page - 1) * pageSize;
        int end = Math.min(page * pageSize , orchestratorMetaList.size());
        Map<Long, Boolean> map = new HashMap<>();
        for (int i = start; i < end; i++) {
            OrchestratorMeta orchestratorMeta = orchestratorMetaList.get(i);
            orchestratorMeta.setStatusName(OrchestratorStatusEnum.getEnum(orchestratorMeta.getStatus()).getName());

            if (!map.containsKey(orchestratorMeta.getProjectId())) {

                ProjectUserAuthResponse projectUserAuthResponse = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance()
                                .getProjectServerSender().ask(new ProjectUserAuthRequest(orchestratorMeta.getProjectId(), username)),
                        ProjectUserAuthResponse.class, ProjectUserAuthRequest.class);
                boolean isEditable = projectUserAuthResponse.getProjectOwner().equals(username);
                if (!isEditable && !CollectionUtils.isEmpty(projectUserAuthResponse.getPrivList())) {
                    isEditable = projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_EDIT.getRank())
                            || projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_RELEASE.getRank());
                }

                map.put(orchestratorMeta.getProjectId(), isEditable);
            }

            orchestratorMeta.setEditable(map.get(orchestratorMeta.getProjectId()));

            orchestratorMetaInfo.add(orchestratorMeta);
        }

        return orchestratorMetaInfo;
    }



    public void updateBmlResource(OrchestratorMeta orchestratorMeta, String username, DSSOrchestratorVersion orchestratorVersion, DSSProject dssProject) throws  Exception {
        // 查询dss工作流信息
        DSSFlow dssFlow = flowMapper.selectFlowByID(orchestratorVersion.getAppId());
        if (StringUtils.isEmpty(dssFlow.getCreator())) {
            dssFlow.setCreator(username);
        }
        String creator = dssFlow.getCreator();
        // 从Bml中获取json信息
        String flowJsonOld = flowService.getFlowJson(creator, orchestratorMeta.getProjectName(), dssFlow);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(flowJsonOld).getAsJsonObject();
        String proxyUser = orchestratorMeta.getProxyUser();

        if(!jsonObject.keySet().contains("nodes")){
            LOGGER.error("flowJson not contains nodes, json is {}",flowJsonOld);
            throw new DSSRuntimeException(6005, orchestratorMeta.getOrchestratorName()+ " 工作流未进行保存,请保存后在重新编辑！！！");
        }

        JsonObject scheduleParams = jsonObject.getAsJsonObject("scheduleParams");
        scheduleParams.addProperty("proxyuser", proxyUser);

        // 更新user.to.proxy用户和proxyuser用户 信息
        JsonArray props = jsonObject.getAsJsonArray("props");
        if(props == null){
            props = new JsonArray();
            jsonObject.add("props", props);
        }
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

        String jsonFlow = jsonObject.toString();

        flowService.saveFlow(orchestratorVersion.getAppId(),jsonFlow
                ,dssFlow.getDescription(),username
                ,dssProject.getWorkspaceName(),dssProject.getName(),null);

    }


    public void getGitOrchestratorSubmitStatus(OrchestratorSubmitJob orchestratorSubmitJob,OrchestratorMeta orchestratorMeta){

        if (OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED.equalsIgnoreCase(orchestratorSubmitJob.getStatus())) {
            // 提交失败 -> 待提交
            orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_SAVE);
            orchestratorMeta.setErrorMsg(orchestratorSubmitJob.getErrorMsg());

        } else if (OrchestratorRefConstant.FLOW_STATUS_PUSHING.equalsIgnoreCase(orchestratorSubmitJob.getStatus())) {
            // 提交中
            orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUSHING);
        } else {
            // 待提交
            orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_SAVE);
        }

    }


    public void getGitOrchestratorReleaseStatus(OrchestratorReleaseVersionInfo releaseVersion,OrchestratorMeta orchestratorMeta){

        // 查询 dss_release_task，根据编排Id获取
        if (OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED.equalsIgnoreCase(releaseVersion.getStatus())) {
            // 发布失败 -> 待发布
            orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUSH);
            orchestratorMeta.setErrorMsg(releaseVersion.getErrorMsg());
            orchestratorMeta.setNewStatus(OrchestratorStatusEnum.FAILED.getStatus());
            orchestratorMeta.setNewStatusName(OrchestratorStatusEnum.FAILED.getName());

        } else if (OrchestratorRefConstant.FLOW_STATUS_PUSHING.equalsIgnoreCase(releaseVersion.getStatus())) {
            // 发布中状态
            orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUBLISHING);

        }else if (OrchestratorRefConstant.FLOW_STATUS_PUBLISH.equalsIgnoreCase(orchestratorMeta.getStatus())
                && OrchestratorRefConstant.FLOW_STATUS_PUSH_SUCCESS.equalsIgnoreCase(releaseVersion.getStatus())) {

            orchestratorMeta.setStatus(OrchestratorStatusEnum.PUBLISH.getStatus());
            orchestratorMeta.setNewStatus(OrchestratorStatusEnum.SUCCESS.getStatus());
            orchestratorMeta.setNewStatusName(OrchestratorStatusEnum.SUCCESS.getName());

        } else{
            // 待发布
            orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUSH);
        }
    }


    public OrchestratorMeta getOrchestratorMetaInfo(ModifyOrchestratorMetaRequest modifyOrchestratorMetaRequest, DSSProject dssProject, String username) throws DSSFrameworkErrorException {

        OrchestratorMeta orchestratorInfo = orchestratorMapper.getOrchestratorMeta(modifyOrchestratorMetaRequest.getOrchestratorId());

        if (orchestratorInfo == null) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排模式ID=" + modifyOrchestratorMetaRequest.getOrchestratorId() + "不存在");
        }

        OrchestratorReleaseVersionInfo releaseVersion = orchestratorMapper.getOrchestratorVersionById(orchestratorInfo.getOrchestratorId());
        OrchestratorSubmitJob orchestratorSubmitJob = orchestratorMapper.selectSubmitJobStatus(orchestratorInfo.getOrchestratorId());
        orchestratorStatus(orchestratorInfo,orchestratorSubmitJob,releaseVersion);

        // 发布中和提交中的工作流不允许进行更新
        if (OrchestratorRefConstant.FLOW_STATUS_PUSHING.equalsIgnoreCase(orchestratorInfo.getStatus())
                || OrchestratorRefConstant.FLOW_STATUS_PUBLISHING.equalsIgnoreCase(orchestratorInfo.getStatus())) {

            DSSFrameworkErrorException.dealErrorException(60000,
                    String.format("%s工作流正在提交中或发布中，不允许进行编辑,请稍后重试", modifyOrchestratorMetaRequest.getOrchestratorName()));

        }

        //若修改了编排名称，检查是否存在相同的编排名称
        if (!modifyOrchestratorMetaRequest.getOrchestratorName().equals(orchestratorInfo.getOrchestratorName())) {
            orchestratorService.isExistSameNameBeforeCreate(modifyOrchestratorMetaRequest.getWorkspaceId(), modifyOrchestratorMetaRequest.getProjectId(), modifyOrchestratorMetaRequest.getOrchestratorName());

            if (dssProject.getAssociateGit() != null && dssProject.getAssociateGit()) {
                Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
                GitRenameRequest renameRequest = new GitRenameRequest(modifyOrchestratorMetaRequest.getWorkspaceId(), dssProject.getName(),
                        modifyOrchestratorMetaRequest.getOrchestratorName(), modifyOrchestratorMetaRequest.getOrchestratorName(), username);
                RpcAskUtils.processAskException(sender.ask(renameRequest), GitCommitResponse.class, GitRenameRequest.class);
            }
        }

        // 修改前端传入的信息, 默认模板调用saveTemplateRef接口进行保存
        orchestratorInfo.setOrchestratorName(modifyOrchestratorMetaRequest.getOrchestratorName());
        orchestratorInfo.setDescription(modifyOrchestratorMetaRequest.getDescription());
        orchestratorInfo.setIsDefaultReference(modifyOrchestratorMetaRequest.getIsDefaultReference());
        if (!Objects.equals(orchestratorInfo.getProxyUser(), modifyOrchestratorMetaRequest.getProxyUser())) {
            orchestratorInfo.setProxyUser(modifyOrchestratorMetaRequest.getProxyUser());
        }
        orchestratorInfo.setUpdateUser(username);
        orchestratorInfo.setUpdateTime(new Date(System.currentTimeMillis()));

        return orchestratorInfo;

    }



    public void orchestratorStatus(OrchestratorMeta orchestratorMeta,OrchestratorSubmitJob orchestratorSubmitJob, OrchestratorReleaseVersionInfo releaseVersion){

        /*
         * 对于接入git的项目下，并且当前状态为save的编排，dss_orchestrator_submit_job_info根据编排Id获取最新的提交记录，
         * 对于当前状态为push、publish或者为空的，查询 dss_release_task，根据编排Id获取
         *
         * 非git项目的工作流只有：发布中、无状态
         * git项目下的工作流才有这四个状态：待提交 待发布 提交中 发布中
         *
         *  git项目Failed就展示 待发布 待提交
         * 非git项目Failed就无状态
         *
         * **/

        if (orchestratorMeta.getAssociateGit() != null && orchestratorMeta.getAssociateGit()) {
            //  git项目下的工作流才有这四个状态：待提交 待发布 提交中 发布中
            if(OrchestratorRefConstant.FLOW_STATUS_SAVE.equalsIgnoreCase(orchestratorMeta.getStatus()) && orchestratorSubmitJob!= null){

                getGitOrchestratorSubmitStatus(orchestratorSubmitJob,orchestratorMeta);

            }else if (StringUtils.isBlank(orchestratorMeta.getStatus())
                    || OrchestratorRefConstant.FLOW_STATUS_PUSH.equalsIgnoreCase(orchestratorMeta.getStatus())
                    || OrchestratorRefConstant.FLOW_STATUS_PUBLISH.equalsIgnoreCase(orchestratorMeta.getStatus())) {

                getGitOrchestratorReleaseStatus(releaseVersion,orchestratorMeta);

            }else{
                orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_SAVE);
            }


            // 获取最近一次的发布状态 |发布成功 or 发布失败 or 未发布
            if(StringUtils.isEmpty(orchestratorMeta.getNewStatus())){

                // 未发布
                if(StringUtils.isEmpty(releaseVersion.getStatus())){
                    orchestratorMeta.setNewStatus(OrchestratorStatusEnum.UNPUBLISHED.getStatus());
                    orchestratorMeta.setNewStatusName(OrchestratorStatusEnum.UNPUBLISHED.getName());
                    orchestratorMeta.setVersion(null);

                }else if (OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED.equalsIgnoreCase(releaseVersion.getStatus())){

                    orchestratorMeta.setNewStatus(OrchestratorStatusEnum.FAILED.getStatus());
                    orchestratorMeta.setNewStatusName(OrchestratorStatusEnum.FAILED.getName());
                    orchestratorMeta.setErrorMsg(releaseVersion.getErrorMsg());

                } else if (OrchestratorRefConstant.FLOW_STATUS_PUSH_SUCCESS.equalsIgnoreCase(releaseVersion.getStatus())) {

                    orchestratorMeta.setNewStatus(OrchestratorStatusEnum.SUCCESS.getStatus());
                    orchestratorMeta.setNewStatusName(OrchestratorStatusEnum.SUCCESS.getName());
                }

            }

        }else {
            // 非git项目的工作流只有：发布中、无状态
            if (OrchestratorRefConstant.FLOW_STATUS_PUSHING.equalsIgnoreCase(releaseVersion.getStatus())) {
                // 发布中
                orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUBLISHING);

            } else if (OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED.equalsIgnoreCase(releaseVersion.getStatus())) {
                // 发布失败 -> 无状态
                orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_STATELESS);
                orchestratorMeta.setErrorMsg(releaseVersion.getErrorMsg());
                orchestratorMeta.setNewStatus(OrchestratorStatusEnum.FAILED.getStatus());
                orchestratorMeta.setNewStatusName(OrchestratorStatusEnum.FAILED.getName());

            } else if (OrchestratorRefConstant.FLOW_STATUS_PUSH_SUCCESS.equalsIgnoreCase(releaseVersion.getStatus())) {

                orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_STATELESS);
                orchestratorMeta.setNewStatus(OrchestratorStatusEnum.SUCCESS.getStatus());
                orchestratorMeta.setNewStatusName(OrchestratorStatusEnum.SUCCESS.getName());

            } else {
                // 无状态
                orchestratorMeta.setStatus(OrchestratorRefConstant.FLOW_STATUS_STATELESS);
                // 未进行发布的工作流，为未发布状态
                if (StringUtils.isEmpty(releaseVersion.getStatus())) {
                    orchestratorMeta.setNewStatus(OrchestratorStatusEnum.UNPUBLISHED.getStatus());
                    orchestratorMeta.setNewStatusName(OrchestratorStatusEnum.UNPUBLISHED.getName());
                    orchestratorMeta.setVersion(null);
                }
            }
        }
    }

}
