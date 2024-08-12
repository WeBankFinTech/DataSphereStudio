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

import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectInfoRequest;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitCurrentCommitRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitCommitResponse;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.*;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.LockMapper;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowEditLock;
import com.webank.wedatasphere.dss.workflow.entity.request.BatchPublishWorkflowRequest;
import com.webank.wedatasphere.dss.workflow.lock.DSSFlowEditLockManager;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.PublishService;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.rpc.Sender;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant.*;

public class PublishServiceImpl implements PublishService {

    @Autowired
    private DSSFlowService dssFlowService;
    @Autowired
    private WorkFlowParser workFlowParser;
    @Autowired
    private LockMapper lockMapper;
    @Autowired
    private DSSFlowService flowService;

    public void setDssFlowService(DSSFlowService dssFlowService) {
        this.dssFlowService = dssFlowService;
    }

    public void setWorkFlowParser(WorkFlowParser workFlowParser) {
        this.workFlowParser = workFlowParser;
    }

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected Sender getOrchestratorSender() {
        return DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();
    }

    @Override
    public String submitPublish(String convertUser, Long workflowId,
                                Map<String, Object> dssLabel, Workspace workspace, String comment) throws Exception {
        LOGGER.info("User {} begins to convert workflow {}.", convertUser, workflowId);
        //1 获取对应的orcId 和 orcVersionId
        //2.进行提交
        DSSFlow dssFlow = null;
        try {
            dssFlow = checkFlowBeforeSubmit(workflowId, convertUser, workspace);
            String schedulerAppConnName = workFlowParser.getValueWithKey(dssFlow.getFlowJson(), DSSWorkFlowConstant.SCHEDULER_APP_CONN_NAME);
            if (StringUtils.isBlank(schedulerAppConnName)) {
                // 向下兼容老版本
                schedulerAppConnName = DEFAULT_SCHEDULER_APP_CONN.getValue();
            }
            SchedulerAppConn schedulerAppConn = (SchedulerAppConn) AppConnManager.getAppConnManager().getAppConn(schedulerAppConnName);
            // 只是为了获取是否需要发布所有Orc，这里直接拿第一个AppInstance即可。
            AppInstance appInstance = schedulerAppConn.getAppDesc().getAppInstances().get(0);
            ResponseConvertOrchestrator response = requestConvertOrchestration(comment, workflowId, convertUser,
                    workspace, schedulerAppConn, dssLabel, appInstance, null);
            if (response.getResponse().isFailed()) {
                throw new DSSErrorException(50311, response.getResponse().getMessage());
            }

            return response.getId();
        } catch (DSSErrorException e) {
            throw e;
        } catch (final Exception t) {
            Object str = dssFlow == null ? workflowId : dssFlow.getName();
            LOGGER.error("User {} failed to submit publish {}.", convertUser, str, t);
            DSSExceptionUtils.dealErrorException(63325, "Failed to submit publish " + str, t, DSSErrorException.class);
        }
        return null;
    }

    private DSSFlow checkFlowBeforeSubmit(Long workflowId, String convertUser, Workspace workspace) throws Exception{
        DSSFlow dssFlow = dssFlowService.getFlow(workflowId);
        if (dssFlow == null) {
            DSSExceptionUtils.dealErrorException(63325, "workflow " + workflowId + " is not exists.", DSSErrorException.class);
            return null;
        }
        LOGGER.info("User {} begins to convert workflow {}.", convertUser, dssFlow.getName());
        ProjectInfoRequest projectInfoRequest = new ProjectInfoRequest();
        projectInfoRequest.setProjectId(dssFlow.getProjectId());
        DSSProject dssProject = (DSSProject) DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender().ask(projectInfoRequest);
        if (dssProject.getWorkspaceId() != workspace.getWorkspaceId()) {
            DSSExceptionUtils.dealErrorException(63335, "工作流所在工作空间和cookie中不一致，请刷新页面后，再次发布！", DSSErrorException.class);
        }
        //仅对接入Git的项目更新状态为 发布-publish
        GitCommitResponse gitCommitResponse = null;
        OrchestratorVo orchestratorVo = null;
        Long orchestratorId = null;
        if (dssProject.getAssociateGit() != null && dssProject.getAssociateGit()) {
            orchestratorVo = RpcAskUtils.processAskException(getOrchestratorSender().ask(new RequestQuertByAppIdOrchestrator(workflowId)),
                    OrchestratorVo.class, RequestQueryByIdOrchestrator.class);
            if (orchestratorVo == null) {
                throw new DSSErrorException(800001, "编排不存在");
            }
            orchestratorId = orchestratorVo.getDssOrchestratorInfo().getId();
            String status = lockMapper.selectOrchestratorStatus(orchestratorId);
            if (OrchestratorRefConstant.FLOW_STATUS_SAVE.equals(status)) {
                throw new DSSErrorException(800001, "发布前请先提交工作流");
            }

            // 获取当前文件Commit
            Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
            GitCurrentCommitRequest currentCommitRequest = new GitCurrentCommitRequest(workspace.getWorkspaceId(), dssProject.getName(), convertUser, dssFlow.getName());
            gitCommitResponse = RpcAskUtils.processAskException(sender.ask(currentCommitRequest), GitCommitResponse.class, GitCurrentCommitRequest.class);
            // 更新commitId
            lockMapper.updateOrchestratorVersionCommitId(gitCommitResponse.getCommitId(), orchestratorId);
            // 更新工作流状态
            lockMapper.updateOrchestratorStatus(orchestratorId, OrchestratorRefConstant.FLOW_STATUS_PUBLISH);
        }
        return dssFlow;
    }

    @Override
    public void batchPublish(String ticketId, BatchPublishWorkflowRequest publishWorkflowRequest, Workspace workspace, String convertUser, Map<String, Object> dssLabel) throws Exception{
        List<Long> orcIds = publishWorkflowRequest.getOrchestratorList();
        String labelStr = publishWorkflowRequest.getLabels().getRoute();
        Map<String, Object> labels = new HashMap<>();
        labels.put(EnvDSSLabel.DSS_ENV_LABEL_KEY, labelStr);
        String comment = publishWorkflowRequest.getComment();

        List<DSSLabel> dssLabelList = Arrays.asList(new EnvDSSLabel(labelStr));
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender(dssLabelList);
        RequestQueryOrchestrator queryRequest = new RequestQueryOrchestrator(orcIds);
        List<Long> workflowIdList = new ArrayList<>();


        try {
            ResponseQueryOrchestrator queryResponse = RpcAskUtils.processAskException(sender.ask(queryRequest), ResponseQueryOrchestrator.class, RequestQueryOrchestrator.class);
            if (queryResponse == null) {
                LOGGER.error("query response is null, it is a fatal error");
                throw new DSSErrorException(80001, "查询编排失败，请确认编排是否存在") ;
            }
            Set<DSSOrchestratorVersion> orchestratorVersions = queryResponse.getOrchestratorVoes().stream().map(OrchestratorVo::getDssOrchestratorVersion).collect(Collectors.toSet());
            workflowIdList = orchestratorVersions.stream().map(DSSOrchestratorVersion::getAppId).collect(Collectors.toList());
        } catch (Exception e) {
            DSSExceptionUtils.dealErrorException(60015, "query orchestrator ref failed",
                    DSSErrorException.class);
        }

        List<DSSFlow> dssFlowList = new ArrayList<>();
        for (Long flowId : workflowIdList) {
            DSSFlow dssFlow = checkFlowBeforeSubmit(flowId, convertUser, workspace);
            // 尝试获取工作流编辑锁
            try {
                //只有父工作流才有锁，子工作流复用父工作流的锁
                if(dssFlow.getRootFlow()) {
                    String flowEditLock = DSSFlowEditLockManager.tryAcquireLock(dssFlow, convertUser, ticketId);
                    dssFlow.setFlowEditLock(flowEditLock);
                }
            } catch (DSSErrorException e) {
                if (DSSWorkFlowConstant.EDIT_LOCK_ERROR_CODE == e.getErrCode()) {
                    DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(flowId);
                    throw new DSSErrorException(60056,"用户已锁定编辑错误码，editLockInfo:" + flowEditLock);
                }
                throw e;
            }
            dssFlowList.add(dssFlow);
        }

        String schedulerAppConnName = workFlowParser.getValueWithKey(dssFlowList.get(0).getFlowJson(), DSSWorkFlowConstant.SCHEDULER_APP_CONN_NAME);
        if (StringUtils.isBlank(schedulerAppConnName)) {
            // 向下兼容老版本
            schedulerAppConnName = DEFAULT_SCHEDULER_APP_CONN.getValue();
        }
        SchedulerAppConn schedulerAppConn = (SchedulerAppConn) AppConnManager.getAppConnManager().getAppConn(schedulerAppConnName);
        // 只是为了获取是否需要发布所有Orc，这里直接拿第一个AppInstance即可。
        AppInstance appInstance = schedulerAppConn.getAppDesc().getAppInstances().get(0);
        ResponseConvertOrchestrator response = requestConvertOrchestration(comment, null, convertUser,
                workspace, schedulerAppConn, dssLabel, appInstance, workflowIdList);

        if (response.getResponse().isFailed()) {
            throw new DSSErrorException(50311, response.getResponse().getMessage());
        }

        return ;
    }

    /**
     * 可覆写该方法自定义实现request对象
     *
     * @return
     */
    protected ResponseConvertOrchestrator requestConvertOrchestration(String comment, Long workflowId, String convertUser, Workspace workspace,
                                                                      SchedulerAppConn schedulerAppConn, Map<String, Object> dssLabel, AppInstance appInstance, List<Long> flowIdList) {
        RequestFrameworkConvertOrchestration requestFrameworkConvertOrchestration = new RequestFrameworkConvertOrchestration();
        requestFrameworkConvertOrchestration.setComment(comment);
        requestFrameworkConvertOrchestration.setOrcAppId(workflowId);
        requestFrameworkConvertOrchestration.setUserName(convertUser);
        requestFrameworkConvertOrchestration.setWorkspace(workspace);
        requestFrameworkConvertOrchestration.setConvertAllOrcs(schedulerAppConn.getOrCreateConversionStandard().getDSSToRelConversionService(appInstance).isConvertAllOrcs());
        requestFrameworkConvertOrchestration.setLabels(dssLabel);
        ResponseConvertOrchestrator response = RpcAskUtils.processAskException(getOrchestratorSender().ask(requestFrameworkConvertOrchestration),
                ResponseConvertOrchestrator.class, RequestFrameworkConvertOrchestration.class);
        return response;
    }


    @Override
    public ResponseConvertOrchestrator getStatus(String username, String taskId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} is asking status of {}.", username, taskId);
        }
        ResponseConvertOrchestrator response = new ResponseConvertOrchestrator();
        //通过rpc的方式去获取到最新status
        try {
            response = requestConvertOrchestrationStatus(taskId);
            LOGGER.info("user {} gets status of {}, status is {}，msg is {}", username, taskId, response.getResponse().getJobStatus(), response.getResponse().getMessage());
        } catch (Exception t) {
            LOGGER.error("failed to getStatus {} ", taskId, t);

        }
        return response;
    }

    /**
     * 可覆写该方法自定义实现request对象
     *
     * @return
     */
    protected ResponseConvertOrchestrator requestConvertOrchestrationStatus(String taskId){
        RequestFrameworkConvertOrchestrationStatus req =  new RequestFrameworkConvertOrchestrationStatus();
        req.setId(taskId);
        return RpcAskUtils.processAskException(getOrchestratorSender().ask(req), ResponseConvertOrchestrator.class,
                RequestFrameworkConvertOrchestrationStatus.class);
    }

}
