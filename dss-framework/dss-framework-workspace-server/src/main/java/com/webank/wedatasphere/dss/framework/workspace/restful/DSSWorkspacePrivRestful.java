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

package com.webank.wedatasphere.dss.framework.workspace.restful;


import com.webank.wedatasphere.dss.framework.workspace.bean.request.UpdateRoleComponentPrivRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.UpdateRoleMenuPrivRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomepageSettingVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspacePrivVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspacePrivService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;


@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
public class DSSWorkspacePrivRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspacePrivRestful.class);

    @Autowired
    DSSWorkspaceService dssWorkspaceService;
    @Autowired
    DSSWorkspacePrivService dssWorkspacePrivService;
    @Autowired
    WorkspaceDBHelper workspaceDBHelper;

    @RequestMapping(path ="getWorkspaceMenuPrivs", method = RequestMethod.GET)
    public Message getWorkspaceMenuPrivs(HttpServletRequest request, @RequestParam(WORKSPACE_ID_STR) String workspaceId){
        //todo 返回工作空间中角色对菜单的访问权限
        DSSWorkspacePrivVO workspaceMenuPrivs = dssWorkspaceService.getWorkspaceMenuPrivs(workspaceId);
        return Message.ok().data("workspaceMenuPrivs", workspaceMenuPrivs);
    }

    @RequestMapping(path ="getWorkspaceHomepageSettings", method = RequestMethod.GET)
    public Message getWorkspaceHomepageSettings(HttpServletRequest request, @RequestParam(WORKSPACE_ID_STR) int workspaceId){
        String username = SecurityFilter.getLoginUsername(request);
        DSSWorkspaceHomepageSettingVO dssWorkspaceHomepageSettingVO = dssWorkspaceService.getWorkspaceHomepageSettings(workspaceId);
        return Message.ok().data("homepageSettings", dssWorkspaceHomepageSettingVO);
    }

    @RequestMapping(path ="updateRoleMenuPriv", method = RequestMethod.POST)
    public Message updateRoleMenuPriv(HttpServletRequest request,@RequestBody UpdateRoleMenuPrivRequest updateRoleMenuPrivRequest){
        String updater = SecurityFilter.getLoginUsername(request);
        int menuId = updateRoleMenuPrivRequest.getMenuId();
        int workspaceId = updateRoleMenuPrivRequest.getWorkspaceId();
        Map<String, Boolean> menuPrivs = updateRoleMenuPrivRequest.getMenuPrivs();
        LOGGER.info("user {} begin to updateRoleMenuPriv, menuId:{}, menuPrivs:{}", updater, menuId, menuPrivs);
        List<Pair<Integer, Boolean>> pairs = new ArrayList<>();
        for(String key : menuPrivs.keySet()){
            Integer roleId = dssWorkspacePrivService.getRoleId(workspaceId, key);
            if (roleId == null) {
                roleId = workspaceDBHelper.getRoleIdByName(key);
            }
            pairs.add(new Pair<Integer, Boolean>(roleId, menuPrivs.get(key)));
        }
        dssWorkspacePrivService.updateRoleMenuPriv(workspaceId, menuId, updater, pairs);
        return Message.ok("更新角色对于菜单的权限成功");
    }

    @RequestMapping(path ="updateRoleComponentPriv", method = RequestMethod.POST)
    public Message updateRoleComponentPriv(HttpServletRequest request,@RequestBody UpdateRoleComponentPrivRequest updateRoleComponentPrivRequest){
        //todo 更新工作空间中角色对于component的权限
        String username = SecurityFilter.getLoginUsername(request);
        int appconnId = updateRoleComponentPrivRequest.getComponentId();
        int workspaceId = updateRoleComponentPrivRequest.getWorkspaceId();
        Map<String,Boolean> componentPrivs = updateRoleComponentPrivRequest.getComponentPrivs();
        LOGGER.info("user {} begin to updateRoleMenuPriv, appconnId:{}, componentPrivs:{}", username, appconnId, componentPrivs);
        List<Pair<Integer, Boolean>> pairs = new ArrayList<>();
        for (String key : componentPrivs.keySet()){
            Integer roleId = dssWorkspacePrivService.getRoleId(workspaceId, key);
            if (roleId == null) {
                roleId = workspaceDBHelper.getRoleIdByName(key);
            }
            pairs.add(new Pair<Integer, Boolean>(roleId, componentPrivs.get(key)));
        }
        dssWorkspacePrivService.updateRoleComponentPriv(workspaceId, appconnId, username, pairs);
        return Message.ok().data("updateRoleComponentPriv","更新组件权限成功");
    }

    @RequestMapping(path ="updateRoleHomepage", method = RequestMethod.POST)
    public Message updateRoleHomepage(HttpServletRequest request){
        return null;
    }
}
