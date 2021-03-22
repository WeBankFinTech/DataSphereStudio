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

package com.webank.wedatasphere.dss.framework.project.restful;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProcessNode;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.utils.ApplicationArea;
import com.webank.wedatasphere.dss.framework.project.utils.RestfulUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/9/22
 * Description:
 */
@Component
@Path("/dss/framework/project")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSFrameworkProjectRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkProjectRestfulApi.class);
    @Autowired
    DSSFrameworkProjectService dssFrameworkProjectService;
    @Autowired
    private DSSProjectService projectService;
    @Autowired
    private AppConnService appConnService;

    /**
     * 获取所有工程或者单个工程
     *
     * @param request
     * @return
     */
    @GET
    @Path("getWorkSpaceStr")
    public Response getWorkSpaceStr(@Context HttpServletRequest request) {
        Workspace workspace = SSOHelper.getWorkspace(request);
        Message message = Message.ok("").data("workspaceStr", DSSCommonUtils.COMMON_GSON.toJson(workspace));
        return Message.messageToResponse(message);
    }

    /**
     * 获取所有工程或者单个工程
     *
     * @param request
     * @param projectRequest
     * @return
     */
    @POST
    @Path("getAllProjects")
    public Response getAllProjects(@Context HttpServletRequest request, @Valid ProjectQueryRequest projectRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        projectRequest.setUsername(username);
        List<ProjectResponse> dssProjectVos = projectService.getListByParam(projectRequest);
        Message message = Message.ok("获取工作空间的工程成功").data("projects", dssProjectVos);
        return Message.messageToResponse(message);
    }

    /**
     * 新建工程,通过和各个AppConn进行交互，将需要满足工程规范的所有的appconn进行创建工程
     */
    @POST
    @Path("createProject")
    public Response createProject(@Context HttpServletRequest request, @Valid ProjectCreateRequest projectCreateRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        try {
            DSSProjectVo dssProjectVo = dssFrameworkProjectService.createProject(projectCreateRequest, username, workspace);
            if (dssProjectVo != null) {
                return Message.messageToResponse(Message.ok("创建工程成功").data("project", dssProjectVo));
            } else {
                return Message.messageToResponse(Message.error("创建工程失败"));
            }
        } catch (final Throwable t) {
            LOGGER.error("failed to create project {} for user {}", projectCreateRequest.getName(), username, t);
            return RestfulUtils.dealError("创建工程失败:" + t.getMessage());
        }
    }

    /**
     * 编辑工程
     *
     * @param request
     * @param projectModifyRequest
     * @return
     */
    @POST
    @Path("modifyProject")
    public Response modifyProject(@Context HttpServletRequest request, @Valid ProjectModifyRequest projectModifyRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            dssFrameworkProjectService.modifyProject(projectModifyRequest, username);
            return Message.messageToResponse(Message.ok("修改工程成功"));
        } catch (Exception e) {
            LOGGER.error("Failed to modify project {} for user {}", projectModifyRequest.getName(), username, e);
            return RestfulUtils.dealError("修改工程失败:" + e.getMessage());
        }
    }

    /**
     * 删除工程
     *
     * @param request
     * @param projectDeleteRequest
     * @return
     */
    @POST
    @Path("deleteProject")
    public Response deleteProject(@Context HttpServletRequest request, @Valid ProjectDeleteRequest projectDeleteRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        try{
            projectService.deleteProject(username,projectDeleteRequest);
            return RestfulUtils.dealOk("删除工程成功");
        }catch(final Throwable t){
            LOGGER.error("Failed to delete {} for user {}", projectDeleteRequest, username);
            return RestfulUtils.dealError("删除工程失败");
        }
    }

    @POST
    @Path("/getAppConnProjectID")
    public Response getAppConnProjectID(@Context HttpServletRequest req, Map<String, Object> json) {
        Long projectID = (Long) json.get("projectID");
        String nodeType = json.get("nodeType").toString();
        AppConn appConn = appConnService.getAppConnByNodeType(nodeType);
        String label = ((Map<String, Object>) json.get("labels")).get("route").toString();
        try {
            Long appConnProjectID = projectService.getAppConnProjectId(projectID, appConn.getAppDesc().getAppName(), Lists.newArrayList(new DSSLabel(label)));
            return Message.messageToResponse(Message.ok().data("appConnProjectID",appConnProjectID));
        } catch (Exception e) {
            return RestfulUtils.dealError("获取AppConn Project ID失败");
        }
    }

    @GET
    @Path("getProcessNodes")
    public Response getProcessNodes(@Context HttpServletRequest request,
                                    @QueryParam("workspaceID") Long workspaceId,
                                    @QueryParam("projectId") Long projectId) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            List<ProcessNode> processNodes = dssFrameworkProjectService.getProcessNodes(username, workspaceId, projectId);
            return RestfulUtils.dealOk("获取开发流程节点成功", new Pair<>("processNodes", processNodes));
        } catch (Exception e) {
            LOGGER.error("Failed to get process nodes for user {}", username);
            return RestfulUtils.dealError("获取开发流程节点失败");
        }
    }

    @GET
    @Path("/listApplicationAreas")
    public Response listApplicationAreas(@Context HttpServletRequest req) {
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
        return Message.messageToResponse(Message.ok().data("applicationAreas", areas));
    }


    @GET
    @Path("/getProjectAbilities")
    public Response getProjectAbilities(@Context HttpServletRequest request){
        //为了获取到此环境的能力，导入 导出  发布等
        String username = SecurityFilter.getLoginUsername(request);
        try{
            List<String> projectAbilities = projectService.getProjectAbilities(username);
            return RestfulUtils.dealOk("获取工程能力成功", new Pair<>("projectAbilities", projectAbilities));
        }catch(final Throwable t){
            LOGGER.error("failed to get project ability for user {}", username, t);
            return RestfulUtils.dealError("获取工程能力失败");
        }
    }


}
