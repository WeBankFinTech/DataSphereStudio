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

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.utils.ApplicationArea;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequestMapping(path = "/dss/framework/project", produces = {"application/json"})
@RestController
public class DSSFrameworkProjectRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkProjectRestfulApi.class);
    @Autowired
    DSSFrameworkProjectService dssFrameworkProjectService;
    @Autowired
    private DSSProjectService projectService;
    @Autowired
    private DSSProjectService dssProjectService;

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
        LOGGER.info("user {} begin to getAllProjects, projectId:{}", username, projectRequest.getId());
        List<ProjectResponse> dssProjectVos = projectService.getListByParam(projectRequest);
        Message message = Message.ok("获取工作空间的工程成功").data("projects", dssProjectVos);
        return message;
    }

    /**
     * 新建工程,通过和各个AppConn进行交互，将需要满足工程规范的所有的appconn进行创建工程
     */
    @RequestMapping(path = "createProject", method = RequestMethod.POST)
    public Message createProject(HttpServletRequest request, @RequestBody ProjectCreateRequest projectCreateRequest) throws Exception {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        //將创建人默认为发布权限和編輯权限
        if (!projectCreateRequest.getEditUsers().contains(username)) {
            projectCreateRequest.getEditUsers().add(username);
        }
        List<String> releaseUsers = projectCreateRequest.getReleaseUsers();
        if (releaseUsers == null) {
            releaseUsers = new ArrayList<>();
            projectCreateRequest.setReleaseUsers(releaseUsers);
        }
        if (!releaseUsers.contains(username)) {
            releaseUsers.add(username);
        }
        LOGGER.info("User {} begin to create project in workspace {}, request params: {}.", username, workspace.getWorkspaceName(), projectCreateRequest);
        return DSSExceptionUtils.getMessage(() -> dssFrameworkProjectService.createProject(projectCreateRequest, username, workspace),
                dssProjectVo -> Message.ok("创建工程成功.").data("project", dssProjectVo),
                String.format("用户 %s 创建工程 %s 失败. ", username, projectCreateRequest.getName()));
    }

    @RequestMapping(path = "checkProjectName", method = RequestMethod.GET)
    public Message checkProjectName(HttpServletRequest request, @RequestParam(name = "name") String name) {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        LOGGER.info("user {} begin to checkProjectName: {}", username, name);
        return DSSExceptionUtils.getMessage(() -> dssFrameworkProjectService.checkProjectName(name, workspace, username),
                () -> Message.ok().data("repeat", false), String.format("用户 %s 创建工程 %s 失败. ", username, name));
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
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        if (projectModifyRequest.getId() == null || projectModifyRequest.getId() < 0) {
            return Message.error("project id is null, cannot modify it.");
        }
        LOGGER.info("user {} begin to modify project in workspace {}, params: {}.", username, workspace.getWorkspaceName(), projectModifyRequest);
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
        return DSSExceptionUtils.getMessage(() -> dssFrameworkProjectService.modifyProject(projectModifyRequest, dbProject, username, workspace),
                () -> Message.ok("修改工程成功."),
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
    public Message deleteProject(HttpServletRequest request, @RequestBody ProjectDeleteRequest projectDeleteRequest) throws Exception {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        LOGGER.info("user {} begin to deleteProject, params:{}", username, projectDeleteRequest);
        return DSSExceptionUtils.getMessage(() -> {
                    // 检查是否具有删除项目权限
                    projectService.isDeleteProjectAuth(projectDeleteRequest.getId(), username);
                    projectService.deleteProject(username, projectDeleteRequest, workspace);
                },
                () -> Message.ok("删除工程成功."),
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
        List<ProjectResponse> dssProjectVos = projectService.getDeletedProjects(projectRequest);
        Message message = Message.ok("获取工作空间已删除的工程成功").data("projects", dssProjectVos);
        return message;
    }


}
