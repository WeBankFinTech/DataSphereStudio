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


import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSUserRoleComponentPriv;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.UpdateRoleComponentPrivRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.UpdateRoleMenuPrivRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomepageSettingVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspacePrivVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspacePrivService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.webank.wedatasphere.dss.framework.common.conf.TokenConf.HPMS_USER_TOKEN;
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
    @Autowired
    HttpServletRequest httpServletRequest;

    @RequestMapping(path ="getWorkspaceMenuPrivs", method = RequestMethod.GET)
    public Message getWorkspaceMenuPrivs(@RequestParam(WORKSPACE_ID_STR) String workspaceId){
        //todo 返回工作空间中角色对菜单的访问权限
        DSSWorkspacePrivVO workspaceMenuPrivs = dssWorkspaceService.getWorkspaceMenuPrivs(workspaceId);
        return Message.ok().data("workspaceMenuPrivs", workspaceMenuPrivs);
    }

    @RequestMapping(path ="getWorkspaceHomepageSettings", method = RequestMethod.GET)
    public Message getWorkspaceHomepageSettings(@RequestParam(WORKSPACE_ID_STR) int workspaceId){
        DSSWorkspaceHomepageSettingVO dssWorkspaceHomepageSettingVO = dssWorkspaceService.getWorkspaceHomepageSettings(workspaceId);
        return Message.ok().data("homepageSettings", dssWorkspaceHomepageSettingVO);
    }

    /**
     * 更新角色菜单权限
     * @param updateRoleMenuPrivRequest
     * @return
     */
    @RequestMapping(path = "updateRoleMenuPriv", method = RequestMethod.POST)
    public Message updateRoleMenuPriv(@RequestBody UpdateRoleMenuPrivRequest updateRoleMenuPrivRequest) throws Exception{
        String updater = SecurityFilter.getLoginUsername(httpServletRequest);
        int menuId = updateRoleMenuPrivRequest.getMenuId();
        int workspaceId = updateRoleMenuPrivRequest.getWorkspaceId();
        WorkspaceUtils.validateWorkspace(workspaceId, httpServletRequest);
        if (!dssWorkspaceService.checkAdminByWorkspace(updater, workspaceId)) {
            return Message.error("无权限进行该操作");
        }
        Map<String, Boolean> menuPrivs = updateRoleMenuPrivRequest.getMenuPrivs();
        List<Pair<Integer, Boolean>> pairs = new ArrayList<>();
        for (String key : menuPrivs.keySet()) {
            Integer roleId = dssWorkspacePrivService.getRoleId(workspaceId, key);
            if (roleId == null) {
                roleId = workspaceDBHelper.getRoleIdByName(key);
            }
            pairs.add(new Pair<Integer, Boolean>(roleId, menuPrivs.get(key)));
        }
        dssWorkspacePrivService.updateRoleMenuPriv(workspaceId, menuId, updater, pairs);
        String workspaceName= dssWorkspaceService.getWorkspaceName((long)workspaceId);
        AuditLogUtils.printLog(updater,workspaceId, workspaceName, TargetTypeEnum.WORKSPACE,workspaceId,workspaceName,
                OperateTypeEnum.UPDATE_ROLE_MENU, updateRoleMenuPrivRequest);
        return Message.ok("更新角色对于菜单的权限成功");
    }

    @RequestMapping(path ="updateRoleComponentPriv", method = RequestMethod.POST)
    public Message updateRoleComponentPriv(@RequestBody UpdateRoleComponentPrivRequest updateRoleComponentPrivRequest)throws DSSErrorException{
        //todo 更新工作空间中角色对于component的权限
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        int appconnId = updateRoleComponentPrivRequest.getComponentId();
        int workspaceId = updateRoleComponentPrivRequest.getWorkspaceId();
        WorkspaceUtils.validateWorkspace(workspaceId, httpServletRequest);
        if (!dssWorkspaceService.checkAdminByWorkspace(username, workspaceId)) {
            return Message.error("无权限进行该操作");
        }
        Map<String, Boolean> componentPrivs = updateRoleComponentPrivRequest.getComponentPrivs();
        List<Pair<Integer, Boolean>> pairs = new ArrayList<>();
        for (String key : componentPrivs.keySet()) {
            Integer roleId = dssWorkspacePrivService.getRoleId(workspaceId, key);
            if (roleId == null) {
                roleId = workspaceDBHelper.getRoleIdByName(key);
            }
            pairs.add(new Pair<Integer, Boolean>(roleId, componentPrivs.get(key)));
        }
        dssWorkspacePrivService.updateRoleComponentPriv(workspaceId, appconnId, username, pairs);
        String workspaceName = dssWorkspaceService.getWorkspaceName((long) workspaceId);
        AuditLogUtils.printLog(username, workspaceId, workspaceName, TargetTypeEnum.WORKSPACE,
                workspaceId, workspaceName, OperateTypeEnum.UPDATE_ROLE_MENU, updateRoleComponentPrivRequest);
        return Message.ok().data("updateRoleComponentPriv", "更新组件权限成功");
    }

    @RequestMapping(path ="updateRoleHomepage", method = RequestMethod.POST)
    public Message updateRoleHomepage(){
        return null;
    }

    @RequestMapping(path ="getAllUserPrivs", method = RequestMethod.GET)
    public Message getAllUserPrivs(@RequestParam Integer currentPage, @RequestParam Integer pageSize){
        if(currentPage<1 || pageSize<1){
            return Message.error("page param error（分页参数错误）");
        }
        if (pageSize > 100) {
            return Message.error("request parameter pageSize is too large and should not exceed 100（参数pageSize不应超过100）");
        }
        String token = ModuleUserUtils.getToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)) {
            if(!token.equals(HPMS_USER_TOKEN)){
                return Message.error("Token:" + token + " has no permission to get all userPrivs.");
            }
        }else {
            return Message.error("Token cannot be empty.");
        }
        PageInfo<DSSUserRoleComponentPriv> allUserPrivs = dssWorkspaceService.getAllUserPrivs(currentPage, pageSize);
        return Message.ok().data("totalCount",allUserPrivs.getTotal()).data("userPrivs",allUserPrivs.getList());
    }
}
