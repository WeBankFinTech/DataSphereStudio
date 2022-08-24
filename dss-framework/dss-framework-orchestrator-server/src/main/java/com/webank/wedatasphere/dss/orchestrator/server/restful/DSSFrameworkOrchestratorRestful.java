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
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import com.webank.wedatasphere.dss.orchestrator.server.constant.OrchestratorLevelEnum;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.*;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorCopyHistory;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorFrameworkService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RequestMapping(path = "/dss/framework/orchestrator", produces = {"application/json"})
@RestController
public class DSSFrameworkOrchestratorRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkOrchestratorRestful.class);

    @Autowired
    private OrchestratorFrameworkService orchestratorFrameworkService;
    @Autowired
    private OrchestratorService orchestratorService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostConstruct
    public void init() {
        AppConnManagerUtils.autoLoadAppConnManager();
    }

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
            return Message.ok("获取编排模式成功").data("page", orchestratorService.getListByPage(orchestratorRequest, username));
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

        return Message.ok("复制工作流已经开始，正在后台复制中...").data("copyJobId", copyJobId);
    }

    /**
     * 获取编排复制任务状态
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/{id}/copyInfo", method = RequestMethod.GET)
    public Message getCopyJobStatus(@PathVariable("id") String copyInfoId) throws Exception {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);

        return Message.ok("获取编排复制任务状态成功").data("orchestratorCopyInfo", orchestratorFrameworkService.getOrchestratorCopyInfoById(copyInfoId));
    }

    /**
     * 查看编排复制历史
     *
     * @param orchestratorId
     * @return
     */
    @RequestMapping(path = "listOrchestratorCopyHistory", method = RequestMethod.GET)
    public Message listOrchestratorCopyHistory(@RequestParam(name = "orchestratorId") Long orchestratorId,
                                               @RequestParam(required = false, name = "currentPage") Integer currentPage,
                                               @RequestParam(required = false, name = "pageSize") Integer pageSize) throws Exception {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        List<OrchestratorCopyHistory> orchestratorCopyHistory = orchestratorFrameworkService.getOrchestratorCopyHistory(username, workspace, orchestratorId, currentPage, pageSize);
        return Message.ok("查找工作流复制历史成功").data("copyJobHistory", orchestratorCopyHistory);
    }

    @RequestMapping(path = "orchestratorLevels", method = RequestMethod.GET)
    public Message getOrchestratorLevels() {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        List<OrchestratorLevelEnum> levels = Arrays.asList(OrchestratorLevelEnum.values());
        LOGGER.info("user {} try to get OrchestratorLevels, workspaceId:{}, result:{}", username, workspace.getWorkspaceId(), levels);
        return Message.ok("获取编排重要级别列表成功").data("orchestratorLevels", levels);
    }
}
