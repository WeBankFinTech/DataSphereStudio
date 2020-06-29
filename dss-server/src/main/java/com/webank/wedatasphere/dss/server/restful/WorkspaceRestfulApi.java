package com.webank.wedatasphere.dss.server.restful;

import com.webank.wedatasphere.dss.server.dto.response.HomepageDemoMenuVo;
import com.webank.wedatasphere.dss.server.dto.response.HomepageVideoVo;
import com.webank.wedatasphere.dss.server.dto.response.OnestopMenuVo;
import com.webank.wedatasphere.dss.server.entity.DWSWorkspace;
import com.webank.wedatasphere.dss.server.dto.response.WorkspaceDepartmentVo;
import com.webank.wedatasphere.dss.server.service.DWSWorkspaceService;
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
import java.util.List;

/**
 * Created by schumiyi on 2020/6/19
 */
@Component
@Path("/dss")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkspaceRestfulApi {

    @Autowired
    private DWSWorkspaceService dwsWorkspaceService;


    @GET
    @Path("/workspaces")
    public Response getAllWorkspaces(@Context HttpServletRequest req) {
        // TODO: Order By time
        List<DWSWorkspace> workspaces = dwsWorkspaceService.getWorkspaces();
        return Message.messageToResponse(Message.ok().data("workspaces", workspaces));
    }

    @GET
    @Path("/workspaces/departments")
    public Response getAllWorkspaceDepartments(@Context HttpServletRequest req) {
        List<WorkspaceDepartmentVo> departments = dwsWorkspaceService.getWorkSpaceDepartments();
        return Message.messageToResponse(Message.ok().data("departments", departments));
    }

    @GET
    @Path("/workspaces/exists")
    public Response getUsernameExistence(@Context HttpServletRequest req, @QueryParam("name") String name) {
        boolean exists = dwsWorkspaceService.existWorkspaceName(name);
        return Message.messageToResponse(Message.ok().data("workspaceNameExists", exists));
    }

    @POST
    @Path("/workspaces")
    public Response addWorkspace(@Context HttpServletRequest req, JsonNode json) {
        String userName = SecurityFilter.getLoginUsername(req);
        String name = json.get("name").getTextValue();
        if (dwsWorkspaceService.existWorkspaceName(name)) {
            return Message.messageToResponse(Message.error("工作空间名重复"));
        }
        String department = json.get("department").getTextValue();
        String label = json.get("label").getTextValue();
        String description = json.get("description").getTextValue();
        Long workspaceId = dwsWorkspaceService.addWorkspace(userName, name, department, label, description);
        return Message.messageToResponse(Message.ok().data("workspaceId", workspaceId));
    }

    @GET
    @Path("/workspaces/demos")
    public Response getAllHomepageDemos(@Context HttpServletRequest req) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        List<HomepageDemoMenuVo> homepageDemos = dwsWorkspaceService.getHomepageDemos(isChinese);
        return Message.messageToResponse(Message.ok().data("demos", homepageDemos));
    }

    @GET
    @Path("/workspaces/videos")
    public Response getAllVideos(@Context HttpServletRequest req) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        List<HomepageVideoVo> homepageVideos = dwsWorkspaceService.getHomepageVideos(isChinese);
        return Message.messageToResponse(Message.ok().data("videos", homepageVideos));
    }

    @GET
    @Path("workspaces/{workspaceId}/managements")
    public Response getWorkspaceManagements(@Context HttpServletRequest req, @PathParam("workspaceId")Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);

        List<OnestopMenuVo> managements = dwsWorkspaceService.getWorkspaceManagements(workspaceId, username, isChinese);
        return Message.messageToResponse(Message.ok().data("managements", managements));
    }

    @GET
    @Path("workspaces/{workspaceId}/applications")
    public Response getWorkspaceApplications(@Context HttpServletRequest req, @PathParam("workspaceId")Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<OnestopMenuVo> applications = dwsWorkspaceService.getWorkspaceApplications(workspaceId, username, isChinese);
        return Message.messageToResponse(Message.ok().data("applications", applications));
    }
}
