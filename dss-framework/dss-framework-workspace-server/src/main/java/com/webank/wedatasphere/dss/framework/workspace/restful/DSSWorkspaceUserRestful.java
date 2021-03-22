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

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSUser;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceUserVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.StaffInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceMenuService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.lang.StringUtils;
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
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;


/**
 * created by cooperyang on 2020/3/17
 * Description:
 */
@Component
@Path("/dss/framework/workspace")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSWorkspaceUserRestful {

    @Autowired
    private DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSWorkspaceMenuService dssWorkspaceMenuService;
    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;
    @Autowired
    private DSSWorkspaceUserService dssWorkspaceUserService;

    @GET
    @Path("getWorkspaceUsers")
    public Response getWorkspaceUsers(@Context HttpServletRequest request, @QueryParam(WORKSPACE_ID_STR) String workspaceId,
                                      @QueryParam("pageNow") Integer pageNow, @QueryParam("pageSize") Integer pageSize,
                                      @QueryParam("department") String department, @QueryParam("username") String username,
                                      @QueryParam("roleName") String roleName){
        //todo 获取工作空间中所有的用户以及他们的角色信息
        if(pageNow == null){
            pageNow = 1;
        }
        if(pageSize == null){
            //默认改成20
            pageSize = 20;
        }
        if(StringUtils.isNotEmpty(roleName)){
            //如果roleName不是空的话，就按照roleName来吧
            List<Long> totals = new ArrayList<>();
            List<DSSWorkspaceUserVO> workspaceUsers =
                    dssWorkspaceService.getWorkspaceUsersByRole(Integer.parseInt(workspaceId),roleName, totals, pageNow, pageSize);
            PageInfo<DSSWorkspaceUserVO> pageInfo = new PageInfo<>(workspaceUsers);
            List<DSSWorkspaceUserVO> list = pageInfo.getList();
            long total = pageInfo.getTotal();
            List<DSSWorkspaceRoleVO> dssRoles = workspaceDBHelper.getRoleVOs(Integer.parseInt(workspaceId));
            return Message.messageToResponse(Message.ok().data("roles", dssRoles).data("workspaceUsers", list).data("total", totals.get(0)));
        }else{
            List<Long> totals = new ArrayList<>();
            List<DSSWorkspaceUserVO> workspaceUsers =
                    dssWorkspaceService.getWorkspaceUsers(workspaceId, department, username, roleName, pageNow, pageSize, totals);
            PageInfo<DSSWorkspaceUserVO> pageInfo = new PageInfo<>(workspaceUsers);
            List<DSSWorkspaceUserVO> list = pageInfo.getList();
            long total = pageInfo.getTotal();
            List<DSSWorkspaceRoleVO> dssRoles = workspaceDBHelper.getRoleVOs(Integer.parseInt(workspaceId));
            return Message.messageToResponse(Message.ok().data("roles", dssRoles).data("workspaceUsers", list).data("total", totals.get(0)));
        }
    }

    @GET
    @Path("getAllWorkspaceUsers")
    public Response getAllWorkspaceUsers(@Context HttpServletRequest request, @QueryParam(WORKSPACE_ID_STR) int workspaceId ){
        String username = SecurityFilter.getLoginUsername(request);
        List<String> users = dssWorkspaceUserService.getAllWorkspaceUsers(workspaceId);
        return Message.messageToResponse(Message.ok().data("users", users));
    }

    @POST
    @Path("addWorkspaceUser")
    public Response addWorkspaceUser(@Context HttpServletRequest request, JsonNode jsonNode){
        //todo 工作空间添加用户
        String creator = SecurityFilter.getLoginUsername(request);
        List<Integer> roles = new ArrayList<>();
        if (jsonNode.get("roles").isArray()){
            for (JsonNode role : jsonNode.get("roles")){
                roles.add(role.getIntValue());
            }
        }
        int workspaceId = jsonNode.get("workspaceId").getIntValue();
        String userName = jsonNode.get("username").getTextValue();
        dssWorkspaceService.addWorkspaceUser(roles, workspaceId, userName, creator);
        return Message.messageToResponse(Message.ok());
    }

    @POST
    @Path("updateWorkspaceUser")
    public Response updateWorkspaceUser(@Context HttpServletRequest request, JsonNode jsonNode){
        String creator = SecurityFilter.getLoginUsername(request);
        List<Integer> roles = new ArrayList<>();
        if (jsonNode.get("roles").isArray()){
            for (JsonNode role : jsonNode.get("roles")){
                roles.add(role.getIntValue());
            }
        }
        int workspaceId = jsonNode.get("workspaceId").getIntValue();
        String userName = jsonNode.get("username").getTextValue();
        dssWorkspaceUserService.updateWorkspaceUser(roles, workspaceId, userName, creator);
        return Message.messageToResponse(Message.ok());
    }

    @POST
    @Path("deleteWorkspaceUser")
    public Response deleteWorkspaceUser(@Context HttpServletRequest request, JsonNode jsonNode){
        //todo 删除工作空间中的用户
        String userName = jsonNode.get("username").getTextValue();
        int workspaceId = jsonNode.get("workspaceId").getIntValue();
        dssWorkspaceUserService.deleteWorkspaceUser(userName,workspaceId);
        return Message.messageToResponse(Message.ok());
    }

    @GET
    @Path("listAllUsers")
    public Response listAllUsers(@Context HttpServletRequest request){
        List<StaffInfoVO> dssUsers = dssWorkspaceUserService.listAllDSSUsers();
        return Message.messageToResponse(Message.ok().data("users", dssUsers));
    }

    @GET
    @Path("getWorkspaceIdByUserName")
    public Response getWorkspaceIdByUserName(@Context HttpServletRequest request,@QueryParam("userName") String userName){
        String loginUserName = SecurityFilter.getLoginUsername(request);
        String queryUserName = userName;
        if(StringUtils.isEmpty(userName)){
            queryUserName = loginUserName;
        }
        List<Integer> userWorkspaceIds = dssWorkspaceUserService.getUserWorkspaceIds(queryUserName);
        String userWorkspaceIdStr = userWorkspaceIds.stream().map(x->x.toString()).collect(Collectors.joining(","));
        return Message.messageToResponse(Message.ok().data("userWorkspaceIds", userWorkspaceIdStr));
    }
}
