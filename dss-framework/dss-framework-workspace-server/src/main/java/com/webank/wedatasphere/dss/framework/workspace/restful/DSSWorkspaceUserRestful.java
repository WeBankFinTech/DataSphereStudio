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

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.common.StaffInfo;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSUserDefaultWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceStarRocksCluster;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.*;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.*;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleCheckService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.dss.common.StaffInfoGetter;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
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
    @Autowired
    private StaffInfoGetter staffInfoGetter;

    @RequestMapping(path = "getWorkspaceUsers", method = RequestMethod.GET)
    public Message getWorkspaceUsers(@RequestParam(WORKSPACE_ID_STR) String workspaceId,
                                     @RequestParam(required = false, name = "pageNow") Integer pageNow, @RequestParam(required = false, name = "pageSize") Integer pageSize,
                                     @RequestParam(required = false, name = "department") String department, @RequestParam(required = false, name = "userName") String username,
                                     @RequestParam(required = false, name = "roleName") String roleName) {
        //todo 获取工作空间中所有的用户以及他们的角色信息
        if (pageNow == null && pageSize == null) {
            pageSize = Integer.MAX_VALUE;
            pageNow = 1;
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
    public Message getAllWorkspaceUsers(@RequestParam(value = WORKSPACE_ID_STR, required = false) Integer workspaceId) {
        DSSWorkspaceUsersVo dssWorkspaceUsersVo = new DSSWorkspaceUsersVo();
        if (workspaceId == null) {
            // workspaceId改为从cookie取
            workspaceId = (int) SSOHelper.getWorkspace(httpServletRequest).getWorkspaceId();
        }
        List<String> users = dssWorkspaceUserService.getAllWorkspaceUsers(workspaceId);
        dssWorkspaceUsersVo.setAccessUsers(users);
//        dssWorkspaceUsersVo.setEditUsers(dssWorkspaceUserService.getWorkspaceEditUsers(workspaceId));
//        dssWorkspaceUsersVo.setReleaseUsers(dssWorkspaceUserService.getWorkspaceReleaseUsers(workspaceId));
        dssWorkspaceUsersVo.setEditUsers(users);
        dssWorkspaceUsersVo.setReleaseUsers(users);
        dssWorkspaceUsersVo.setCreateUsers(users);
        return Message.ok().data("users", dssWorkspaceUsersVo);
    }

    @RequestMapping(path = "getAllWorkspaceUsersWithDepartment", method = RequestMethod.GET)
    public Message getAllWorkspaceUsersWithDepartment() {
        DSSWorkspaceUsersDepartmentVo dSSWorkspaceUsersDepartmentVo = new DSSWorkspaceUsersDepartmentVo();
        // workspaceId改为从cookie取
        int workspaceId = (int) SSOHelper.getWorkspace(httpServletRequest).getWorkspaceId();
        dSSWorkspaceUsersDepartmentVo.setAccessUsers(dssWorkspaceUserService.getAllWorkspaceUsersDepartment(workspaceId));
//        dssWorkspaceUsersVo.setEditUsers(dssWorkspaceUserService.getWorkspaceEditUsers(workspaceId));
//        dssWorkspaceUsersVo.setReleaseUsers(dssWorkspaceUserService.getWorkspaceReleaseUsers(workspaceId));
        dSSWorkspaceUsersDepartmentVo.setEditUsers(dssWorkspaceUserService.getAllWorkspaceUsersDepartment(workspaceId));
        dSSWorkspaceUsersDepartmentVo.setReleaseUsers(dssWorkspaceUserService.getAllWorkspaceUsersDepartment(workspaceId));
        return Message.ok().data("users", dSSWorkspaceUsersDepartmentVo);
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
        dssUserService.insertIfNotExist(userName, workspace);
        dssWorkspaceUserService.addWorkspaceUser(roles, workspace.getWorkspaceId(), userName, creator, userId);
        AuditLogUtils.printLog(userName, workspaceId, workspace.getWorkspaceName(), TargetTypeEnum.WORKSPACE, workspaceId,
                workspace.getWorkspaceName(), OperateTypeEnum.ADD_USERS, updateWorkspaceUserRequest);
        return Message.ok();
    }

    @RequestMapping(path = "updateWorkspaceUser", method = RequestMethod.POST)
    public Message updateWorkspaceUser(@RequestBody UpdateWorkspaceUserRequest updateWorkspaceUserRequest) {
        String creator = SecurityFilter.getLoginUsername(httpServletRequest);
        List<Integer> roles = updateWorkspaceUserRequest.getRoles();
        int workspaceId = updateWorkspaceUserRequest.getWorkspaceId();
        String workspaceName = dssWorkspaceService.getWorkspaceName((long) workspaceId);
        String userName = updateWorkspaceUserRequest.getUserName();
        if (!roleCheckService.checkRolesOperation(workspaceId, creator, userName, roles)) {
            return Message.error("无权限进行该操作");
        }
        dssWorkspaceUserService.updateWorkspaceUser(roles, workspaceId, userName, creator);
        AuditLogUtils.printLog(userName, workspaceId, workspaceName, TargetTypeEnum.WORKSPACE, workspaceId,
                workspaceName, OperateTypeEnum.UPDATE_USERS, updateWorkspaceUserRequest);
        return Message.ok();
    }

    @RequestMapping(path = "deleteWorkspaceUser", method = RequestMethod.POST)
    public Message deleteWorkspaceUser(@RequestBody DeleteWorkspaceUserRequest deleteWorkspaceUserRequest) {
        //todo 删除工作空间中的用户
        String userName = deleteWorkspaceUserRequest.getUserName();
        int workspaceId = deleteWorkspaceUserRequest.getWorkspaceId();
        String workspaceName = dssWorkspaceService.getWorkspaceName((long) workspaceId);
        String creator = SecurityFilter.getLoginUsername(httpServletRequest);
        if (!roleCheckService.checkRolesOperation(workspaceId, creator, userName, new ArrayList<>())) {
            return Message.error("无权限进行该操作");
        }
        dssWorkspaceUserService.deleteWorkspaceUser(userName, workspaceId);
        AuditLogUtils.printLog(userName, workspaceId, workspaceName, TargetTypeEnum.WORKSPACE, workspaceId,
                workspaceName, OperateTypeEnum.UPDATE_USERS, deleteWorkspaceUserRequest);
        return Message.ok();
    }

    @RequestMapping(path = "listAllUsers", method = RequestMethod.GET)
    public Message listAllUsers() {
        List<StaffInfoVO> dssUsers = dssWorkspaceUserService.listAllDSSUsers();
        return Message.ok().data("users", dssUsers);
    }

    /**
     * 判断username是否离职
     *
     * @param body
     * @return 离职返回true，未离职false
     */
    @RequestMapping(path = "isDismissed", method = RequestMethod.POST)
    public Message isDismissed(@RequestBody Map<String, List<String>> body) {
        List<String> usernames = body.get("usernames");
        List<Map<String, Boolean>> userStatus = new ArrayList<>(usernames.size());
        for (String username : usernames) {
            // 如果传入的username为空，则跳过
            if(StringUtils.isEmpty(username)){
                continue;
            }

            boolean isDismissed;
            if (username.startsWith("hduser") || username.startsWith("WTSS_") || "hadoop".equalsIgnoreCase(username)) {
                isDismissed = false;
            } else {
                StaffInfo staffInfo = staffInfoGetter.getStaffInfoByUsername(username);
                // 离职或不存在用户返回True
                isDismissed = staffInfo == null || "2".equals(staffInfo.getStatus());
            }
            Map<String, Boolean> tuple = Collections.singletonMap(username, isDismissed);
            userStatus.add(tuple);
        }
        return Message.ok().data("isDismissed", userStatus);
    }

    @RequestMapping(path = "getWorkspaceIdByUserName", method = RequestMethod.GET)
    public Message getWorkspaceIdByUserName(@RequestParam(required = false, name = "userName") String userName) {
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
            if (!token.equals(HPMS_USER_TOKEN)) {
                return Message.error("Token:" + token + " has no permission to get user info.");
            }
        } else {
            return Message.error("User:" + username + " has no permission to get user info.");
        }
        List<DSSWorkspaceRoleVO> userRoles = dssWorkspaceUserService.getUserRoleByUserName(username);
        return Message.ok().data("userName", username).data("roleInfo", userRoles);
    }

    @RequestMapping(path = "/clearUser", method = RequestMethod.GET)
    public Message clearUser(@RequestParam("userName") String userName, @RequestParam(name = "handover_ename", required = false) String handover_ename) {
        String token = ModuleUserUtils.getToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)) {
            if (!token.equals(HPMS_USER_TOKEN)) {
                return Message.error("Token:" + token + " has no permission to clear user.");
            }
        } else {
            return Message.error("User:" + userName + " has no permission to clear user.");
        }
        dssWorkspaceUserService.clearUserByUserName(userName);
        AuditLogUtils.printLog(userName, null, null, TargetTypeEnum.WORKSPACE_ROLE, null,
                "clearUser", OperateTypeEnum.DELETE, null);
        return Message.ok("清理成功");

    }

    @PostMapping(path = "/revokeUserRole")
    public Message revokeUserRole(@Validated @RequestBody RevokeUserRole revokeUserRole) {
        String userName = revokeUserRole.getUserName();
        Integer[] workspaceIds = revokeUserRole.getWorkspaceIds();
        Integer[] roleIds = revokeUserRole.getRoleIds();
        String token = ModuleUserUtils.getToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)) {
            if (!token.equals(HPMS_USER_TOKEN)) {
                return Message.error("Token:" + token + " has no permission to revoke userRole.");
            }
        } else {
            return Message.error("User:" + userName + " has no permission to revoke userRole.");
        }
        dssWorkspaceUserService.revokeUserRoles(userName, workspaceIds, roleIds);
        AuditLogUtils.printLog(userName, null, null, TargetTypeEnum.WORKSPACE_ROLE, null,
                "revokeUserRole", OperateTypeEnum.DELETE, revokeUserRole);
        return Message.ok("回收成功");

    }


    /**
     * 根据角色获取工作空间用户信息
     */
    @RequestMapping(path = "/getWorkspaceUserByRole", method = RequestMethod.GET)
    public Message getWorkspaceUserByRole(@RequestParam(value = WORKSPACE_ID_STR, required = false) Long workspaceId,
                                          @RequestParam(value = "roleId", required = false, defaultValue = "1") Integer roleId) {

        if (workspaceId == null) {
            workspaceId = SSOHelper.getWorkspace(httpServletRequest).getWorkspaceId();
        }
        LOGGER.info(String.format("getWorkspaceUserByRole workspaceId is %s, roleId is %s", workspaceId, roleId));
        List<String> users = dssWorkspaceUserService.getWorkspaceUserByRoleId(workspaceId, roleId);
        String workspaceName = dssWorkspaceService.getWorkspaceName(workspaceId);
        return Message.ok().data("users", users).data("workspaceName", workspaceName);

    }

    @RequestMapping(path = "/getWorkspaceProxyUsers", method = RequestMethod.GET)
    public Message getWorkspaceProxyUsers(@RequestParam(value = WORKSPACE_ID_STR, required = false) Long workspaceId) {


        if (workspaceId == null) {
            workspaceId = SSOHelper.getWorkspace(httpServletRequest).getWorkspaceId();
        }

        LOGGER.info("getWorkspaceProxyUsers workspaceId is {}", workspaceId);

        List<String> users = dssWorkspaceUserService.getAllWorkspaceUsers(workspaceId);

        return Message.ok().data("users", users);

    }

    @RequestMapping(path = "/updateUserDefaultWorkspace", method = RequestMethod.POST)
    public Message updateUserDefaultWorkspace(@RequestBody UpdateUserDefaultWorkspaceRequest request) throws DSSErrorException {

        String username = SecurityFilter.getLoginUsername(httpServletRequest);

        Long workspaceId = request.getWorkspaceId();
        String workspaceName = dssWorkspaceService.getWorkspaceName(workspaceId);
        if (StringUtils.isEmpty(workspaceName)) {
            LOGGER.error("updateUserDefaultWorkspace workspaceId is  {} , workspaceName is {}",workspaceId,workspaceName);
            throw new DSSErrorException(90054,String.format("%s workspace not exists!", workspaceName) );
        }

        request.setWorkspaceName(workspaceName);

        request.setUsername(username);

        DSSUserDefaultWorkspace dssUserDefaultWorkspace = dssWorkspaceUserService.updateUserDefaultWorkspace(request);

        AuditLogUtils.printLog(username, workspaceId,workspaceName, TargetTypeEnum.WORKSPACE, workspaceId, workspaceName,
                OperateTypeEnum.UPDATE, request);

        return Message.ok().data("dssUserDefaultWorkspace", dssUserDefaultWorkspace);
    }

    @RequestMapping(path = "/updateWorkspaceStarRocksCluster", method = RequestMethod.POST)
    public Message updateWorkspaceStarRocksCluster(@RequestBody UpdateWorkspaceStarRocksClusterRequestList requestWrapper) throws DSSErrorException {

        List<UpdateWorkspaceStarRocksClusterRequest> request = requestWrapper.getStarRocksUpdateRequest();
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        String ticketId = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> DSSWorkFlowConstant.BDP_USER_TICKET_ID.equals(cookie.getName()))
                .findFirst().map(Cookie::getValue).get();
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        if (CollectionUtils.isEmpty(request)) {
            Long workspaceId = SSOHelper.getWorkspace(httpServletRequest).getWorkspaceId();
            dssWorkspaceService.deleteStarRocksClusterByWorkspaceId(workspaceId);
            return Message.ok().data("dssWorkspaceStarRocksCluster", Lists.newArrayList());
        }

        request.forEach(r -> {
            String workspaceName = dssWorkspaceService.getWorkspaceName(r.getWorkspaceId());
            if (StringUtils.isEmpty(workspaceName)) {
                LOGGER.error("updateUserDefaultWorkspace workspaceId is {} , workspaceName is {}",r.getWorkspaceId(), workspaceName);
                throw new DSSErrorException(90054, String.format("%s workspace not exists!", workspaceName) );
            }

            if (!dssWorkspaceService.checkAdminByWorkspace(username, r.getWorkspaceId().intValue())) {
                throw new DSSErrorException(90054, String.format("%s 用户不是当前工作空间管理员,无权限进行该操作!",username));
            }
            r.setWorkspaceName(workspaceName);
            r.setUsername(username);
        });

        List<DSSWorkspaceStarRocksCluster> dssWorkspaceStarRocksCluster = dssWorkspaceService.updateStarRocksCluster(request, ticketId, workspace, username);

        AuditLogUtils.printLog(username, request.get(0).getWorkspaceId(), request.get(0).getWorkspaceName(), TargetTypeEnum.WORKSPACE, request.get(0).getWorkspaceId(), request.get(0).getWorkspaceName(),
                OperateTypeEnum.UPDATE, request);

        return Message.ok().data("dssWorkspaceStarRocksCluster", dssWorkspaceStarRocksCluster);
    }


    @RequestMapping(path = "/getWorkspaceStarRocksCluster", method = RequestMethod.GET)
    public Message getWorkspaceStarRocksCluster(@RequestParam(value = WORKSPACE_ID_STR, required = false) Long workspaceId) throws DSSErrorException {

        if (workspaceId == null) {
            workspaceId = SSOHelper.getWorkspace(httpServletRequest).getWorkspaceId();
        }

        LOGGER.info("getWorkspaceStarRocksCluster workspaceId is {}", workspaceId);

        List<DSSWorkspaceStarRocksCluster> starRocksCluster = dssWorkspaceService.getStarRocksCluster(workspaceId);

        return Message.ok().data("dssWorkspaceStarRocksCluster", starRocksCluster);
    }


}
