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

import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.DeleteWorkspaceUserRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.RevokeUserRole;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.UpdateWorkspaceUserRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceUserVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceUsersVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.StaffInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleCheckService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.common.conf.TokenConf.HPMS_USER_TOKEN;
import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;


@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
public class DSSWorkspaceUserRestful {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceUserRestful.class);

    @Autowired
    private DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;
    @Autowired
    private DssAdminUserService dssUserService;
    @Autowired
    private DSSWorkspaceUserService dssWorkspaceUserService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private DSSWorkspaceRoleCheckService roleCheckService;

    @RequestMapping(path = "getWorkspaceUsers", method = RequestMethod.GET)
    public Message getWorkspaceUsers( @RequestParam(WORKSPACE_ID_STR) String workspaceId,
                                     @RequestParam(required = false, name = "pageNow") Integer pageNow, @RequestParam(required = false, name = "pageSize") Integer pageSize,
                                     @RequestParam(required = false, name = "department") String department, @RequestParam(required = false, name = "userName") String username,
                                     @RequestParam(required = false, name = "roleName") String roleName) {
        //todo 获取工作空间中所有的用户以及他们的角色信息
        if(pageNow==null&&pageSize==null){
            pageSize=Integer.MAX_VALUE;
            pageNow=1;
        }
        if (pageNow == null) {
            pageNow = 1;
        }
        if (pageSize == null) {
            //默认改成20
            pageSize = 20;
        }
        List<Long> totals = new ArrayList<>();
        List<DSSWorkspaceUserVO> workspaceUsers =
                dssWorkspaceService.getWorkspaceUsers(workspaceId, department, username, roleName, pageNow, pageSize, totals);
        List<DSSWorkspaceRoleVO> dssRoles = workspaceDBHelper.getRoleVOs(Integer.parseInt(workspaceId));
        return Message.ok().data("roles", dssRoles).data("workspaceUsers", workspaceUsers).data("total", totals.get(0));
    }

    @RequestMapping(path = "getAllWorkspaceUsers", method = RequestMethod.GET)
    public Message getAllWorkspaceUsers() {
        DSSWorkspaceUsersVo dssWorkspaceUsersVo = new DSSWorkspaceUsersVo();
        // workspaceId改为从cookie取
        int workspaceId = (int) SSOHelper.getWorkspace(httpServletRequest).getWorkspaceId();
        dssWorkspaceUsersVo.setAccessUsers(dssWorkspaceUserService.getAllWorkspaceUsers(workspaceId));
//        dssWorkspaceUsersVo.setEditUsers(dssWorkspaceUserService.getWorkspaceEditUsers(workspaceId));
//        dssWorkspaceUsersVo.setReleaseUsers(dssWorkspaceUserService.getWorkspaceReleaseUsers(workspaceId));
        dssWorkspaceUsersVo.setEditUsers(dssWorkspaceUserService.getAllWorkspaceUsers(workspaceId));
        dssWorkspaceUsersVo.setReleaseUsers(dssWorkspaceUserService.getAllWorkspaceUsers(workspaceId));
        return Message.ok().data("users", dssWorkspaceUsersVo);
    }


    @RequestMapping(path = "existUserInWorkspace", method = RequestMethod.GET)
    public Message existUserInWorkspace(@RequestParam(WORKSPACE_ID_STR) int workspaceId, @RequestParam("queryUserName") String queryUserName) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        List<String> users = dssWorkspaceUserService.getAllWorkspaceUsers(workspaceId);
        boolean existFlag = users.stream().anyMatch(user -> user.equalsIgnoreCase(queryUserName));
        LOGGER.info("Check exist user result:" + existFlag + ", query user  is " + queryUserName + ",workSpace id is " + workspaceId);
        return Message.ok().data("existFlag", existFlag);
    }


    @RequestMapping(path = "addWorkspaceUser", method = RequestMethod.POST)
    public Message addWorkspaceUser(@RequestBody UpdateWorkspaceUserRequest updateWorkspaceUserRequest) {
        //todo 工作空间添加用户
        String creator = SecurityFilter.getLoginUsername(httpServletRequest);
        List<Integer> roles = updateWorkspaceUserRequest.getRoles();
        Workspace workspace;
        //兼容外部系统通过接口调用场景，cookie未设置workspaceName
        if (Arrays.stream(httpServletRequest.getCookies()).noneMatch(l -> l.getName().equals("workspaceName"))) {
            workspace = new Workspace();
            try {
                workspace = SSOHelper.getWorkspace(httpServletRequest);
            } catch (AppStandardWarnException appStandardWarnException) {
                workspace.setWorkspaceId(updateWorkspaceUserRequest.getWorkspaceId());
                workspace.setWorkspaceName(String.valueOf(updateWorkspaceUserRequest.getWorkspaceId()));
            }
        } else {
            workspace = SSOHelper.getWorkspace(httpServletRequest);
        }
        int workspaceId = updateWorkspaceUserRequest.getWorkspaceId();
        if (workspace.getWorkspaceId() != workspaceId) {
            return Message.error("cookie 中的 workspaceId 与请求添加用户的 workspace 不同！");
        }
        String userName = updateWorkspaceUserRequest.getUserName();
        String userId = updateWorkspaceUserRequest.getUserId();
        Long count = dssWorkspaceUserService.getCountByUsername(userName, workspaceId);
        if (count != null && count > 0) {
            return Message.error("用户已经存在该工作空间，不需要重复添加！");
        }
        if (!roleCheckService.checkRolesOperation(workspaceId, creator, userName, roles)) {
            return Message.error("无权限进行该操作");
        }
        dssUserService.insertOrUpdateUser(userName, workspace);
        dssWorkspaceUserService.addWorkspaceUser(roles, workspace.getWorkspaceId(), userName, creator, userId);
        AuditLogUtils.printLog(userName,workspaceId, workspace.getWorkspaceName(), TargetTypeEnum.WORKSPACE,workspaceId,
                workspace.getWorkspaceName(), OperateTypeEnum.ADD_USERS,updateWorkspaceUserRequest);
        return Message.ok();
    }

    @RequestMapping(path = "updateWorkspaceUser", method = RequestMethod.POST)
    public Message updateWorkspaceUser( @RequestBody UpdateWorkspaceUserRequest updateWorkspaceUserRequest) {
        String creator = SecurityFilter.getLoginUsername(httpServletRequest);
        List<Integer> roles = updateWorkspaceUserRequest.getRoles();
        int workspaceId = updateWorkspaceUserRequest.getWorkspaceId();
        String workspaceName= dssWorkspaceService.getWorkspaceName((long)workspaceId);
        String userName = updateWorkspaceUserRequest.getUserName();
        if (!roleCheckService.checkRolesOperation(workspaceId, creator, userName, roles)) {
            return Message.error("无权限进行该操作");
        }
        dssWorkspaceUserService.updateWorkspaceUser(roles, workspaceId, userName, creator);
        AuditLogUtils.printLog(userName,workspaceId, workspaceName, TargetTypeEnum.WORKSPACE,workspaceId,
                workspaceName, OperateTypeEnum.UPDATE_USERS,updateWorkspaceUserRequest);
        return Message.ok();
    }

    @RequestMapping(path = "deleteWorkspaceUser", method = RequestMethod.POST)
    public Message deleteWorkspaceUser( @RequestBody DeleteWorkspaceUserRequest deleteWorkspaceUserRequest) {
        //todo 删除工作空间中的用户
        String userName = deleteWorkspaceUserRequest.getUserName();
        int workspaceId = deleteWorkspaceUserRequest.getWorkspaceId();
        String workspaceName= dssWorkspaceService.getWorkspaceName((long)workspaceId);
        String creator = SecurityFilter.getLoginUsername(httpServletRequest);
        if (!roleCheckService.checkRolesOperation(workspaceId, creator, userName, new ArrayList<>())) {
            return Message.error("无权限进行该操作");
        }
        dssWorkspaceUserService.deleteWorkspaceUser(userName, workspaceId);
        AuditLogUtils.printLog(userName,workspaceId, workspaceName, TargetTypeEnum.WORKSPACE,workspaceId,
                workspaceName, OperateTypeEnum.UPDATE_USERS,deleteWorkspaceUserRequest);
        return Message.ok();
    }

    @RequestMapping(path = "listAllUsers", method = RequestMethod.GET)
    public Message listAllUsers() {
        List<StaffInfoVO> dssUsers = dssWorkspaceUserService.listAllDSSUsers();
        return Message.ok().data("users", dssUsers);
    }

    @RequestMapping(path = "getWorkspaceIdByUserName", method = RequestMethod.GET)
    public Message getWorkspaceIdByUserName( @RequestParam(required = false, name = "userName") String userName) {
        String loginUserName = SecurityFilter.getLoginUsername(httpServletRequest);
        String queryUserName = userName;
        if (StringUtils.isEmpty(userName)) {
            queryUserName = loginUserName;
        }
        List<Integer> userWorkspaceIds = dssWorkspaceUserService.getUserWorkspaceIds(queryUserName);
        String userWorkspaceIdStr = userWorkspaceIds.stream().map(x -> x.toString()).collect(Collectors.joining(","));
        return Message.ok().data("userWorkspaceIds", userWorkspaceIdStr);
    }

    @RequestMapping(path = "getUserRole", method = RequestMethod.GET)
    public Message getWorkspaceUserRole(@RequestParam(name = "userName") String username) {
        String token = ModuleUserUtils.getToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)) {
            if(!token.equals(HPMS_USER_TOKEN)){
                return Message.error("Token:" + token + " has no permission to get user info.");
            }
        }else {
            return Message.error("User:" + username + " has no permission to get user info.");
        }
        List<Map<String,Object>> userRoles = dssWorkspaceUserService.getUserRoleByUserName(username);
        return Message.ok().data("userName", username).data("roleInfo", userRoles);
    }

    @RequestMapping(path = "/clearUser", method = RequestMethod.GET)
    public Message clearUser(@RequestParam("userName") String userName) {
        String token = ModuleUserUtils.getToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)) {
            if(!token.equals(HPMS_USER_TOKEN)){
                return Message.error("Token:" + token + " has no permission to clear user.");
            }
        }else {
            return Message.error("User:" + userName + " has no permission to clear user.");
        }
        dssWorkspaceUserService.clearUserByUserName(userName);
        AuditLogUtils.printLog(userName,null, null, TargetTypeEnum.WORKSPACE_ROLE,null,
                "clearUser", OperateTypeEnum.DELETE,null);
        return Message.ok("清理成功");

    }

    @PostMapping(path = "/revokeUserRole")
    public Message revokeUserRole(@RequestBody RevokeUserRole revokeUserRole) {
        String userName = revokeUserRole.getUserName();
        Integer[] workspaceIds = revokeUserRole.getWorkspaceIds();
        Integer[] roleIds = revokeUserRole.getRoleIds();
        String token = ModuleUserUtils.getToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)) {
            if(!token.equals(HPMS_USER_TOKEN)){
                return Message.error("Token:" + token + " has no permission to revoke userRole.");
            }
        }else {
            return Message.error("User:" + userName + " has no permission to clear user.");
        }
        dssWorkspaceUserService.revokeUserRoles(userName, workspaceIds, roleIds);
        AuditLogUtils.printLog(userName,null, null, TargetTypeEnum.WORKSPACE_ROLE,null,
                "revokeUserRole", OperateTypeEnum.DELETE,revokeUserRole);
        return Message.ok("回收成功");

    }
}
