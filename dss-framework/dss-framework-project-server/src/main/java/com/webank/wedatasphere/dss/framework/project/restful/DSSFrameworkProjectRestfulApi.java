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
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.framework.project.conf.ProjectConf;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.service.ProjectHttpRequestHook;
import com.webank.wedatasphere.dss.framework.project.utils.ApplicationArea;
import com.webank.wedatasphere.dss.framework.proxy.exception.DSSProxyUserErrorException;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    private Message executePreHook(Function<ProjectHttpRequestHook, Message> function) {
        String errorMsg = projectHttpRequestHooks.stream().map(function).filter(Objects::nonNull).map(Message::getMessage)
                .collect(Collectors.joining(", "));
        if(StringUtils.isNotBlank(errorMsg)) {
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
    @RequestMapping(path = "getAllProjects", method = RequestMethod.POST)
    public Message getAllProjects(HttpServletRequest request, @RequestBody ProjectQueryRequest projectRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        projectRequest.setUsername(username);
        Message message = executePreHook(projectHttpRequestHook -> projectHttpRequestHook.beforeGetAllProjects(request, projectRequest));
        if(message != null) {
            return message;
        }
        LOGGER.info("user {} begin to getAllProjects, projectId: {}.", username, projectRequest.getId());
        List<ProjectResponse> dssProjectVos = projectService.getListByParam(projectRequest);
        if(!CollectionUtils.isEmpty(dssProjectVos) && projectRequest.getFilterProject()){
            dssProjectVos = dssProjectVos.stream().filter(item->
                    (item.getEditUsers().contains(username) || item.getReleaseUsers().contains(username))
                            && item.getEditable()).collect(Collectors.toList());
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
        if(projectCreateRequest.getEditUsers() == null) {
            projectCreateRequest.setEditUsers(new ArrayList<>());
        }
        if(projectCreateRequest.getReleaseUsers() == null) {
            projectCreateRequest.setReleaseUsers(new ArrayList<>());
        }
        Message message = executePreHook(projectHttpRequestHook -> projectHttpRequestHook.beforeCreateProject(request, projectCreateRequest));
        if(message != null) {
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
                            dssProjectVo.getId(),dssProjectVo.getName(), OperateTypeEnum.CREATE,projectCreateRequest);
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
    public Message getProjectInfoByName(@RequestParam(name = "projectName") String projectName){
        DSSProjectDO dbProject = dssProjectService.getProjectByName(projectName);
        Message message;
        if(dbProject==null){
            String msg = String.format("project %s does not exist.", projectName);
            message = Message.error(msg);
        }else{
            DSSProjectVo dssProjectVo = new DSSProjectVo();
            dssProjectVo.setDescription(dbProject.getDescription());
            dssProjectVo.setId(dbProject.getId());
            dssProjectVo.setName(dbProject.getName());
            message = Message.ok().data("project", dssProjectVo);
        }
        return  message;
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
        if(message != null) {
            return message;
        }
        if (projectModifyRequest.getDescription().length() > MAX_DESC_LENGTH) {
            return Message.error("The project description information is too long, exceeding the maximum length:" + MAX_DESC_LENGTH);
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(projectModifyRequest.getBusiness()) && projectModifyRequest.getBusiness().length() > MAX_BUSSINESS_SIZE){
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
                            projectModifyRequest.getId(),projectModifyRequest.getName(), OperateTypeEnum.UPDATE,projectModifyRequest);
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
        if(message != null) {
            return message;
        }
        LOGGER.info("user {} begin to deleteProject, workspace {}, project entity: {}.", username, workspace.getWorkspaceName(), projectDeleteRequest);
        return DSSExceptionUtils.getMessage(() -> {
                    // 检查是否具有删除项目权限
                    projectService.isDeleteProjectAuth(projectDeleteRequest.getId(), username);
                    DSSProjectDO dssProjectDO= projectService.getProjectById(projectDeleteRequest.getId());
                    projectService.deleteProject(username, projectDeleteRequest, workspace,dssProjectDO);
                    AuditLogUtils.printLog(username, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.PROJECT,
                            dssProjectDO.getId(),dssProjectDO.getName(), OperateTypeEnum.DELETE,projectDeleteRequest);                },
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
        if(message != null) {
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


}
