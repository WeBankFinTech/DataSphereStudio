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

package com.webank.wedatasphere.dss.framework.workspace.restful;

import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageDemoMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageVideoVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.OnestopMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by schumiyi on 2020/6/19
 */
@Component
@Path("/dss/framework/workspace")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkspaceRestfulApi {
    @Autowired
    private DSSWorkspaceService dssWorkspaceService;

    @GET
    @Path("/workspaces")
    public Response getAllWorkspaces(@Context HttpServletRequest req) {
        // TODO: Order By time
        String username = SecurityFilter.getLoginUsername(req);
        List<DSSWorkspace> workspaces = dssWorkspaceService.getWorkspaces(username);
        return Message.messageToResponse(Message.ok().data("workspaces", workspaces));
    }

    @GET
    @Path("/workspaces/{id}")
    public Response getWorkspacesById(@Context HttpServletRequest req, @PathParam("id") Long id) {
        DSSWorkspace workspace = dssWorkspaceService.getWorkspacesById(id);
        return Message.messageToResponse(Message.ok().data("workspace", workspace));
    }

    @GET
    @Path("/workspaces/departments")
    public Response getAllWorkspaceDepartments(@Context HttpServletRequest req) {
        List<DepartmentVO> departments = dssWorkspaceService.getDepartments();
        return Message.messageToResponse(Message.ok().data("departments", departments));
    }

    @GET
    @Path("/workspaces/exists")
    public Response getUsernameExistence(@Context HttpServletRequest req, @QueryParam("name") String name) {
        boolean exists = dssWorkspaceService.existWorkspaceName(name);
        return Message.messageToResponse(Message.ok().data("workspaceNameExists", exists));
    }

    @POST
    @Path("/workspaces")
    public Response addWorkspace(@Context HttpServletRequest req, JsonNode json) throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(req);
        if (!dssWorkspaceService.checkAdmin(userName)){
            return Message.messageToResponse(Message.error("您好，您不是管理员,没有权限建立工作空间"));
        }
        String name = json.get("name").getTextValue();
        if (dssWorkspaceService.existWorkspaceName(name)) {
            return Message.messageToResponse(Message.error("工作空间名重复"));
        }
        String department = json.get("department").getTextValue();
        String label = json.get("label").getTextValue();
        String description = json.get("description").getTextValue();
        String productName = "DSS";
        int workspaceId = dssWorkspaceService.createWorkspace(name, label, userName, description, department, productName);
        return Message.messageToResponse(Message.ok().data("workspaceId", workspaceId));
    }

    @GET
    @Path("/workspaces/demos")
    public Response getAllHomepageDemos(@Context HttpServletRequest req) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        List<HomepageDemoMenuVo> homepageDemos = dssWorkspaceService.getHomepageDemos(isChinese);
        return Message.messageToResponse(Message.ok().data("demos", homepageDemos));
    }

    @GET
    @Path("/workspaces/videos")
    public Response getAllVideos(@Context HttpServletRequest req) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        List<HomepageVideoVo> homepageVideos = dssWorkspaceService.getHomepageVideos(isChinese);
        return Message.messageToResponse(Message.ok().data("videos", homepageVideos));
    }

    @GET
    @Path("workspaces/{workspaceId}/managements")
    public Response getWorkspaceManagements(@Context HttpServletRequest req, @PathParam("workspaceId") Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<OnestopMenuVo> managements = dssWorkspaceService.getWorkspaceManagements(workspaceId, username, isChinese);
        return Message.messageToResponse(Message.ok().data("managements", managements));
    }

    @GET
    @Path("workspaces/{workspaceId}/applications")
    public Response getWorkspaceApplications(@Context HttpServletRequest req, @PathParam("workspaceId") Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<OnestopMenuVo> applications = dssWorkspaceService.getWorkspaceApplications(workspaceId, username, isChinese);
        return Message.messageToResponse(Message.ok().data("applications", applications));
    }

    @GET
    @Path("/workspaces/{workspaceId}/favorites")
    public Response getWorkspaceFavorites(@Context HttpServletRequest req, @PathParam("workspaceId") Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<WorkspaceFavoriteVo> favorites = dssWorkspaceService.getWorkspaceFavorites(workspaceId, username, isChinese);
        Set<WorkspaceFavoriteVo> favoriteVos = new HashSet<>(favorites);
        return Message.messageToResponse(Message.ok().data("favorites", favoriteVos));
    }

    /**
     * 应用加入收藏，返回收藏后id
     *
     * @param req
     * @param json
     * @return
     */
    @POST
    @Path("/workspaces/{workspaceId}/favorites")
    public Response addFavorite(@Context HttpServletRequest req, @PathParam("workspaceId") Long workspaceId, JsonNode json) {
        String username = SecurityFilter.getLoginUsername(req);
        Long menuApplicationId = json.get("menuApplicationId").getLongValue();
        Long favoriteId = dssWorkspaceService.addFavorite(username, workspaceId, menuApplicationId);
        return Message.messageToResponse(Message.ok().data("favoriteId", favoriteId));
    }

    @DELETE
    @Path("/workspaces/{workspaceId}/favorites/{favouritesId}")
    public Response deleteFavorite(@Context HttpServletRequest req, @PathParam("workspaceId") Long workspaceId, @PathParam("favouritesId") Long favouritesId) {
        String username = SecurityFilter.getLoginUsername(req);
        Long favoriteId = dssWorkspaceService.deleteFavorite(username, favouritesId);
        return Message.messageToResponse(Message.ok().data("favoriteId", favoriteId));
    }

}
