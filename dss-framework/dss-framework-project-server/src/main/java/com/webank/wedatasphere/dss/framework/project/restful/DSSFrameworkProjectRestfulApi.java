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

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.framework.project.utils.ApplicationArea;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
    private DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSProjectUserService projectUserService;

    /**
     * 获取所有工程或者单个工程
     *
     * @param request
     * @return
     */
    @RequestMapping(path ="getWorkSpaceStr", method = RequestMethod.GET)
    public Message getWorkSpaceStr(HttpServletRequest request) {
        Workspace workspace = SSOHelper.getWorkspace(request);
        Message message = Message.ok("").data("workspaceStr", DSSCommonUtils.COMMON_GSON.toJson(workspace));
        return message;
    }

    /**
     * 获取所有工程或者单个工程
     *
     * @param request
     * @param projectRequest
     * @return
     */
    @RequestMapping(path ="getAllProjects", method = RequestMethod.POST)
    public Message getAllProjects(HttpServletRequest request, @RequestBody ProjectQueryRequest projectRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        projectRequest.setUsername(username);
        List<ProjectResponse> dssProjectVos = projectService.getListByParam(projectRequest);
        Message message = Message.ok("获取工作空间的工程成功").data("projects", dssProjectVos);
        return message;
    }

    /**
     * 新建工程,通过和各个AppConn进行交互，将需要满足工程规范的所有的appconn进行创建工程
     */
    @RequestMapping(path ="createProject", method = RequestMethod.POST)
    public Message createProject(HttpServletRequest request, @RequestBody ProjectCreateRequest projectCreateRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        try {
            //將创建人默认为发布权限和編輯权限
            if(!projectCreateRequest.getEditUsers().contains(username)){
                projectCreateRequest.getEditUsers().add(username);
            }
            List<String> releaseUsers = projectCreateRequest.getReleaseUsers();
            if(releaseUsers == null){
                releaseUsers = new ArrayList<>();
                projectCreateRequest.setReleaseUsers(releaseUsers);
            }
            if(!releaseUsers.contains(username)){
                releaseUsers.add(username);
            }
            DSSProjectVo dssProjectVo = dssFrameworkProjectService.createProject(projectCreateRequest, username, workspace,true);
            if (dssProjectVo != null) {
                return Message.ok("创建工程成功").data("project", dssProjectVo);
            } else {
                return Message.error("创建工程失败");
            }
        } catch (final Throwable t) {
            LOGGER.error("failed to create project {} for user {}", projectCreateRequest.getName(), username, t);
            return  Message.error("创建工程失败:" + t.getMessage());
        }
    }

    /**
     * 编辑工程
     *
     * @param request
     * @param projectModifyRequest
     * @return
     */
    @RequestMapping(path ="modifyProject", method = RequestMethod.POST)
    public Message modifyProject(HttpServletRequest request, @RequestBody ProjectModifyRequest projectModifyRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        try {
            //將创建人默认为发布权限和編輯权限
            if(!projectModifyRequest.getEditUsers().contains(username)){
                projectModifyRequest.getEditUsers().add(username);
            }
            List<String> releaseUsers = projectModifyRequest.getReleaseUsers();
            if(releaseUsers == null){
                releaseUsers = new ArrayList<>();
                projectModifyRequest.setReleaseUsers(releaseUsers);
            }
            if(!releaseUsers.contains(username)){
                releaseUsers.add(username);
            }
            dssFrameworkProjectService.modifyProject(projectModifyRequest, username,workspace);
            return Message.ok("修改工程成功");
        } catch (Exception e) {
            LOGGER.error("Failed to modify project {} for user {}", projectModifyRequest.getName(), username, e);
            return Message.error("修改工程失败:" + e.getMessage());
        }
    }

    /**
     * 删除工程
     *
     * @param request
     * @param projectDeleteRequest
     * @return
     */
    @RequestMapping(path ="deleteProject", method = RequestMethod.POST)
    public Message deleteProject(HttpServletRequest request, @RequestBody ProjectDeleteRequest projectDeleteRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        // 删除第三方系统中的项目
        projectDeleteRequest.setIfDelOtherSys(true);
        try{
            // 检查是否具有删除项目权限
            projectService.isDeleteProjectAuth(projectDeleteRequest.getId(), username);


            dssFrameworkProjectService.deleteProject(username, projectDeleteRequest, workspace);
            return  Message.ok("删除工程成功");
        }catch(final Throwable t){
            LOGGER.error("Failed to delete {} for user {}", projectDeleteRequest, username);
            return  Message.error("删除工程失败");
        }
    }

    @RequestMapping(path ="listApplicationAreas", method = RequestMethod.GET)
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

    @RequestMapping(path ="getProjectAbilities", method = RequestMethod.GET)
    public Message getProjectAbilities(HttpServletRequest request){
        //为了获取到此环境的能力，导入 导出  发布等
        String username = SecurityFilter.getLoginUsername(request);
        try{
            List<String> projectAbilities = projectService.getProjectAbilities(username);
            return  Message.ok("获取工程能力成功").data("projectAbilities", projectAbilities);
        }catch(final Throwable t){
            LOGGER.error("failed to get project ability for user {}", username, t);
            return  Message.error("获取工程能力失败");
        }
    }
}
