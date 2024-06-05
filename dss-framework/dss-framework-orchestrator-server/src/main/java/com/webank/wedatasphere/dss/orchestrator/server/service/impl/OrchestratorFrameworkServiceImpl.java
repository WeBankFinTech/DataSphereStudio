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
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.protocol.project.*;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorRefOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.type.DSSOrchestratorRelation;
import com.webank.wedatasphere.dss.orchestrator.core.type.DSSOrchestratorRelationManager;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorCopyJobMapper;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


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

    private static final int MAX_DESC_LENGTH = 250;
    private static final int MAX_NAME_LENGTH = 128;

    private final ThreadFactory orchestratorCopyThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("dss-orchestrator—copy-thread-%d")
            .setDaemon(false)
            .build();

    private final ExecutorService orchestratorCopyThreadPool = new ThreadPoolExecutor(0, 200, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024), orchestratorCopyThreadFactory, new ThreadPoolExecutor.AbortPolicy());

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
        if(orchestratorCreateRequest.getDescription().length() > MAX_DESC_LENGTH){
            DSSFrameworkErrorException.dealErrorException(60000, "描述过长，请限制在" + MAX_DESC_LENGTH + "以内");
        }
        if(orchestratorCreateRequest.getOrchestratorName().length() > MAX_NAME_LENGTH){
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

    public <K extends StructureRequestRef, V extends ResponseRef> V tryOrchestrationOperation(List<DSSLabel> dssLabels, Boolean askProjectSender, String userName, String projectName,
                                                                                               Workspace workspace, DSSOrchestratorInfo dssOrchestrator,
                                                                                               Function<OrchestrationService, StructureOperation> getOrchestrationOperation,
                                                                                               BiFunction<StructureOperation, K, V> responseRefConsumer, String operationName) {
        ImmutablePair<OrchestrationService, AppInstance> orchestrationPair = getOrchestrationService(dssOrchestrator, userName, workspace, dssLabels);
        Long refProjectId, refOrchestrationId;
        if (askProjectSender) {
            ProjectRefIdResponse projectRefIdResponse = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                    .ask(new ProjectRefIdRequest(Optional.ofNullable(orchestrationPair).map(ImmutablePair::getValue).map(AppInstance::getId).orElse(null), dssOrchestrator.getProjectId())), ProjectRefIdResponse.class, ProjectRefIdRequest.class);
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
        if(orchestratorModifyRequest.getDescription().length() > MAX_DESC_LENGTH){
            DSSFrameworkErrorException.dealErrorException(60000, "描述字段过长，请限制在" + MAX_DESC_LENGTH + "以内");
        }
        if(orchestratorModifyRequest.getOrchestratorName().length() > MAX_NAME_LENGTH){
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称过长，请限制在" + MAX_NAME_LENGTH + "以内");
        }
        //判断工程是否存在,并且取出工程名称和空间名称
        DSSProject dssProject = validateOperation(orchestratorModifyRequest.getProjectId(), username);
        workspace.setWorkspaceName(dssProject.getWorkspaceName());
        //是否存在相同的编排名称 //todo 返回orchestratorInfo而不是id
        Long orchestratorId = orchestratorService.isExistSameNameBeforeUpdate(orchestratorModifyRequest);
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
        if(orchestratorDeleteRequest.getDeleteSchedulerWorkflow()) {
            tryOrchestrationOperation(dssLabels, false, username, dssProject.getName(), workspace, orchestratorInfo,
                    OrchestrationService::getOrchestrationDeletionOperation,
                    (structureOperation, structureRequestRef) -> ((OrchestrationDeletionOperation) structureOperation)
                            .deleteOrchestration((RefOrchestrationContentRequestRef) structureRequestRef), "delete");
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
    public String copyOrchestrator(String username, OrchestratorCopyRequest orchestratorCopyRequest, Workspace workspace) throws Exception{
        if(orchestratorCopyRequest.getTargetOrchestratorName().length() > MAX_NAME_LENGTH){
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
        if (orchestratorCopyRequest.getLabels()!= null && orchestratorCopyRequest.getLabels().getRoute() != null) {
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

        List < OrchestratorCopyHistory > orchestratorCopyHistoryList = new ArrayList<>();
        OrchestratorCopyHistory orchestratorCopyHistory;
        for (DSSOrchestratorCopyInfo orchestratorCopyInfo: orchestratorCopyInfoList) {
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
        if (appConn != null) {
            AppInstance appInstance = appConn.getAppDesc().getAppInstances().get(0);
            return new ImmutablePair<>(appConn.getOrCreateStructureStandard().getOrchestrationService(appInstance), appInstance);
        } else {
            return new ImmutablePair<>(null, null);
        }
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

}
