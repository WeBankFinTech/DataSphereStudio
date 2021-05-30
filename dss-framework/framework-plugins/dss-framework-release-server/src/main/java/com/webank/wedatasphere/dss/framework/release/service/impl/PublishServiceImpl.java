/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.release.service.impl;

import com.webank.wedatasphere.dss.appconn.core.WorkflowAppConn;
import com.webank.wedatasphere.dss.appconn.schedule.core.SchedulerAppConn;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.release.dao.OrchestratorReleaseInfoMapper;
import com.webank.wedatasphere.dss.framework.release.dao.ProjectMapper;
import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import com.webank.wedatasphere.dss.framework.release.service.PublishService;
import com.webank.wedatasphere.dss.framework.release.utils.ReleaseConf;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorFrameworkAppConn;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.process.DevProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProdProcessService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefPublishToSchedulerService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectPublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.PublishToSchedulerStage;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.RefScheduleOperation;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.common.entity.project.Project;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * created by cooperyang on 2020/12/14
 * Description:
 */
@Service
public class PublishServiceImpl implements PublishService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishServiceImpl.class);

    @Autowired
    private AppConnService appConnService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private OrchestratorReleaseInfoMapper orchestratorReleaseInfoMapper;

    @Autowired
    @Qualifier("projectPublishRefFactory")
    private RefFactory<ProjectPublishToSchedulerRef> refFactory;

    private SchedulerAppConn schedulerAppConn;

    private WorkflowAppConn workflowAppConn;

    private OrchestratorFrameworkAppConn orchestratorFrameworkAppConn;

    @PostConstruct
    public void init() {
        schedulerAppConn = (SchedulerAppConn)appConnService.getAppConn(
            ReleaseConf.DSS_SCHEDULE_APPCONN_NAME.getValue());
        workflowAppConn = (WorkflowAppConn)appConnService.getAppConn("workflow");
        orchestratorFrameworkAppConn = (OrchestratorFrameworkAppConn)appConnService.getAppConn(
            "orchestrator-framework");
    }

    @Override
    public void publish(String releaseUser, ProjectInfo projectInfo, List<OrchestratorReleaseInfo> orchestratorReleaseInfos,
                        DSSLabel dssLabel, Workspace workspace, boolean supportMultiEnv) throws Exception {
        if (schedulerAppConn == null) {
            LOGGER.error("scheduler appconn is null, can not publish to scheduler system");
            DSSExceptionUtils.dealErrorException(61123, "scheduler appconn is null", DSSErrorException.class);
        }
        LOGGER.info("{} begins to publish project {} orcInfos {} to scheduler {}", releaseUser,
            projectInfo.getProjectName(), orchestratorReleaseInfos, schedulerAppConn.getAppDesc().getAppName());
        //如果是支持工作流级别的发布,那么就是走工作流
        if (schedulerAppConn.supportFlowSchedule()) {
            publishWorkflow(releaseUser, projectInfo, orchestratorReleaseInfos, dssLabel, workspace, supportMultiEnv);
        } else {
            //工程级别的发布
            publishProject(releaseUser, projectInfo, orchestratorReleaseInfos, dssLabel, workspace, supportMultiEnv);
        }
    }

    private void publishWorkflow(String releaseUser, ProjectInfo projectInfo,
        List<OrchestratorReleaseInfo> orchestratorReleaseInfos, DSSLabel dssLabel, Workspace workspace,
        boolean supportMultiEnv) throws Exception {
        if (workflowAppConn == null) {
            LOGGER.error("workflow appconn is null, can not do publish operation");
            DSSExceptionUtils.dealErrorException(60032, "workflow appconn is null", DSSErrorException.class);
        }
        DevelopmentIntegrationStandard developmentIntegrationStandard = workflowAppConn.getAppStandards().
            stream().
            filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard).
            map(appStandard -> (DevelopmentIntegrationStandard)appStandard).
            findAny().orElse(null);
        List<DSSLabel> dssLabels = Collections.singletonList(dssLabel);
        if (developmentIntegrationStandard == null) {
            LOGGER.error("development Standard is null will not go on");
            DSSExceptionUtils.dealErrorException(60032, "development Standard is null", DSSErrorException.class);
        } else {
            ProcessService processService = developmentIntegrationStandard.getProcessService(dssLabels);
            if (processService instanceof DevProcessService) {
                DevProcessService devProcessService = (DevProcessService)processService;
                RefPublishToSchedulerService refPublishToSchedulerService
                    = devProcessService.getRefPublishToSchedulerService();
                RefScheduleOperation refScheduleOperation = refPublishToSchedulerService.createRefScheduleOperation();
                PublishToSchedulerStage publishToSchedulerStage = refScheduleOperation.createPublishToSchedulerStage();
                try {
                    ProjectPublishToSchedulerRef ref = refFactory.newRef(ProjectPublishToSchedulerRef.class,
                        workflowAppConn.getClass().getClassLoader(),
                        "com.webank.wedatasphere.dss.workflow.appconn.ref");
                    List<Long> orchestratorIds = orchestratorReleaseInfos.stream()
                        .map(OrchestratorReleaseInfo::getOrchestratorId)
                        .collect(Collectors.toList());
                    ref.setOrcIds(orchestratorIds);
                    List<Long> orcAppIds = orchestratorReleaseInfos.stream()
                        .map(OrchestratorReleaseInfo::getOrchestratorVersionAppId)
                        .collect(Collectors.toList());
                    ref.setOrcAppIds(orcAppIds);
                    DSSProject project = toProject(projectInfo);
                    project.setWorkspaceName(workspace.getWorkspaceName());
                    ref.setProject(project);
                    ref.setUserName(releaseUser);
                    //ref.setPublishType(PublishType.FULL);
                    ref.setWorkspace(workspace);
                    if (supportMultiEnv) {
                        ref.setLabels(dssLabels);
                    } else {
                        //如果不支持多环境的话，只需要在dev进行同步到dolphin scheduler就好
                        ref.setLabels(Collections.singletonList(new DSSLabel("DEV")));
                    }
                    // 目前只考虑工作流发布，一次只发布一个编排
                    OrchestratorReleaseInfo releaseInfo = orchestratorReleaseInfos.get(0);
                    // 如果发布过，记录调度系统中对应的工作流id
                    OrchestratorReleaseInfo latestOrchestratorReleaseInfo
                        = orchestratorReleaseInfoMapper.getByOrchestratorId(releaseInfo.getOrchestratorId());
                    if (latestOrchestratorReleaseInfo != null) {
                        ref.setSchedulerWorkflowId(latestOrchestratorReleaseInfo.getSchedulerWorkflowId());
                    }

                    ResponseRef responseRef = publishToSchedulerStage.publishToScheduler(ref);
                    Long schedulerWorkflowId = Long.valueOf(responseRef.getResponseBody());

                    String orchestratorVersion = projectMapper.getVersionByOrchestratorVersionId(
                        releaseInfo.getOrchestratorVersionId());
                    if (Objects.isNull(latestOrchestratorReleaseInfo)) { // 未发布过，插入记录
                        releaseInfo.setOrchestratorVersion(orchestratorVersion);
                        releaseInfo.setSchedulerWorkflowId(schedulerWorkflowId);
                        orchestratorReleaseInfoMapper.insert(releaseInfo);
                    } else {
                        latestOrchestratorReleaseInfo.setOrchestratorVersionId(releaseInfo.getOrchestratorVersionId());
                        latestOrchestratorReleaseInfo.setOrchestratorVersion(orchestratorVersion);
                        latestOrchestratorReleaseInfo.setUpdateTime(new Date());
                        orchestratorReleaseInfoMapper.update(latestOrchestratorReleaseInfo);
                    }
                } catch (ExternalOperationFailedException e) {
                    String errorInfo = e.getDesc();
                    DSSExceptionUtils.dealErrorException(60018, errorInfo, e, ExternalOperationFailedException.class);
                } catch (final Throwable t) {
                    DSSExceptionUtils.dealErrorException(61121, "Failed to publish workflow", t,
                        DSSErrorException.class);
                }
            }
        }
    }

    private void publishProject(String releaseUser, ProjectInfo projectInfo,
        List<OrchestratorReleaseInfo> orchestratorReleaseInfos, DSSLabel dssLabel, Workspace workspace,
        boolean supportMultiEnv) throws Exception {
        if (orchestratorFrameworkAppConn == null) {
            LOGGER.error("orchestrator appconn is null, can not do publish operation");
            DSSExceptionUtils.dealErrorException(60032, "orchestrator appconn is null", DSSErrorException.class);
        }
        DevelopmentIntegrationStandard developmentIntegrationStandard = orchestratorFrameworkAppConn.getAppStandards().
            stream().
            filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard).
            map(appStandard -> (DevelopmentIntegrationStandard)appStandard).
            findAny().orElse(null);
        List<DSSLabel> dssLabels = Collections.singletonList(dssLabel);
        if (developmentIntegrationStandard == null) {
            LOGGER.error("development Standard is null will not go on");
            DSSExceptionUtils.dealErrorException(60032, "development Standard is null", DSSErrorException.class);
        } else {
            ProcessService processService = developmentIntegrationStandard.getProcessService(dssLabels);
            if (processService instanceof DevProcessService) {
                DevProcessService devProcessService = (DevProcessService)processService;
                RefPublishToSchedulerService refPublishToSchedulerService
                    = devProcessService.getRefPublishToSchedulerService();
                RefScheduleOperation refScheduleOperation = refPublishToSchedulerService.createRefScheduleOperation();
                PublishToSchedulerStage publishToSchedulerStage = refScheduleOperation.createPublishToSchedulerStage();
                try {
                    ProjectPublishToSchedulerRef ref = refFactory.newRef(ProjectPublishToSchedulerRef.class,
                        orchestratorFrameworkAppConn.getClass().getClassLoader(),
                        "com.webank.wedatasphere.dss.appconn.orchestrator.ref");
                    //ref.setLabels(dssLabels);
                    List<Long> orchestratorIds = orchestratorReleaseInfos.stream()
                        .map(OrchestratorReleaseInfo::getOrchestratorId)
                        .collect(Collectors.toList());
                    ref.setOrcIds(orchestratorIds);
                    Project project = toProject(projectInfo);
                    ref.setProject(project);
                    ref.setUserName(releaseUser);
                    //ref.setPublishType(PublishType.FULL);
                    ref.setWorkspace(workspace);
                    if (supportMultiEnv) {
                        ref.setLabels(dssLabels);
                    } else {
                        //如果不支持多环境的话，只需要在dev进行同步到scheudlis就好
                        ref.setLabels(Collections.singletonList(new DSSLabel("DEV")));
                    }
                    publishToSchedulerStage.publishToScheduler(ref);
                } catch (final Throwable t) {
                    if (t instanceof ExternalOperationFailedException) {
                        String errorInfo = t.getCause().getMessage();
                        ExternalOperationFailedException warnException = (ExternalOperationFailedException)t;
                        DSSExceptionUtils.dealErrorException(60018, "msg:" + errorInfo, t,
                            ExternalOperationFailedException.class);
                    } else {
                        DSSExceptionUtils.dealErrorException(61121, "Failed to create Ref for publish", t,
                            DSSErrorException.class);
                    }
                }
            }
        }
    }

    private DSSProject toProject(ProjectInfo projectInfo) {
        DSSProject dssProject = new DSSProject();
        dssProject.setName(projectInfo.getProjectName());
        dssProject.setCreateBy(projectInfo.getCreateBy());
        dssProject.setDescription(projectInfo.getDescription());
        dssProject.setId(projectInfo.getProjectId());
        dssProject.setCreateTime(projectInfo.getCreateTime());
        return dssProject;
    }


    private void publishOrchestrator(String releaseUser, ProjectInfo projectInfo, List<OrchestratorReleaseInfo> orchestratorReleaseInfos) {

    }


    @Override
    public void publish(Long projectId, Map<Long, Long> orchestratorInfoMap, DSSLabel dssLabel) {

    }


    @Override
    public void publish(String releaseUser, ProjectInfo projectInfo, Long orchestratorId, DSSLabel dssLabel,
                        Workspace workspace) throws Exception {
        if (null == schedulerAppConn){
            LOGGER.error("scheduler AppConn is null");
            throw new DSSErrorException(70023, "schedulerAppConn is null");
        }
        if (schedulerAppConn.supportFlowSchedule()){
            //如果支持工作流级别的发布,不需要进行工作流级别的打包
            //todo 先不实现，先做工程级别的
        } else{
            //工程级别的发布
            publishProject(releaseUser, projectInfo, orchestratorId, dssLabel, workspace);
        }
    }

    private void publishProject(String releaseUser, ProjectInfo projectInfo, Long orchestratorId, DSSLabel dssLabel, Workspace workspace) throws Exception {
        if (orchestratorFrameworkAppConn == null) {
            LOGGER.error("orchestrator appconn is null, can not do publish operation");
            DSSExceptionUtils.dealErrorException(60032, "orchestrator appconn is null", DSSErrorException.class);
        }
        DevelopmentIntegrationStandard developmentIntegrationStandard =
                orchestratorFrameworkAppConn.getAppStandards().
                        stream().
                        filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard).
                        map(appStandard -> (DevelopmentIntegrationStandard) appStandard).
                        findAny().orElse(null);
        List<DSSLabel> dssLabels = Collections.singletonList(dssLabel);
        if (developmentIntegrationStandard == null) {
            LOGGER.error("development Standard is null will not go on");
            DSSExceptionUtils.dealErrorException(60032, "development Standard is null", DSSErrorException.class);
        } else {
            ProcessService processService = developmentIntegrationStandard.getProcessService(dssLabels);
            if (processService instanceof ProdProcessService) {
                ProdProcessService prodProcessService = (ProdProcessService) processService;
                RefPublishToSchedulerService refPublishToSchedulerService = prodProcessService.getRefPublishToSchedulerService();
                RefScheduleOperation refScheduleOperation = refPublishToSchedulerService.createRefScheduleOperation();
                PublishToSchedulerStage publishToSchedulerStage = refScheduleOperation.createPublishToSchedulerStage();
                try {
                    ProjectPublishToSchedulerRef ref =
                            refFactory.newRef(ProjectPublishToSchedulerRef.class,
                                    orchestratorFrameworkAppConn.getClass().getClassLoader(), "com.webank.wedatasphere.dss.appconn.orchestrator.ref");
                    ref.setLabels(dssLabels);
                    ref.setOrcIds(Arrays.asList(orchestratorId));
                    Project project = toProject(projectInfo);
                    ref.setProject(project);
                    ref.setUserName(releaseUser);
                    //ref.setPublishType(PublishType.FULL);
                    ref.setWorkspace(workspace);
                    publishToSchedulerStage.publishToScheduler(ref);
                } catch (final Throwable t) {
                    DSSExceptionUtils.dealErrorException(61121, "Failed to create Ref for publish", t, DSSErrorException.class);
                }
            }
        }
    }

}
