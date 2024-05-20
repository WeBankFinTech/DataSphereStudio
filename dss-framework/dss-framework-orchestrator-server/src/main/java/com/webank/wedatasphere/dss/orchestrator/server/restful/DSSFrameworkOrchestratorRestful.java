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
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.git.common.protocol.GitTree;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserInfoRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitHistoryResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserInfoResponse;
import com.webank.wedatasphere.dss.git.common.protocol.util.UrlUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.conf.OrchestratorConf;
import com.webank.wedatasphere.dss.orchestrator.server.constant.OrchestratorLevelEnum;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.*;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorCopyHistory;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorUnlockVo;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorFrameworkService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorPluginService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestLockWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseLockWorkflow;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.LockMapper;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowEditLock;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.rpc.Sender;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
            String newVersion = orchestratorService.rollbackOrchestrator(username, projectId, projectName, orchestratorId, version, labels, workspace);
            Message message = Message.ok("回滚版本成功").data("newVersion", newVersion);
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

    @RequestMapping(value = "diffFlow", method = RequestMethod.POST)
    public Message diff(@RequestBody OrchestratorSubmitRequest submitFlowRequest) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);
        List<DSSLabel> dssLabelList = new ArrayList<>();
        dssLabelList.add(new EnvDSSLabel(submitFlowRequest.getLabels().getRoute()));

        String ticketId = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> DSSWorkFlowConstant.BDP_USER_TICKET_ID.equals(cookie.getName()))
                .findFirst().map(Cookie::getValue).get();
        DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(submitFlowRequest.getFlowId());
        if (flowEditLock != null && !flowEditLock.getOwner().equals(ticketId)) {
            return Message.error("当前工作流被用户" + flowEditLock.getUsername() + "已锁定编辑，您编辑的内容不能再被保存。如有疑问，请与" + flowEditLock.getUsername() + "确认");
        }
        GitTree gitTree = orchestratorPluginService.diffFlow(submitFlowRequest, userName, workspace);
        return Message.ok().data("tree", gitTree.getChildren());
    }

    @RequestMapping(value = "submitFlow", method = RequestMethod.POST)
    public Message submitFlow(@RequestBody OrchestratorSubmitRequest submitFlowRequest) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        List<DSSLabel> dssLabelList = new ArrayList<>();
        dssLabelList.add(new EnvDSSLabel(submitFlowRequest.getLabels().getRoute()));

        String ticketId = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> DSSWorkFlowConstant.BDP_USER_TICKET_ID.equals(cookie.getName()))
                .findFirst().map(Cookie::getValue).get();
        DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(submitFlowRequest.getFlowId());
        if (flowEditLock != null && !flowEditLock.getOwner().equals(ticketId)) {
            return Message.error("当前工作流被用户" + flowEditLock.getUsername() + "已锁定编辑，您编辑的内容不能再被保存。如有疑问，请与" + flowEditLock.getUsername() + "确认");
        }

        orchestratorPluginService.submitFlow(submitFlowRequest, userName, workspace);
        return Message.ok();
    }

    @RequestMapping(path = "gitUrl", method = RequestMethod.GET)
    public Message gitUrl(@RequestParam(required = true, name = "projectName") String projectName,
                          @RequestParam(required = false, name = "workflowName") String workflowName,
                          @RequestParam(required = false, name = "workflowNodeName") String workflowNodeName,
                          HttpServletResponse response) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitUserInfoRequest gitUserInfoRequest = new GitUserInfoRequest();
        gitUserInfoRequest.setWorkspaceId(workspace.getWorkspaceId());
        gitUserInfoRequest.setType(GitConstant.GIT_ACCESS_READ_TYPE);

        GitUserInfoResponse readInfoResponse = RpcAskUtils.processAskException(sender.ask(gitUserInfoRequest), GitUserInfoResponse.class, GitUserInfoRequest.class);
        String gitUsername = readInfoResponse.getGitUser().getGitUser();
        String gitPassword = readInfoResponse.getGitUser().getGitPassword();
        String gitUrlPre = UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue());
        String authenToken = "";
        try {
            authenToken = orchestratorService.getAuthenToken(gitUrlPre, gitUsername, gitPassword);
        } catch (ExecutionException e) {
            LOGGER.error("git登陆失败，原因为: ", e);
            return Message.error("git登陆失败，请检查git节点配置的用户名/密码/url");
        }
        // 获取顶级域名 eg: http://git.bdp.weoa.com -> weoa.com
        String domainIp = UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue());
        int lastDotIndex = domainIp.lastIndexOf(".");
        String topDomain = "";
        if (lastDotIndex != -1) {
            int secondToLastIndexOf = domainIp.substring(0, lastDotIndex).lastIndexOf(".");
            if (secondToLastIndexOf != -1) {
                topDomain = domainIp.substring(secondToLastIndexOf);
            }
        }
        String cookie = "_gitlab_session=" + authenToken + ";path=/; Domain="+ topDomain +"; HttpOnly; +";
        LOGGER.info("Cookie is {}", cookie);
        response.setHeader("Access-Control-Allow-Origin", topDomain);
        response.addHeader("Set-Cookie", cookie);
        // 获取命名空间
        GitUserInfoRequest gitWriteUserRequest = new GitUserInfoRequest();
        gitWriteUserRequest.setWorkspaceId(workspace.getWorkspaceId());
        gitWriteUserRequest.setType(GitConstant.GIT_ACCESS_WRITE_TYPE);
        GitUserInfoResponse writeInfoResponse = RpcAskUtils.processAskException(sender.ask(gitWriteUserRequest), GitUserInfoResponse.class, GitUserInfoRequest.class);
        String namespace = writeInfoResponse.getGitUser().getGitUser();
        // 拼接跳转的url
        String gitUrlProject = gitUrlPre + "/" + namespace + "/" + projectName;
        if (!StringUtils.isEmpty(workflowName)) {
            gitUrlProject +=  "/tree/master/" + workflowName;
        }
        return Message.ok().data("gitUrl", UrlUtils.normalizeIp(gitUrlProject));

    }

    @RequestMapping(value = "submitFlow/status", method = RequestMethod.GET)
    public Message submitFlow(@RequestParam Long orchestratorId) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        String orchestratorStatus = orchestratorFrameworkService.getOrchestratorStatus(orchestratorId);

        return Message.ok().data("status", orchestratorStatus);
    }

    @RequestMapping(value = "publish/history", method = RequestMethod.GET)
    public Message getHistory(@RequestParam Long orchestratorId, @RequestParam String projectName) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        GitHistoryResponse history = orchestratorFrameworkService.getHistory(workspace.getWorkspaceId(), orchestratorId, projectName);

        return Message.ok().data("history", history);
    }

    @RequestMapping(value = "allType", method = RequestMethod.GET)
    public Message allType(@RequestParam Long orchestratorId, @RequestParam String projectName) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);

        List<String> type = Arrays.asList(".sql",".hql",".jdbc", ".py", ".python", ".scala", ".sh");

        return Message.ok().data("type", type);
    }
}
