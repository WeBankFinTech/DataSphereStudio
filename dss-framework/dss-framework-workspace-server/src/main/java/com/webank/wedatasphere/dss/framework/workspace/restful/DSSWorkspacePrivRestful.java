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


import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomepageSettingVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspacePrivVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspacePrivService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.math3.util.Pair;
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
 * created by cooperyang on 2020/3/17
 * Description:
 */
@Component
@Path("/dss/framework/workspace")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSWorkspacePrivRestful {

    @Autowired
    DSSWorkspaceService dssWorkspaceService;
    @Autowired
    DSSWorkspacePrivService dssWorkspacePrivService;
    @Autowired
    WorkspaceDBHelper workspaceDBHelper;

    @GET
    @Path("getWorkspaceMenuPrivs")
    public Response getWorkspaceMenuPrivs(@Context HttpServletRequest request, @QueryParam(WORKSPACE_ID_STR) String workspaceId){
        //todo 返回工作空间中角色对菜单的访问权限
        DSSWorkspacePrivVO workspaceMenuPrivs = dssWorkspaceService.getWorkspaceMenuPrivs(workspaceId);
        return Message.messageToResponse(Message.ok().data("workspaceMenuPrivs", workspaceMenuPrivs));
    }

    @GET
    @Path("getWorkspaceComponentPrivs")
    public Response getWorkspaceComponentPrivs(@Context HttpServletRequest request, @QueryParam(WORKSPACE_ID_STR) String workspaceId){
        return Message.messageToResponse(Message.ok("接口废弃"));
    }

    @GET
    @Path("getWorkspaceHomepageSettings")
    public Response getWorkspaceHomepageSettings(@Context HttpServletRequest request, @QueryParam(WORKSPACE_ID_STR) int workspaceId){
        String username = SecurityFilter.getLoginUsername(request);
        DSSWorkspaceHomepageSettingVO dssWorkspaceHomepageSettingVO = dssWorkspaceService.getWorkspaceHomepageSettings(workspaceId);
        return Message.messageToResponse(Message.ok().data("homepageSettings", dssWorkspaceHomepageSettingVO));
    }

    @POST
    @Path("updateRoleMenuPriv")
    public Response updateRoleMenuPriv(@Context HttpServletRequest request, JsonNode jsonNode){
        String updater = SecurityFilter.getLoginUsername(request);
        int menuId = jsonNode.get("menuId").getIntValue();
        int workspaceId = jsonNode.get("workspaceId").getIntValue();
        JsonNode menuPrivs = jsonNode.get("menuPrivs");
        List<Pair<Integer, Boolean>> pairs = new ArrayList<>();
        menuPrivs.getFields().forEachRemaining(field -> {
            Integer roleId = dssWorkspacePrivService.getRoleId(workspaceId, field.getKey());
            if (roleId == null) {
                roleId = workspaceDBHelper.getRoleIdByName(field.getKey());
            }
            pairs.add(new Pair<Integer, Boolean>(roleId, field.getValue().getBooleanValue()));
        });
        dssWorkspacePrivService.updateRoleMenuPriv(workspaceId, menuId, updater, pairs);
        return Message.messageToResponse(Message.ok("更新角色对于菜单的权限成功"));
    }

    @POST
    @Path("updateRoleComponentPriv")
    public Response updateRoleComponentPriv(@Context HttpServletRequest request, JsonNode jsonNode){
        //todo 更新工作空间中角色对于component的权限
        String username = SecurityFilter.getLoginUsername(request);
        int menuId = jsonNode.get("componentId").getIntValue();
        int workspaceId = jsonNode.get("workspaceId").getIntValue();
        JsonNode componentPrivs = jsonNode.get("componentPrivs");
        List<Pair<Integer, Boolean>> pairs = new ArrayList<>();
        componentPrivs.getFields().forEachRemaining(field -> {
            Integer roleId = dssWorkspacePrivService.getRoleId(workspaceId, field.getKey());
            if (roleId == null) {
                roleId = workspaceDBHelper.getRoleIdByName(field.getKey());
            }
            pairs.add(new Pair<Integer, Boolean>(roleId, field.getValue().getBooleanValue()));
        });
        dssWorkspacePrivService.updateRoleComponentPriv(workspaceId, menuId, username, pairs);
        return Message.messageToResponse(Message.ok().data("updateRoleComponentPriv","更新组件权限成功"));
    }

    @POST
    @Path("updateRoleHomepage")
    public Response updateRoleHomepage(@Context HttpServletRequest request, JsonNode jsonNode){
        return null;
    }
}
