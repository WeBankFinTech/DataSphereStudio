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

import com.github.pagehelper.PageHelper;
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
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.protocol.project.*;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorRefOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestOrchestratorInfos;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.type.DSSOrchestratorRelation;
import com.webank.wedatasphere.dss.orchestrator.core.type.DSSOrchestratorRelationManager;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorCopyJobMapper;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.publish.ExportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.ImportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.*;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorCopyHistory;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorCopyVo;
import com.webank.wedatasphere.dss.orchestrator.server.job.OrchestratorCopyEnv;
import com.webank.wedatasphere.dss.orchestrator.server.job.OrchestratorCopyJob;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorFrameworkService;
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
import org.apache.linkis.protocol.util.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


public class OrchestratorFrameworkServiceImpl implements OrchestratorFrameworkService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final SimpleDateFormat SDF = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

    @Autowired
    private OrchestratorMapper orchestratorMapper;
    @Autowired
    private OrchestratorService newOrchestratorService;
    @Autowired
    private OrchestratorManager orchestratorManager;
    @Autowired
    private OrchestratorCopyJobMapper orchestratorCopyJobMapper;
    @Autowired
    private OrchestratorCopyEnv orchestratorCopyEnv;

    private final ThreadFactory orchestratorCopyThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("dss-orchestrator—copy-thread-%d")
            .setDaemon(false)
            .build();

    private final ExecutorService orchestratorCopyThreadPool = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MILLISECONDS,
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
        //是否存在相同的编排名称
        newOrchestratorService.isExistSameNameBeforeCreate(orchestratorCreateRequest.getWorkspaceId(), orchestratorCreateRequest.getProjectId(), orchestratorCreateRequest.getOrchestratorName());
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
        //1.去orchestratorFramework创建编排模式
        LOGGER.info("{} begins to create a orchestrator {}.", username, orchestratorCreateRequest);
        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(orchestratorCreateRequest.getLabels().getRoute()));
        //2.如果调度系统要求同步创建工作流，向调度系统发送创建工作流的请求
        OrchestrationResponseRef orchestrationResponseRef = tryOrchestrationOperation(dssLabels, true, username,
                dssProject.getName(), workspace, dssOrchestratorInfo,
                OrchestrationService::getOrchestrationCreationOperation,
                (structureOperation, structureRequestRef) -> ((OrchestrationCreationOperation) structureOperation)
                        .createOrchestration((DSSOrchestrationContentRequestRef) structureRequestRef), "create");
        OrchestratorVo orchestratorVo = newOrchestratorService.createOrchestrator(username, workspace, dssProject.getName(),
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

    private <K extends StructureRequestRef, V extends ResponseRef> V tryOrchestrationOperation(List<DSSLabel> dssLabels, Boolean askProjectSender, String userName, String projectName,
                                                                                               Workspace workspace, DSSOrchestratorInfo dssOrchestrator,
                                                                                               Function<OrchestrationService, StructureOperation> getOrchestrationOperation,
                                                                                               BiFunction<StructureOperation, K, V> responseRefConsumer, String operationName) {
        ImmutablePair<OrchestrationService, AppInstance> orchestrationPair = getOrchestrationService(dssOrchestrator, userName, workspace, dssLabels);
        Long refProjectId, refOrchestrationId;
        if (askProjectSender) {
            ProjectRefIdResponse projectRefIdResponse = (ProjectRefIdResponse) DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                    .ask(new ProjectRefIdRequest(orchestrationPair.getValue().getId(), dssOrchestrator.getProjectId()));
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
        //判断工程是否存在,并且取出工程名称和空间名称
        DSSProject dssProject = validateOperation(orchestratorModifyRequest.getProjectId(), username);
        workspace.setWorkspaceName(dssProject.getWorkspaceName());
        //是否存在相同的编排名称 //todo 返回orchestratorInfo而不是id
        Long orchestratorId = newOrchestratorService.isExistSameNameBeforeUpdate(orchestratorModifyRequest);
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
        //1.如果调度系统要求同步创建工作流，向调度系统发送更新工作流的请求
        tryOrchestrationOperation(dssLabels, false, username, dssProject.getName(), workspace, dssOrchestratorInfo,
                OrchestrationService::getOrchestrationUpdateOperation,
                (structureOperation, structureRequestRef) -> ((OrchestrationUpdateOperation) structureOperation)
                        .updateOrchestration((OrchestrationUpdateRequestRef) structureRequestRef), "update");
        newOrchestratorService.updateOrchestrator(username, workspace, dssOrchestratorInfo, dssLabels);
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
        tryOrchestrationOperation(dssLabels, false, username, dssProject.getName(), workspace, orchestratorInfo,
                OrchestrationService::getOrchestrationDeletionOperation,
                (structureOperation, structureRequestRef) -> ((OrchestrationDeletionOperation) structureOperation)
                        .deleteOrchestration((RefOrchestrationContentRequestRef) structureRequestRef), "delete");

        newOrchestratorService.deleteOrchestrator(username, workspace, dssProject.getName(), orchestratorInfo.getId(), dssLabels);
        LOGGER.info("delete orchestrator {} by orchestrator framework succeed.", orchestratorInfo.getName());
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
        orchestratorVo.setOrchestratorVersion(orchestratorInfo.getOrchestratorLevel());
        orchestratorVo.setOrchestratorName(orchestratorInfo.getName());
        orchestratorVo.setOrchestratorId(orchestratorInfo.getId());
        return orchestratorVo;
    }

    @Override
    public void copyOrchestrator(String username, OrchestratorCopyRequest orchestratorCopyRequest, Workspace workspace) throws Exception{
        //校验编排名是可用
        newOrchestratorService.isExistSameNameBeforeCreate(workspace.getWorkspaceId(), orchestratorCopyRequest.getTargetProjectId(), orchestratorCopyRequest.getTargetOrchestratorName());
        //判断用户对项目是否有权限
        DSSProject sourceProject = validateOperation(orchestratorCopyRequest.getSourceProjectId(), username);
        DSSProject targetProject = validateOperation(orchestratorCopyRequest.getTargetProjectId(), username);

        DSSOrchestratorInfo sourceOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorCopyRequest.getSourceOrchestratorId());
        if (sourceOrchestratorInfo == null) {
            LOGGER.error("orchestrator: {} not found.", orchestratorCopyRequest.getSourceOrchestratorName());
        }

        OrchestratorCopyVo orchestratorCopyVo = new OrchestratorCopyVo();
        orchestratorCopyVo.setOrchestrator(sourceOrchestratorInfo);
        orchestratorCopyVo.setUsername(username);
        orchestratorCopyVo.setDssLabel(new EnvDSSLabel(DSSCommonUtils.ENV_LABEL_VALUE_DEV));
        orchestratorCopyVo.setTargetOrchestratorName(orchestratorCopyRequest.getTargetOrchestratorName());
        orchestratorCopyVo.setSourceProjectId(sourceProject.getId());
        orchestratorCopyVo.setWorkspace(workspace);
        orchestratorCopyVo.setSourceProjectName(sourceProject.getName());
        orchestratorCopyVo.setTargetProjectId(targetProject.getId());
        orchestratorCopyVo.setCopyTaskId(null);
        orchestratorCopyVo.setWorkflowNodeSuffix(orchestratorCopyRequest.getWorkflowNodeSuffix());
        orchestratorCopyVo.setTargetProjectName(targetProject.getName());

        OrchestratorCopyJob orchestratorCopyJob = new OrchestratorCopyJob();
        orchestratorCopyJob.setOrchestratorCopyVo(orchestratorCopyVo);
        orchestratorCopyJob.setOrchestratorCopyEnv(orchestratorCopyEnv);
        orchestratorCopyThreadPool.submit(orchestratorCopyJob);
    }

    @Override
    public List<OrchestratorCopyHistory> getOrchestratorCopyHistory(String username, Workspace workspace, Long orchestratorId, Integer currentPage, Integer pageSize) throws Exception {
        if (currentPage == null) {
            currentPage = 1;
        }
        if (pageSize == null) {
            pageSize = 4;
        }
        PageHelper.startPage(currentPage, pageSize);

        List<DSSOrchestratorCopyInfo> orchestratorCopyInfoList = orchestratorCopyJobMapper.getOrchestratorCopyInfoList(orchestratorId);

        if (CollectionUtils.isEmpty(orchestratorCopyInfoList)){
            return Lists.newArrayList();
        }
        List<OrchestratorCopyHistory> orchestratorCopyHistoryList = new ArrayList<>();
        OrchestratorCopyHistory orchestratorCopyHistory;
        for (DSSOrchestratorCopyInfo orchestratorCopyInfo: orchestratorCopyInfoList) {
            orchestratorCopyHistory = new OrchestratorCopyHistory();
            orchestratorCopyHistory.setId(orchestratorId);
            orchestratorCopyHistory.setUsername(username);
            orchestratorCopyHistory.setWorkspaceName(workspace.getWorkspaceName());
            orchestratorCopyHistory.setIsCopying(orchestratorCopyInfo.getIsCopying());
            orchestratorCopyHistory.setSourceOrchestratorName(orchestratorCopyInfo.getSourceOrchestratorName());
            orchestratorCopyHistory.setTargetOrchestratorName(orchestratorCopyInfo.getTargetOrchestratorName());
            orchestratorCopyHistory.setSourceProjectName(orchestratorCopyInfo.getSourceProjectName());
            orchestratorCopyHistory.setTargetProjectName(orchestratorCopyInfo.getTargetProjectName());
            orchestratorCopyHistory.setExceptionInfo(orchestratorCopyInfo.getExceptionInfo());
            orchestratorCopyHistory.setStatus(orchestratorCopyInfo.getStatus());
            orchestratorCopyHistory.setStartTime(SDF.format(orchestratorCopyInfo.getStartTime()));
            orchestratorCopyHistory.setEndTime(SDF.format(orchestratorCopyInfo.getEndTime()));
            orchestratorCopyHistory.setWorkflowNodeSuffix(orchestratorCopyInfo.getWorkflowNodeSuffix());
            orchestratorCopyHistory.setMicroserverName(orchestratorCopyInfo.getMicroserverName());
            orchestratorCopyHistoryList.add(orchestratorCopyHistory);
        }
        return orchestratorCopyHistoryList;
    }

    @Override
    public Boolean getOrchestratorCopyStatus(Long sourceOrchestratorId) {
        return StringUtils.isNotBlank(orchestratorCopyJobMapper.getOrchestratorCopyStatus(sourceOrchestratorId));
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
    private DSSProject validateOperation(long projectId, String username) throws DSSOrchestratorErrorException {
        ProjectInfoRequest projectInfoRequest = new ProjectInfoRequest();
        projectInfoRequest.setProjectId(projectId);
        DSSProject dssProject = (DSSProject) DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                .ask(projectInfoRequest);
        if (dssProject == null) {
            DSSExceptionUtils.dealErrorException(6003, "工程不存在", DSSOrchestratorErrorException.class);
        }
        if (!hasProjectEditPriv(projectId, username)) {
            DSSExceptionUtils.dealErrorException(6004, "用户没有工程编辑权限", DSSOrchestratorErrorException.class);
        }
        return dssProject;
    }

    private boolean hasProjectEditPriv(Long projectId, String username) {
        ProjectUserAuthResponse projectUserAuthResponse = (ProjectUserAuthResponse) DSSSenderServiceFactory.getOrCreateServiceInstance()
                .getProjectServerSender().ask(new ProjectUserAuthRequest(projectId, username));
        boolean hasEditPriv = false;
        if (!CollectionUtils.isEmpty(projectUserAuthResponse.getPrivList())) {
            hasEditPriv = projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_EDIT.getRank()) ||
                    projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_RELEASE.getRank());
        }
        return hasEditPriv || projectUserAuthResponse.getProjectOwner().equals(username);
    }

}
