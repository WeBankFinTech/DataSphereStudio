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
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomePageVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceOverviewVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceMenuService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
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
import java.util.ArrayList;
import java.util.List;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;



/**
 * created by cooperyang on 2020/3/4
 * Description:
 */
@Component
@Path("/dss/framework/workspace")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSWorkspaceRestful {
    @Autowired
    private DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSWorkspaceMenuService dssWorkspaceMenuService;
    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;

    @POST
    @Path("/createWorkspace")
    public Response createWorkspace(@Context HttpServletRequest request, JsonNode node)throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(request);
        if (!dssWorkspaceService.checkAdmin(userName)){
            return Message.messageToResponse(Message.error("您好，您不是管理员,没有权限建立工作空间"));
        }
        String workSpaceName = node.get("workspaceName").getTextValue();
        String department = node.get("department").getTextValue();
        String description = node.get("description").getTextValue();
        String stringTags = node.get("tags").getTextValue();
        String productName = node.get("productName").getTextValue();
        int workspaceId = dssWorkspaceService.createWorkspace(workSpaceName, stringTags, userName, description, department, productName);
        return Message.messageToResponse(Message.ok().data("workspaceId", workspaceId).data("workspaceName",workSpaceName));
    }

    @GET
    @Path("listDepartments")
    public Response listDepartments(@Context HttpServletRequest request, @QueryParam(WORKSPACE_ID_STR) String workspaceId){
        //todo 要从um中获取
        List<DepartmentVO> departments  = dssWorkspaceService.getDepartments();
        return Message.messageToResponse(Message.ok().data("departments", departments));
    }

    @GET
    @Path("getWorkspaces")
    public Response getWorkspaces(@Context HttpServletRequest request){
        String username = SecurityFilter.getLoginUsername(request);
        List<DSSWorkspace> workspaces = dssWorkspaceService.getWorkspaces(username);
        List<DSSWorkspaceVO> dssWorkspaceVOS = new ArrayList<>();
        for (DSSWorkspace workspace:workspaces ){
            String name = workspace.getName();
            int id = workspace.getId();
            String labels = workspace.getLabel();
            DSSWorkspaceVO dssWorkspaceVO = new DSSWorkspaceVO();
            dssWorkspaceVO.setId(id);
            dssWorkspaceVO.setName(name);
            dssWorkspaceVO.setTags(labels);
            dssWorkspaceVO.setDepartment(workspace.getDepartment());
            dssWorkspaceVO.setDescription(workspace.getDescription());
            dssWorkspaceVO.setProduct(workspace.getProduct());
            dssWorkspaceVOS.add(dssWorkspaceVO);
        }
        //todo 获取用户所有所有能够访问的工作空间
        return Message.messageToResponse(Message.ok().data("workspaces", dssWorkspaceVOS));
    }

    @GET
    @Path("getWorkspaceHomePage")
    public Response getWorkspaceHomePage(@Context HttpServletRequest request, @QueryParam("micro_module") String moduleName){
        //如果用户的工作空间大于两个，那么就直接返回/workspace页面
        String username = SecurityFilter.getLoginUsername(request);
        DSSWorkspaceHomePageVO dssWorkspaceHomePageVO = dssWorkspaceService.getWorkspaceHomePage(username,moduleName);
        return Message.messageToResponse(Message.ok().data("workspaceHomePage", dssWorkspaceHomePageVO));
    }

    @GET
    @Path("getOverview")
    public Response getOverview(@Context HttpServletRequest request, @QueryParam(WORKSPACE_ID_STR) int workspaceId){
       String username = SecurityFilter.getLoginUsername(request);
       String language = request.getHeader("Content-language");
       boolean isEnglish = "en".equals(language);
       DSSWorkspaceOverviewVO dssWorkspaceOverviewVO = dssWorkspaceService.getOverview(username, workspaceId, isEnglish);
       return Message.messageToResponse(Message.ok().data("overview", dssWorkspaceOverviewVO));
    }

    @GET
    @Path("getWorkflowAllVersions")
    public Response getWorkflowAllVersions(@Context HttpServletRequest request,
                                           @QueryParam(WORKSPACE_ID_STR) String workspaceId,
                                           @QueryParam("workflowId") String workflowId){
        //todo 返回的
        return null;
    }

    @POST
    @Path("rollbackWorkflowVersion")
    public Response rollbackWorkflowVersion(@Context HttpServletRequest request, JsonNode jsonNode){
        //todo 回退工作流的版本
        return null;
    }

    @GET
    @Path("refreshCache")
    public Response refreshCache(@Context HttpServletRequest request){
        workspaceDBHelper.retrieveFromDB();
        return Message.messageToResponse(Message.ok("refresh ok"));
    }

    @GET
    @Path("getAllPublicTables")
    public Response getAllPublicTables(@Context HttpServletRequest request, @QueryParam(WORKSPACE_ID_STR) String workspaceId){
        //todo 返回工作空间中的所有的公共表
        //dss_workspace_public_table,DSSPublicTableVO
        return null;
    }
}

