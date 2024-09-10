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

package com.webank.wedatasphere.dss.framework.project.restful;

import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.framework.project.conf.ProjectConf;
import com.webank.wedatasphere.dss.framework.project.contant.DSSProjectConstant;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.request.*;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.service.ProjectHttpRequestHook;
import com.webank.wedatasphere.dss.framework.project.utils.ApplicationArea;
import com.webank.wedatasphere.dss.framework.proxy.conf.ProxyUserConfiguration;
import com.webank.wedatasphere.dss.framework.proxy.exception.DSSProxyUserErrorException;
import com.webank.wedatasphere.dss.framework.proxy.service.DssProxyUserService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.service.StaffInfoGetter;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitSearchRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitSearchResponse;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.linkis.common.exception.WarnException;
import org.apache.linkis.rpc.Sender;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper.WORKSPACE_ID_COOKIE_KEY;
import static com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper.WORKSPACE_NAME_COOKIE_KEY;

@RequestMapping(path = "/dss/framework/project", produces = {"application/json"})
@RestController
public class DSSFrameworkProjectRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkProjectRestfulApi.class);
    private static final int MAX_DESC_LENGTH = ProjectConf.MAX_DESC_LENGTH.getValue();
    private static final int MAX_BUSSINESS_SIZE = 200;
    @Autowired
    DSSFrameworkProjectService dssFrameworkProjectService;
    @Autowired
    private DSSProjectService projectService;
    @Autowired
    private DSSProjectService dssProjectService;
    @Autowired
    private List<ProjectHttpRequestHook> projectHttpRequestHooks;
    @Autowired
    private DssProxyUserService dssProxyUserService;
    @Autowired
    private StaffInfoGetter staffInfoGetter;

    @Autowired
    private DSSWorkspaceRoleService dssWorkspaceRoleService;

    @Autowired
    private DSSWorkspaceService dssWorkspaceService;

    private Message executePreHook(Function<ProjectHttpRequestHook, Message> function) {
        String errorMsg = projectHttpRequestHooks.stream().map(function).filter(Objects::nonNull).map(Message::getMessage)
                .collect(Collectors.joining(", "));
        if (StringUtils.isNotBlank(errorMsg)) {
            return Message.error(errorMsg);
        } else {
            return null;
        }
    }

    private Message executeAfterHook(Consumer<ProjectHttpRequestHook> consumer, Supplier<Message> supplier) {
        try {
            projectHttpRequestHooks.forEach(consumer);
        } catch (WarnException e) {
            LOGGER.error("execute after hook failed!", e);
            return Message.error("Execute hook failed, reason: " + e.getDesc());
        } catch (Exception e) {
            LOGGER.error("execute after hook failed!", e);
            return Message.error("Execute hook failed, reason: " + ExceptionUtils.getRootCauseMessage(e));
        }
        return supplier.get();
    }

    /**
     * 获取所有工程或者单个工程
     *
     * @param request
     * @param projectRequest
     * @return
     */
    @RequestMapping(path = {"getAllProjects","getAllProdProjects"}, method = RequestMethod.POST)
    public Message getAllProjects(HttpServletRequest request, @RequestBody ProjectQueryRequest projectRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        projectRequest.setUsername(username);
        Message message = executePreHook(projectHttpRequestHook -> projectHttpRequestHook.beforeGetAllProjects(request, projectRequest));
        if(message != null) {
            return message;
        }

        try {
            dssWorkspaceService.getWorkspacesById(projectRequest.getWorkspaceId(), username);
        } catch (DSSErrorException e) {
            LOGGER.error("User {} get workspace {} failed.", username, projectRequest.getWorkspaceId(), e);
            return Message.error(e);
        }
        List<String> roles = dssWorkspaceRoleService.getRoleInWorkspace(username, projectRequest.getWorkspaceId().intValue());
        if (roles == null || roles.isEmpty()) {
            LOGGER.error("username {}, in workspace {} roles are null or empty", username, projectRequest.getWorkspaceId());
            return Message.error("can not get roles information");
        }

        LOGGER.info("user {} begin to getAllProjects, projectId: {}.", username, projectRequest.getId());
        List<ProjectResponse> dssProjectVos = projectService.getListByParam(projectRequest);
        if(!CollectionUtils.isEmpty(dssProjectVos) && projectRequest.getFilterProject()){
            dssProjectVos = dssProjectVos.stream().filter(item->
                    (item.getEditUsers().contains(username) || item.getReleaseUsers().contains(username))
                            && item.getEditable()).collect(Collectors.toList());
        }
        //如果是生产中心请求，则靠考虑用户或者代理用户是否有发布权限
        if(request.getServletPath().endsWith("getAllProdProjects")){
            String proxyUser=username;
            if(ProxyUserConfiguration.isProxyUserEnable()) {
                try {
                    proxyUser = dssProxyUserService.getProxyUser(request);
                } catch (DSSProxyUserErrorException e) {
                    //do nothing
                }
            }
            final String finalProxyUsername = proxyUser;
            dssProjectVos = dssProjectVos.stream().filter(
                    item-> item.getReleaseUsers().contains(username)
                    ||item.getReleaseUsers().contains(finalProxyUsername)
            ).collect(Collectors.toList());
        }
        return Message.ok("获取工作空间的工程成功").data("projects", dssProjectVos);
    }

    /**
     * 新建工程,通过和各个AppConn进行交互，将需要满足工程规范的所有的appconn进行创建工程
     */
    @RequestMapping(path = "createProject", method = RequestMethod.POST)
    public Message createProject(HttpServletRequest request, @RequestBody ProjectCreateRequest projectCreateRequest) throws DSSProxyUserErrorException {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        LOGGER.info("user {} begin to createProject, workspace {}, project entity: {}.", username, workspace.getWorkspaceName(), projectCreateRequest);
        if (projectCreateRequest.getEditUsers() == null) {
            projectCreateRequest.setEditUsers(new ArrayList<>());
        }
        if (projectCreateRequest.getReleaseUsers() == null) {
            projectCreateRequest.setReleaseUsers(new ArrayList<>());
        }
        Message message = executePreHook(projectHttpRequestHook -> projectHttpRequestHook.beforeCreateProject(request, projectCreateRequest));
        if (message != null) {
            return message;
        }
        //將创建人默认为发布权限和編輯权限
        if (!projectCreateRequest.getEditUsers().contains(username)) {
            projectCreateRequest.getEditUsers().add(username);
        }
        if (!projectCreateRequest.getReleaseUsers().contains(username)) {
            projectCreateRequest.getReleaseUsers().add(username);
        }
        return DSSExceptionUtils.getMessage(() -> {
                    DSSProjectVo dssProjectVo = dssFrameworkProjectService.createProject(projectCreateRequest, username, workspace);
                    AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.PROJECT,
                            dssProjectVo.getId(), dssProjectVo.getName(), OperateTypeEnum.CREATE, projectCreateRequest);
                    return dssProjectVo;
                },
                dssProjectVo ->
                        executeAfterHook(projectHttpRequestHook -> projectHttpRequestHook.afterCreateProject(request, projectCreateRequest, dssProjectVo),
                                () -> Message.ok("创建工程成功.").data("project", dssProjectVo)),
                String.format("用户 %s 创建工程 %s 失败. ", username, projectCreateRequest.getName()));
    }

    @RequestMapping(path = "checkProjectName", method = RequestMethod.GET)
    public Message checkProjectName(HttpServletRequest request, @RequestParam(name = "name") String name) {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        LOGGER.info("user {} begin to checkProjectName: {}", username, name);
        return DSSExceptionUtils.getMessage(() -> dssFrameworkProjectService.checkProjectName(name, workspace, username),
                () -> Message.ok().data("repeat", false), String.format("用户 %s，创建工程名 %s 失败. ", username, name));
    }

    @RequestMapping(path = "getProjectInfo", method = RequestMethod.GET)
    public Message getProjectInfoByName(@RequestParam(name = "projectName") String projectName) {
        DSSProjectDO dbProject = dssProjectService.getProjectByName(projectName);
        Message message;
        if (dbProject == null) {
            String msg = String.format("project %s does not exist.", projectName);
            message = Message.error(msg);
        } else {
            DSSProjectVo dssProjectVo = new DSSProjectVo();
            dssProjectVo.setDescription(dbProject.getDescription());
            dssProjectVo.setId(dbProject.getId());
            dssProjectVo.setName(dbProject.getName());
            message = Message.ok().data("project", dssProjectVo);
        }
        return message;
    }

    /**
     * 编辑工程
     *
     * @param request
     * @param projectModifyRequest
     * @return
     */
    @RequestMapping(path = "modifyProject", method = RequestMethod.POST)
    public Message modifyProject(HttpServletRequest request, @RequestBody ProjectModifyRequest projectModifyRequest) throws Exception {
        if (projectModifyRequest.getId() == null || projectModifyRequest.getId() < 0) {
            return Message.error("project id is null, cannot modify it.");
        }
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        LOGGER.info("user {} begin to modifyProject, workspace {}, project entity: {}.", username, workspace.getWorkspaceName(), projectModifyRequest);
        Message message = executePreHook(projectHttpRequestHook -> projectHttpRequestHook.beforeModifyProject(request, projectModifyRequest));
        if (message != null) {
            return message;
        }
        if (projectModifyRequest.getDescription().length() > MAX_DESC_LENGTH) {
            return Message.error("The project description information is too long, exceeding the maximum length:" + MAX_DESC_LENGTH);
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(projectModifyRequest.getBusiness()) && projectModifyRequest.getBusiness().length() > MAX_BUSSINESS_SIZE) {
            return Message.error("The project bussiness is too long, exceeding the maximum length:" + MAX_BUSSINESS_SIZE);
        }
        DSSProjectDO dbProject = dssProjectService.getProjectById(projectModifyRequest.getId());
        //工程不存在
        if (dbProject == null) {
            LOGGER.error("project {} is not exists.", projectModifyRequest.getName());
            return Message.error(String.format("project %s is not exists.", projectModifyRequest.getName()));
        }
        String createUsername = dbProject.getUsername();
        //將创建人默认为发布权限和編輯权限
        if (!projectModifyRequest.getEditUsers().contains(createUsername)) {
            projectModifyRequest.getEditUsers().add(createUsername);
        }
        List<String> releaseUsers = projectModifyRequest.getReleaseUsers();
        if (releaseUsers == null) {
            releaseUsers = new ArrayList<>();
            projectModifyRequest.setReleaseUsers(releaseUsers);
        }
        if (!releaseUsers.contains(createUsername)) {
            releaseUsers.add(createUsername);
        }
        return DSSExceptionUtils.getMessage(() -> {
                    dssFrameworkProjectService.modifyProject(projectModifyRequest, dbProject, username, workspace);
                    AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.PROJECT,
                            projectModifyRequest.getId(), projectModifyRequest.getName(), OperateTypeEnum.UPDATE, projectModifyRequest);
                },
                () ->
                        executeAfterHook(projectHttpRequestHook -> projectHttpRequestHook.afterModifyProject(request, projectModifyRequest),
                                () -> Message.ok("修改工程成功.")),
                String.format("用户 %s 修改工程 %s 失败. ", username, projectModifyRequest.getName()));
    }

    /**
     * 删除工程
     *
     * @param request
     * @param projectDeleteRequest
     * @return
     */
    @RequestMapping(path = "deleteProject", method = RequestMethod.POST)
    public Message deleteProject(HttpServletRequest request, @RequestBody ProjectDeleteRequest projectDeleteRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        Message message = executePreHook(projectHttpRequestHook -> projectHttpRequestHook.beforeDeleteProject(request, projectDeleteRequest));
        if (message != null) {
            return message;
        }
        LOGGER.info("user {} begin to deleteProject, workspace {}, project entity: {}.", username, workspace.getWorkspaceName(), projectDeleteRequest);
        return DSSExceptionUtils.getMessage(() -> {
                    // 检查是否具有删除项目权限
                    projectService.isDeleteProjectAuth(projectDeleteRequest.getId(), username);
                    DSSProjectDO dssProjectDO = projectService.getProjectById(projectDeleteRequest.getId());
                    projectService.deleteProject(username, projectDeleteRequest, workspace, dssProjectDO);
                    AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.PROJECT,
                            dssProjectDO.getId(), dssProjectDO.getName(), OperateTypeEnum.DELETE, projectDeleteRequest);
                },
                () ->
                        executeAfterHook(projectHttpRequestHook -> projectHttpRequestHook.afterDeleteProject(request, projectDeleteRequest),
                                () -> Message.ok("删除工程成功.")),
                String.format("用户 %s 删除工程失败. ", username));
    }

    @RequestMapping(path = "listApplicationAreas", method = RequestMethod.GET)
    public Message listApplicationAreas(HttpServletRequest req) {
        String header = req.getHeader("Content-language").trim();
        ApplicationArea[] applicationAreas = ApplicationArea.values();
        List<String> areas = new ArrayList<>();
        Arrays.stream(applicationAreas).forEach(item -> {
            if ("zh-CN".equals(header)) {
                areas.add(item.getName());
            } else {
                areas.add(item.getEnName());
            }
        });
        return Message.ok().data("applicationAreas", areas);
    }

    @RequestMapping(path = "getProjectAbilities", method = RequestMethod.GET)
    public Message getProjectAbilities(HttpServletRequest request) {
        //为了获取到此环境的能力，导入 导出  发布等
        String username = SecurityFilter.getLoginUsername(request);
        try {
            List<String> projectAbilities = projectService.getProjectAbilities(username);
            return Message.ok("获取工程能力成功").data("projectAbilities", projectAbilities);
        } catch (final Throwable t) {
            LOGGER.error("failed to get project ability for user {}", username, t);
            return Message.error("获取工程能力失败");
        }
    }


    /**
     * 获取已删除的所有工程
     */
    @RequestMapping(path = "getDeletedProjects", method = RequestMethod.POST)
    public Message getDeletedProjects(HttpServletRequest request, @Valid @RequestBody ProjectQueryRequest projectRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        projectRequest.setUsername(username);
        Message message = executePreHook(projectHttpRequestHook -> projectHttpRequestHook.beforeGetDeletedProject(request, projectRequest));
        if (message != null) {
            return message;
        }
        List<ProjectResponse> dssProjectVos = projectService.getDeletedProjects(projectRequest);
        return Message.ok("获取工作空间已删除的工程成功").data("projects", dssProjectVos);
    }

    @RequestMapping(path = "searchGit", method = RequestMethod.POST)
    public Message searchGit(HttpServletRequest request, @RequestBody GitSearchRequest searchRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitSearchResponse gitSearchResponse = RpcAskUtils.processAskException(sender.ask(searchRequest), GitSearchResponse.class, GitSearchRequest.class);

        return Message.ok().data("data", gitSearchResponse);
    }

    @RequestMapping(path = "modifyProjectMeta", method = RequestMethod.POST)
    public Message modifyProjectMeta(HttpServletRequest request, @RequestBody ProjectModifyRequest projectModifyRequest) {

        if (projectModifyRequest.getId() == null || projectModifyRequest.getId() < 0) {
            return Message.error("project id is null, cannot modify it.");
        }

        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);

        if(projectModifyRequest.getWorkspaceId() == null){
            projectModifyRequest.setWorkspaceId(workspace.getWorkspaceId());

        }else if(!projectModifyRequest.getWorkspaceId().equals(workspace.getWorkspaceId())){
            return Message.error("命名空间信息和cookie中的命名空间不一致,请确认！！！");
        }

        if (projectModifyRequest.getDescription().length() > MAX_DESC_LENGTH) {
            return Message.error("The project description information is too long, exceeding the maximum length:" + MAX_DESC_LENGTH);
        }

        DSSProjectDO dbProject = dssProjectService.getProjectById(projectModifyRequest.getId());
        //工程不存在
        if (dbProject == null) {
            LOGGER.error("project {} is not exists.", projectModifyRequest.getName());
            return Message.error(String.format("project %s is not exists.", projectModifyRequest.getName()));
        }

        String createUsername = dbProject.getUsername();
        //將创建人默认为发布权限和编辑权限
        if (!projectModifyRequest.getEditUsers().contains(createUsername)) {
            projectModifyRequest.getEditUsers().add(createUsername);
        }

        if (!projectModifyRequest.getReleaseUsers().contains(createUsername)) {
            projectModifyRequest.getReleaseUsers().add(createUsername);
        }

        return DSSExceptionUtils.getMessage(() -> {
                    dssFrameworkProjectService.modifyProjectMeta(projectModifyRequest, dbProject, username, workspace);
                    AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.PROJECT,
                            projectModifyRequest.getId(), projectModifyRequest.getName(), OperateTypeEnum.UPDATE, projectModifyRequest);
                },
                () -> executeAfterHook(projectHttpRequestHook -> projectHttpRequestHook.afterModifyProject(request, projectModifyRequest),
                        () -> Message.ok("修改工程成功.")),
                String.format("用户 %s 修改工程 %s 失败. ", username, projectModifyRequest.getName()));
    }


    @RequestMapping(path = "listAllProjectName", method = RequestMethod.POST)
    public Message listAllProjectName(HttpServletRequest request, @RequestBody ProjectQueryRequest projectRequest) {

        String username = SecurityFilter.getLoginUsername(request);
        projectRequest.setUsername(username);

        if (projectRequest.getWorkspaceId() == null) {
            Workspace workspace = SSOHelper.getWorkspace(request);
            projectRequest.setWorkspaceId(workspace.getWorkspaceId());
        }

        try {
            dssWorkspaceService.getWorkspacesById(projectRequest.getWorkspaceId(), username);
        } catch (DSSErrorException e) {
            LOGGER.error("User {} get workspace {} failed.", username, projectRequest.getWorkspaceId(), e);
            return Message.error(e);
        }
        List<String> roles = dssWorkspaceRoleService.getRoleInWorkspace(username, projectRequest.getWorkspaceId().intValue());
        if (roles == null || roles.isEmpty()) {
            LOGGER.error("username {}, in workspace {} roles are null or empty", username, projectRequest.getWorkspaceId());
            return Message.error("can not get roles information");
        }

        LOGGER.info("user {} begin to listAllProjectName, workspaceId: {}.", username, projectRequest.getWorkspaceId());

        List<String> projectNames = projectService.queryProjectName(projectRequest);

        return Message.ok().data("data", projectNames);
    }

    /**
     * 项目转移
     */
    @RequestMapping(path = "transferProject", method = RequestMethod.POST)
    public Message transferProject(HttpServletRequest request, @RequestBody ProjectTransferRequest projectTransferRequest) {
        if (projectTransferRequest.getProjectName() ==null|| projectTransferRequest.getTransferUserName()==null) {
            return Message.error("project id is null, cannot modify it.");
        }
                LOGGER.info("begin to transferProject,  project entity: {}.", projectTransferRequest);
        DSSProjectDO dbProject = dssProjectService.getProjectByName(projectTransferRequest.getProjectName());
        //工程不存在
        if (dbProject == null) {
            LOGGER.error("project {} is not exists.", projectTransferRequest.getProjectName());
            return Message.error(String.format("project %s is not exists.", projectTransferRequest.getProjectName()));
        }
        String oldProjectOwner = dbProject.getCreateBy();
        String operator =SecurityFilter.getLoginUsername(request); ;
        String workspaceName = dssWorkspaceService.getWorkspaceName(dbProject.getWorkspaceId());
        Long workspaceId = dbProject.getWorkspaceId();
        Workspace workspace = new Workspace();
        workspace.setWorkspaceId(workspaceId);
        workspace.setWorkspaceName(workspaceName);
        SSOHelper.addWorkspaceInfo(request, workspace);
        workspace.addCookie(WORKSPACE_NAME_COOKIE_KEY, workspaceName);
        workspace.addCookie(WORKSPACE_ID_COOKIE_KEY,workspaceId.toString());

        //1.把用户加到工作空间里。
        //2.，把operator加到查看、编辑、发布权限里。
        //3.把create_by和username换成transferUserName，并且把transferUserName加到查看、编辑、发布权限里
        //4.同步git

        try {
            dssFrameworkProjectService.transferProject(projectTransferRequest, dbProject, oldProjectOwner, workspace,
                    operator);
            AuditLogUtils.printLog(oldProjectOwner, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.PROJECT,
                    dbProject.getId(), projectTransferRequest.getProjectName(), OperateTypeEnum.UPDATE, projectTransferRequest);
        } catch (Exception e) {
            String errorMsg = String.format("用户 %s 转移工程 %s 失败. ", operator,
                    projectTransferRequest.getProjectName());
            LOGGER.error(errorMsg, e);
            return Message.error(errorMsg + e.getMessage());
        }
        return Message.ok("转移工程成功.");
    }


    @RequestMapping(path = "queryAllProjects", method = RequestMethod.POST)
    public Message queryAllProject(HttpServletRequest request, @RequestBody ProjectQueryRequest projectRequest){

        String username = SecurityFilter.getLoginUsername(request);
        projectRequest.setUsername(username);
        Message message = executePreHook(projectHttpRequestHook -> projectHttpRequestHook.beforeGetAllProjects(request, projectRequest));
        if (message != null) {
            return message;
        }

        if (projectRequest.getWorkspaceId() == null) {
            Workspace workspace = SSOHelper.getWorkspace(request);
            projectRequest.setWorkspaceId(workspace.getWorkspaceId());
        }

        try {
            dssWorkspaceService.getWorkspacesById(projectRequest.getWorkspaceId(), username);
        } catch (DSSErrorException e) {
            LOGGER.error("User {} get workspace {} failed.", username, projectRequest.getWorkspaceId(), e);
            return Message.error(e);
        }
        List<String> roles = dssWorkspaceRoleService.getRoleInWorkspace(username, projectRequest.getWorkspaceId().intValue());
        if (roles == null || roles.isEmpty()) {
            LOGGER.error("username {}, in workspace {} roles are null or empty", username, projectRequest.getWorkspaceId());
            return Message.error("can not get roles information");
        }

        LOGGER.info("user {} begin to queryAllProjects, projectId: {}.", username, projectRequest.getId());
        if(!StringUtils.isEmpty(projectRequest.getSortBy()) && !StringUtils.isEmpty(projectRequest.getOrderBy())){
            String orderBySql = DSSProjectConstant.concatOrderBySql(projectRequest.getSortBy(),projectRequest.getOrderBy());
            LOGGER.info(String.format("queryAllProjects sort orderBySql is %s", orderBySql));
            if(orderBySql == null){
                return Message.error("queryAllProjects sortBy or orderBy params input error");
            }
            projectRequest.setOrderBySql(orderBySql);
        }


        if(projectRequest.getPageNow() == null || projectRequest.getPageNow() <= 0){
            projectRequest.setPageNow(1);
        }

        if(projectRequest.getPageSize() == null || projectRequest.getPageSize() <= 0 ){
            projectRequest.setPageSize(10);
        }

        List<Long> totals = new ArrayList<>();
        List<ProjectResponse> dssProjectVos = projectService.queryListByParam(projectRequest,totals);

        return Message.ok("获取工作空间的工程成功").data("projects", dssProjectVos).data("total",totals.get(0));

    }

}
