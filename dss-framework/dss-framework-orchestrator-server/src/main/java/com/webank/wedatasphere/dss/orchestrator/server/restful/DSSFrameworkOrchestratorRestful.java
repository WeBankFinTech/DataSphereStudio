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

package com.webank.wedatasphere.dss.orchestrator.server.restful;

import com.webank.wedatasphere.dss.appconn.manager.utils.AppConnManagerUtils;
import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectInfoListRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectInfoListResponse;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectInfoRequest;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.git.common.protocol.GitTree;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitAddMemberRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserInfoRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitAddMemberResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitFileContentResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitHistoryResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserInfoResponse;
import com.webank.wedatasphere.dss.git.common.protocol.util.UrlUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.*;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.server.conf.OrchestratorConf;
import com.webank.wedatasphere.dss.orchestrator.server.constant.OrchestratorLevelEnum;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.*;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.*;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorFrameworkService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorPluginService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestLockWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseLockWorkflow;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.LockMapper;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowEditLock;
import com.webank.wedatasphere.dss.workflow.lock.DSSFlowEditLockManager;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.rpc.Sender;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequestMapping(path = "/dss/framework/orchestrator", produces = {"application/json"})
@RestController
public class DSSFrameworkOrchestratorRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkOrchestratorRestful.class);

    @Autowired
    private OrchestratorFrameworkService orchestratorFrameworkService;
    @Autowired
    private OrchestratorService orchestratorService;
    @Autowired
    private OrchestratorPluginService orchestratorPluginService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private LockMapper lockMapper;
    @Autowired
    private DSSFlowService flowService;
    @Autowired
    private OrchestratorMapper orchestratorMapper;

    /**
     * 创建编排模式
     *
     * @param createRequest
     * @return
     */
    @RequestMapping(path = "createOrchestrator", method = RequestMethod.POST)
    public Message createOrchestrator(@RequestBody OrchestratorCreateRequest createRequest) throws Exception {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        //todo 先注释掉
        // orchestratorService.saveOrchestrator(createRequest,null,username);
        CommonOrchestratorVo orchestratorVo = orchestratorFrameworkService.createOrchestrator(username, createRequest, workspace);
        AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.ORCHESTRATOR,
                orchestratorVo.getOrchestratorId(), createRequest.getOrchestratorName(), OperateTypeEnum.CREATE, createRequest);
        return Message.ok("创建工作流编排模式成功").data("orchestratorId", orchestratorVo.getOrchestratorId());
    }

    /**
     * 查询所有的编排模式
     *
     * @param orchestratorRequest
     * @return
     */
    @RequestMapping(path = "getAllOrchestrator", method = RequestMethod.POST)
    public Message getAllOrchestrator(@RequestBody OrchestratorRequest orchestratorRequest) {
        try {
            String username = SecurityFilter.getLoginUsername(httpServletRequest);
            LOGGER.info("user {} begin to geyAllOrchestrator, requestBody:{}", username, orchestratorRequest);
            return Message.ok("获取编排模式成功").data("page", orchestratorService.getOrchestratorInfos(orchestratorRequest, username));
        } catch (Exception e) {
            LOGGER.error("getAllOrchestratorError ", e);
            return Message.error("获取编排模式失败:" + e.getMessage());
        }
    }

    /**
     * 修改编排模式
     *
     * @param modifyRequest
     * @return
     */
    @RequestMapping(path = "modifyOrchestrator", method = RequestMethod.POST)
    public Message modifyOrchestrator(@RequestBody OrchestratorModifyRequest modifyRequest) throws Exception {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        if (orchestratorFrameworkService.getOrchestratorCopyStatus(modifyRequest.getId())) {
            return Message.error("当前工作流正在被复制，不允许编辑");
        }
        CommonOrchestratorVo orchestratorVo = orchestratorFrameworkService.modifyOrchestrator(username, modifyRequest, workspace);
        AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.ORCHESTRATOR,
                orchestratorVo.getOrchestratorId(), modifyRequest.getOrchestratorName(), OperateTypeEnum.UPDATE, modifyRequest);
        return Message.ok("修改工作流编排模式成功").data("orchestratorId", orchestratorVo.getOrchestratorId());
    }

    /**
     * 删除编排模式
     *
     * @param deleteRequest
     * @return
     */
    @RequestMapping(path = "deleteOrchestrator", method = RequestMethod.POST)
    public Message deleteOrchestrator(@RequestBody OrchestratorDeleteRequest deleteRequest) throws Exception {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        if (orchestratorFrameworkService.getOrchestratorCopyStatus(deleteRequest.getId())) {
            return Message.error("当前工作流正在被复制，不允许删除");
        }
        ProjectInfoRequest projectInfoRequest = new ProjectInfoRequest();
        projectInfoRequest.setProjectId(deleteRequest.getProjectId());
        DSSProject dssProject = (DSSProject) DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender().ask(projectInfoRequest);
        if (dssProject.getWorkspaceId() != workspace.getWorkspaceId()) {
            DSSExceptionUtils.dealErrorException(63335, "工作流所在工作空间和cookie中不一致，请刷新页面后，再次发布！", DSSErrorException.class);
        }
        CommonOrchestratorVo orchestratorVo = orchestratorFrameworkService.deleteOrchestrator(username, deleteRequest, workspace);
        AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.ORCHESTRATOR,
                orchestratorVo.getOrchestratorId(), orchestratorVo.getOrchestratorName(), OperateTypeEnum.DELETE, deleteRequest);
        return Message.ok("删除工作流编排模式成功");
    }

    /**
     * 复制编排模式
     *
     * @param orchestratorCopyRequest
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "copyOrchestrator", method = RequestMethod.POST)
    public Message copyOrchestrator(@RequestBody OrchestratorCopyRequest orchestratorCopyRequest) throws Exception {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);

        if (orchestratorFrameworkService.getOrchestratorCopyStatus(orchestratorCopyRequest.getSourceOrchestratorId())) {
            return Message.error("当前工作流正在被复制，不允许再次复制");
        }

        String copyJobId = orchestratorFrameworkService.copyOrchestrator(username, orchestratorCopyRequest, workspace);
        AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.ORCHESTRATOR,
                orchestratorCopyRequest.getSourceOrchestratorId(), orchestratorCopyRequest.getSourceOrchestratorName(), OperateTypeEnum.COPY, orchestratorCopyRequest);

        return Message.ok("复制工作流已经开始，正在后台复制中，复制状态可以从复制历史查看...").data("copyJobId", copyJobId);
    }

    /**
     * 获取编排复制任务状态
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/{id}/copyInfo", method = RequestMethod.GET)
    public Message getCopyJobStatus(@PathVariable("id") String copyInfoId) throws Exception {
        return Message.ok("获取编排复制任务状态成功").data("orchestratorCopyInfo", orchestratorFrameworkService.getOrchestratorCopyInfoById(copyInfoId));
    }

    /**
     * 查询锁信息和解锁操作共用此接口。
     * confirmDelete参数为false时，返回编辑锁的owner等信息。confirmDelete为true时，执行解锁操作。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "unlockOrchestrator", method = RequestMethod.POST)
    public Message unlockOrchestrator(@RequestBody OrchestratorUnlockRequest unlockRequest) throws Exception {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        OrchestratorUnlockVo orchestratorVo = orchestratorFrameworkService.unlockOrchestrator(username, workspace, unlockRequest);
        AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.ORCHESTRATOR,
                orchestratorVo.getOrchestratorId(), orchestratorVo.getOrchestratorName(), OperateTypeEnum.DELETE, unlockRequest);
        return Message.ok().data("status", orchestratorVo.getStatus())
                .data("confirmMessage", orchestratorVo.getConfirmMessage())
                .data("lockOwner", orchestratorVo.getLockOwner());
    }

    /**
     * 查看编排复制历史
     *
     * @param orchestratorId
     * @return
     */
    @RequestMapping(path = "listOrchestratorCopyHistory", method = RequestMethod.GET)
    public Message listOrchestratorCopyHistory(@RequestParam(required = false, name = "orchestratorId") Long orchestratorId,
                                               @RequestParam(required = false, name = "currentPage") Integer currentPage,
                                               @RequestParam(required = false, name = "pageSize") Integer pageSize) throws Exception {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        if (orchestratorId == null) {
            return Message.error("请到父工作流查看复制历史信息！");
        }
        Pair<Long, List<OrchestratorCopyHistory>> result = orchestratorFrameworkService.getOrchestratorCopyHistory(username, workspace, orchestratorId, currentPage, pageSize);
        return Message.ok("查找工作流复制历史成功").data("copyJobHistory", result.getSecond()).data("total", result.getFirst());
    }

    @RequestMapping(path = "orchestratorLevels", method = RequestMethod.GET)
    public Message getOrchestratorLevels() {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        List<OrchestratorLevelEnum> levels = Arrays.asList(OrchestratorLevelEnum.values());
        LOGGER.info("user {} try to get OrchestratorLevels, workspaceId:{}, result:{}", username, workspace.getWorkspaceId(), levels);
        return Message.ok("获取编排重要级别列表成功").data("orchestratorLevels", levels);
    }

    @RequestMapping(path = "rollbackOrchestrator", method = RequestMethod.POST)
    public Message rollbackOrchestrator(HttpServletRequest request, @RequestBody RollbackOrchestratorRequest rollbackOrchestratorRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        Long orchestratorId = rollbackOrchestratorRequest.getOrchestratorId();
        String version = rollbackOrchestratorRequest.getVersion();
        Long projectId = rollbackOrchestratorRequest.getProjectId();
        String projectName = rollbackOrchestratorRequest.getProjectName();
        Workspace workspace = SSOHelper.getWorkspace(request);
        LabelRouteVO labels = rollbackOrchestratorRequest.getLabels();
        try {
            LOGGER.info("user {} begin to rollbackOrchestrator, params:{}", username, rollbackOrchestratorRequest);
            OrchestratorRollBackGitVo rollbackOrchestrator = orchestratorService.rollbackOrchestrator(username, projectId, projectName, orchestratorId, version, labels, workspace);
            try {
                orchestratorService.rollbackOrchestratorGit(rollbackOrchestrator, username, projectId, projectName, orchestratorId, labels, workspace);
            } catch (Exception e) {
                return Message.ok("回滚版本成功,git回滚失败，请重新保存并提交工作流").data("newVersion", rollbackOrchestrator.getVersion());
            }
            Message message = Message.ok("回滚版本成功").data("newVersion", rollbackOrchestrator.getVersion());
            return message;
        } catch (final Throwable t) {
            LOGGER.error("Failed to rollback orchestrator for user {} orchestratorId {}, projectId {} version {}",
                    username, orchestratorId, projectId, version, t);
            return Message.error("回滚工作流版本失败");
        }
    }

    @RequestMapping(path = "openOrchestrator", method = RequestMethod.POST)
    public Message openOrchestrator(HttpServletRequest req, @RequestBody OpenOrchestratorRequest openOrchestratorRequest) throws Exception {
        String openUrl = "";
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        List<DSSLabel> dssLabelList = Arrays.asList(new EnvDSSLabel(openOrchestratorRequest.getLabels().getRoute()));
        Long orchestratorId = openOrchestratorRequest.getOrchestratorId();
        if (orchestratorFrameworkService.getOrchestratorCopyStatus(orchestratorId)){
            return Message.error("当前工作流正在被复制，不允许被打开，请稍后");
        }
        LOGGER.info("user {} try to openOrchestrator, params:{}", userName, openOrchestratorRequest);
        openUrl = orchestratorService.openOrchestrator(userName, workspace, orchestratorId, dssLabelList);
        OrchestratorVo orchestratorVo = orchestratorService.getOrchestratorVoById(orchestratorId);
        LOGGER.info("open url is {}, orcId is {}, dssLabels is {}", openUrl, orchestratorId, dssLabelList);
        return Message.ok().data("OrchestratorOpenUrl", openUrl).
                data("OrchestratorVo", orchestratorVo);
    }

    /**
     * 获取编排模式下的所有版本号
     *
     * @param queryOrchestratorVersion
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "getVersionByOrchestratorId", method = RequestMethod.POST)
    public Message getVersionByOrchestratorId(@RequestBody QueryOrchestratorVersion queryOrchestratorVersion) throws Exception {
        List list = orchestratorService.getVersionByOrchestratorId(queryOrchestratorVersion.getOrchestratorId());
        return Message.ok().data("list", list);
    }

    @RequestMapping(value = "diffFlowJob", method = RequestMethod.POST)
    public Message diff(@RequestBody OrchestratorSubmitRequest submitFlowRequest) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        Long orchestratorId = submitFlowRequest.getOrchestratorId();
        DSSOrchestratorVersion latestOrchestratorVersion = orchestratorFrameworkService.getLatestOrchestratorVersion(orchestratorId);
        submitFlowRequest.setFlowId(latestOrchestratorVersion.getAppId());

        String ticketId = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> DSSWorkFlowConstant.BDP_USER_TICKET_ID.equals(cookie.getName()))
                .findFirst().map(Cookie::getValue).get();
        DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(submitFlowRequest.getFlowId());
        if (flowEditLock != null && !flowEditLock.getOwner().equals(ticketId)) {
            return Message.error("当前工作流被用户" + flowEditLock.getUsername() + "已锁定编辑，您编辑的内容不能再被保存。如有疑问，请与" + flowEditLock.getUsername() + "确认");
        }
        Long taskId = orchestratorPluginService.diffFlow(submitFlowRequest, userName, workspace);
        return Message.ok().data("taskId", taskId);
    }

    @RequestMapping(value = "diffStatus", method = RequestMethod.GET)
    public Message diffStatus(@RequestParam(required = true, name = "taskId") Long taskId) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        try {
            String status = orchestratorPluginService.diffStatus(taskId);
            return Message.ok().data("status", status);
        } catch (Exception e) {
            return Message.ok().data("status", OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED).data("errMsg", e.getMessage());
        }
    }

    @RequestMapping(value = "diffContent", method = RequestMethod.GET)
    public Message diffContent(@RequestParam(required = true, name = "taskId") Long taskId) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        return Message.ok().data("tree", orchestratorPluginService.diffContent(taskId));
    }

    @RequestMapping(value = "diffOrchestratorPublish", method = RequestMethod.POST)
    public Message diffOrchestratorPublish(@RequestBody OrchestratorSubmitRequest submitFlowRequest) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        OrchestratorDiffDirVo gitTree = orchestratorPluginService.diffPublish(submitFlowRequest, userName, workspace);
        return Message.ok().data("tree", gitTree);
    }

    @RequestMapping(value = "diffFlowContent", method = RequestMethod.POST)
    public Message diffFlowContent(@RequestBody OrchestratorSubmitRequest submitFlowRequest) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        Long orchestratorId = submitFlowRequest.getOrchestratorId();
        DSSOrchestratorVersion latestOrchestratorVersion = orchestratorFrameworkService.getLatestOrchestratorVersion(orchestratorId);
        submitFlowRequest.setFlowId(latestOrchestratorVersion.getAppId());

        String ticketId = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> DSSWorkFlowConstant.BDP_USER_TICKET_ID.equals(cookie.getName()))
                .findFirst().map(Cookie::getValue).get();
        DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(submitFlowRequest.getFlowId());
        if (flowEditLock != null && !flowEditLock.getOwner().equals(ticketId)) {
            return Message.error("当前工作流被用户" + flowEditLock.getUsername() + "已锁定编辑，您编辑的内容不能再被保存。如有疑问，请与" + flowEditLock.getUsername() + "确认");
        }
        try {
            GitFileContentResponse contentResponse = orchestratorPluginService.diffFlowContent(submitFlowRequest, userName, workspace);
            return Message.ok().data("content", contentResponse);
        }catch (Exception e) {
            return Message.error("获取文件内容失败，原因为：" + e.getMessage());
        }
    }

    @RequestMapping(value = "batchSubmitFlow", method = RequestMethod.POST)
    public Message batchSubmitFlow(@RequestBody OrchestratorBatchSubmitRequest batchSubmitRequest) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        List<OrchestratorSubmitRequest> submitRequestList = batchSubmitRequest.getSubmitRequestList();

        if (CollectionUtils.isEmpty(submitRequestList)) {
            return Message.error("至少需要选择一项工作流进行提交");
        }

        Map<String, List<OrchestratorRelationVo>> map = new HashMap<>();
        Map<String, Long> projectMap = new HashMap<>();
        String ticketId = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> DSSWorkFlowConstant.BDP_USER_TICKET_ID.equals(cookie.getName()))
                .findFirst().map(Cookie::getValue).get();
        for (OrchestratorSubmitRequest submitFlowRequest: submitRequestList) {
            try {
                checkSubmitWorkflow(ticketId, submitFlowRequest, workspace, userName);
                boolean b = map.containsKey(submitFlowRequest.getProjectName());
                if (b) {
                    List<OrchestratorRelationVo> orchestratorRelationVos = map.get(submitFlowRequest.getProjectName());
                    orchestratorRelationVos.add(new OrchestratorRelationVo(submitFlowRequest.getFlowId(), submitFlowRequest.getOrchestratorId()));
                } else {
                    List<OrchestratorRelationVo> orchestratorRelationVos = new ArrayList<>();
                    orchestratorRelationVos.add(new OrchestratorRelationVo(submitFlowRequest.getFlowId(), submitFlowRequest.getOrchestratorId()));
                    DSSOrchestratorInfo orchestrator = orchestratorMapper.getOrchestrator(submitFlowRequest.getOrchestratorId());
                    long projectId = orchestrator.getProjectId();
                    projectMap.put(submitFlowRequest.getProjectName(), projectId);
                    map.put(submitFlowRequest.getProjectName(), orchestratorRelationVos);
                }
            } catch (Exception e) {
                return Message.error("提交工作流失败，请保存工作流重试，原因为："+  e.getMessage());
            }

        }

        String label = batchSubmitRequest.getLabels().getRoute();
        String comment = batchSubmitRequest.getComment();
        try {
            orchestratorPluginService.batchSubmitFlow(map, projectMap, userName, workspace, label, comment);
        } catch (Exception e) {
            return Message.error("提交工作流失败，请保存工作流重试，原因为："+  e.getMessage());
        }

        return Message.ok();
    }


    @RequestMapping(value = "submitFlow", method = RequestMethod.POST)
    public Message submitFlow(@RequestBody OrchestratorSubmitRequest submitFlowRequest) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        String ticketId = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> DSSWorkFlowConstant.BDP_USER_TICKET_ID.equals(cookie.getName()))
                .findFirst().map(Cookie::getValue).get();

        try {
            checkSubmitWorkflow(ticketId, submitFlowRequest, workspace, userName);
            orchestratorPluginService.submitFlow(submitFlowRequest, userName, workspace);
        } catch (Exception e) {
            return Message.error("提交工作流失败，请保存工作流重试，原因为："+  e.getMessage());
        }


        return Message.ok();
    }

    private void checkSubmitWorkflow(String ticketId, OrchestratorSubmitRequest submitFlowRequest, Workspace workspace, String userName) throws DSSErrorException{
        Long orchestratorId = submitFlowRequest.getOrchestratorId();
        try {
            checkWorkspace(orchestratorId, workspace);
        } catch (Exception e) {
            LOGGER.error("check failed, the reason is: ", e);
            throw new DSSErrorException(80001, "提交失败，原因为：" + e.getMessage());
        }

        List<DSSLabel> dssLabelList = new ArrayList<>();
        dssLabelList.add(new EnvDSSLabel(submitFlowRequest.getLabels().getRoute()));

        DSSOrchestratorVersion latestOrchestratorVersion = orchestratorFrameworkService.getLatestOrchestratorVersion(orchestratorId);
        Long flowId = latestOrchestratorVersion.getAppId();
        DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(flowId);
        submitFlowRequest.setFlowId(flowId);
        if (flowEditLock != null && !flowEditLock.getOwner().equals(ticketId)) {
            throw new DSSErrorException(80001,"当前工作流被用户" + flowEditLock.getUsername() + "已锁定编辑，您编辑的内容不能再被保存。如有疑问，请与" + flowEditLock.getUsername() + "确认");
        }
        lockFlow(flowId, userName, ticketId);


    }

    private void lockFlow(Long flowID, String username, String ticketId) throws DSSErrorException {
        DSSFlow dssFlow = flowService.getFlowByID(flowID);
        // 尝试获取工作流编辑锁
        try {
            //只有父工作流才有锁，子工作流复用父工作流的锁
            if(dssFlow.getRootFlow()) {
                String flowEditLock = DSSFlowEditLockManager.tryAcquireLock(dssFlow, username, ticketId);
                dssFlow.setFlowEditLock(flowEditLock);
            }
        } catch (DSSErrorException e) {
            if (DSSWorkFlowConstant.EDIT_LOCK_ERROR_CODE == e.getErrCode()) {
                DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(flowID);
                throw new DSSErrorException(60056,"用户已锁定编辑错误码，editLockInfo:" + flowEditLock);
            }
            throw e;
        }
    }

    @RequestMapping(path = "gitUrl", method = RequestMethod.GET)
    public Message gitUrl(@RequestParam(required = true, name = "projectName") String projectName,
                          @RequestParam(required = false, name = "workflowName") String workflowName,
                          HttpServletResponse response) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitAddMemberRequest gitAddMemberRequest = new GitAddMemberRequest(workspace.getWorkspaceId(), projectName, userName, workflowName);
        GitAddMemberResponse addMemberResponse = RpcAskUtils.processAskException(sender.ask(gitAddMemberRequest), GitAddMemberResponse.class, GitAddMemberRequest.class);
        return Message.ok().data("gitUrl", addMemberResponse.getGitUrl());

    }

    @RequestMapping(value = "submitFlow/status", method = RequestMethod.GET)
    public Message submitFlow(@RequestParam Long orchestratorId) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        try {
            checkWorkspace(orchestratorId, workspace);
        } catch (Exception e) {
            LOGGER.error("check failed, the reason is: ", e);
            return Message.error("提交失败，原因为：" + e.getMessage());
        }

        OrchestratorSubmitJob orchestratorSubmitJob = orchestratorFrameworkService.getOrchestratorStatus(orchestratorId);
        // 未提交
        if (orchestratorSubmitJob == null) {
            return Message.error("该编排未开始提交");
        }
        String status = orchestratorSubmitJob.getStatus();
        if (OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED.equals(status)) {
            return Message.error("提交失败，原因为：" + orchestratorSubmitJob.getErrorMsg());
        }
        return Message.ok().data("status", status);
    }

    @RequestMapping(value = "publish/history", method = RequestMethod.GET)
    public Message getHistory(@RequestParam Long orchestratorId, @RequestParam String projectName) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        try {
            checkWorkspace(orchestratorId, workspace);
        } catch (Exception e) {
            LOGGER.error("check failed, the reason is: ", e);
            return Message.error("提交失败，原因为：" + e.getMessage());
        }

        GitHistoryResponse history = orchestratorFrameworkService.getHistory(workspace.getWorkspaceId(), orchestratorId, projectName);

        return Message.ok().data("history", history);
    }

    private void checkWorkspace(Long orchestratorId, Workspace workspace) throws DSSErrorException{
        OrchestratorVo orchestratorVoById = orchestratorService.getOrchestratorVoById(orchestratorId);
        if (orchestratorVoById == null) {
            DSSExceptionUtils.dealErrorException(80001, "编排不存在", DSSErrorException.class);
        }
        long projectId = orchestratorVoById.getDssOrchestratorInfo().getProjectId();
        ProjectInfoRequest projectInfoRequest = new ProjectInfoRequest();
        projectInfoRequest.setProjectId(projectId);
        DSSProject dssProject = (DSSProject) DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender().ask(projectInfoRequest);
        if (dssProject.getWorkspaceId() != workspace.getWorkspaceId()) {
            DSSExceptionUtils.dealErrorException(63335, "工作流所在工作空间和cookie中不一致，请刷新页面后，再次发布！", DSSErrorException.class);
        }
    }

    @RequestMapping(value = "allType", method = RequestMethod.GET)
    public Message allType(HttpServletRequest req) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        return Message.ok().data("type", GitConstant.GIT_SERVER_SEARCH_TYPE);
    }


    @RequestMapping(value = "/getAllOrchestratorMeta", method = RequestMethod.POST)
    public Message getAllOrchestratorMeta(HttpServletRequest httpServletRequest, @RequestBody OrchestratorMetaRequest orchestratorMetaRequest) {

        if (orchestratorMetaRequest.getPageNow() == null) {
            orchestratorMetaRequest.setPageNow(1);
        }

        if (orchestratorMetaRequest.getPageSize() == null) {
            orchestratorMetaRequest.setPageSize(10);
        }

        if (orchestratorMetaRequest.getWorkspaceId() == null) {
            Long workspaceId = SSOHelper.getWorkspace(httpServletRequest).getWorkspaceId();
            orchestratorMetaRequest.setWorkspaceId(workspaceId);
        }

        String userName = SecurityFilter.getLoginUsername(httpServletRequest);
        List<Long> totals = new ArrayList<>();

        List<OrchestratorMeta> orchestratorMetaList = orchestratorFrameworkService.getAllOrchestratorMeta(orchestratorMetaRequest, totals,userName);

        return Message.ok().data("data", orchestratorMetaList).data("total", totals.get(0));
    }


    @RequestMapping(value = "/modifyOrchestratorMeta", method = RequestMethod.POST)
    public Message modifyOrchestratorMeta(HttpServletRequest httpServletRequest, @RequestBody ModifyOrchestratorMetaRequest modifyOrchestratorMetaRequest) {

        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        if (orchestratorFrameworkService.getOrchestratorCopyStatus(modifyOrchestratorMetaRequest.getOrchestratorId())) {
            return Message.error("当前工作流正在被复制，不允许编辑");
        }

        DSSOrchestratorVersion orchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(modifyOrchestratorMetaRequest.getOrchestratorId(),1);

        if(orchestratorVersion == null){
            return Message.error(String.format("%s工作流不存在或已被删除，请重新查询后进行编辑",modifyOrchestratorMetaRequest.getOrchestratorName()));
        }

        String proxyUser = modifyOrchestratorMetaRequest.getProxyUser();
        // 添加代理用户验证是否合法
        if(!StringUtils.isEmpty(proxyUser) && !Pattern.compile("^[a-zA-Z0-9_]+$").matcher(proxyUser).find()){
            return Message.error(String.format("%s代理用名称输入不合法",proxyUser));
        }

        DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(orchestratorVersion.getAppId());
        //若工作流已经被其他用户抢锁，则当前用户不能再保存成功
        String ticketId = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> DSSWorkFlowConstant.BDP_USER_TICKET_ID.equals(cookie.getName()))
                .findFirst().map(Cookie::getValue).get();
        if (flowEditLock != null && !flowEditLock.getOwner().equals(ticketId)) {
            return Message.error("当前工作流被用户" + flowEditLock.getUsername() + "已锁定编辑，您编辑的内容不能再被保存。如有疑问，请与" + flowEditLock.getUsername() + "确认");
        }

        modifyOrchestratorMetaRequest.setWorkspaceId(workspace.getWorkspaceId());
        modifyOrchestratorMetaRequest.setWorkspaceName(workspace.getWorkspaceName());

        try {
            orchestratorFrameworkService.modifyOrchestratorMeta(username, modifyOrchestratorMetaRequest, workspace,orchestratorVersion);
        } catch (Exception e) {
            LOGGER.error(String.format("%s modify OrchestratorMeta fail", modifyOrchestratorMetaRequest.getOrchestratorName()));
            e.printStackTrace();
            return Message.error(String.format("%s 工作流信息编辑失败", modifyOrchestratorMetaRequest.getOrchestratorName()), e);
        }

        return Message.ok(String.format("%s工作流信息编辑成功", modifyOrchestratorMetaRequest.getOrchestratorName()));
    }


    /**
     * 获取编排状态
     **/
    @RequestMapping(value = "/getOrchestratorGitStatus", method = RequestMethod.GET)
    public Message getOrchestratorGitStatus() {

        return Message.ok().data("status", orchestratorService.getOrchestratorGitStatus());
    }


    /**
     * 获取编排名称
     **/
    @RequestMapping(value = "/getAllOrchestratorName", method = RequestMethod.GET)
    public Message getAllOrchestratorName(HttpServletRequest httpServletRequest,
                                          @RequestParam(value = "workspaceId", required = false) Long workspaceId,
                                          @RequestParam(value = "projectName",required = false) String projectName) {

        if (workspaceId == null) {
            workspaceId = SSOHelper.getWorkspace(httpServletRequest).getWorkspaceId();
        }
        LOGGER.info(String.format("getAllOrchestratorName workspaceId is %s", workspaceId));
        return Message.ok().data("data", orchestratorService.getAllOrchestratorName(workspaceId,projectName));
    }

}
