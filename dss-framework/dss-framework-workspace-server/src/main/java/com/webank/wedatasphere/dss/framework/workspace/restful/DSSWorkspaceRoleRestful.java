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
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceRole;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.AddWorkspaceRoleRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;


@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
public class DSSWorkspaceRoleRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceRoleRestful.class);
    @Autowired
    DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSWorkspaceRoleService dssWorkspaceRoleService;

    @RequestMapping(path = "getWorkspaceRoles", method = RequestMethod.GET)
    public Message getWorkspaceRoles(@RequestParam(WORKSPACE_ID_STR) int workspaceId) {
        //todo 获取工作空间中所有的角色
        List<DSSWorkspaceRoleVO> workspaceRoles = dssWorkspaceService.getWorkspaceRoles(workspaceId);
        return Message.ok().data("workspaceRoles", workspaceRoles);
    }

    @RequestMapping(path = "addWorkspaceRole", method = RequestMethod.POST)
    public Message addWorkspaceRole(HttpServletRequest request, @RequestBody AddWorkspaceRoleRequest addWorkspaceRoleRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        int workspaceId = addWorkspaceRoleRequest.getWorkspaceId();
        String roleName = addWorkspaceRoleRequest.getRoleName();
        List<Integer> menuIds = addWorkspaceRoleRequest.getMenuIds();
        List<Integer> componentIds = addWorkspaceRoleRequest.getComponentIds();
        if (!dssWorkspaceService.checkAdmin(username) || !dssWorkspaceService.checkAdminByWorkspace(username, workspaceId)) {
            return Message.error("无权限进行该操作");
        }
        DSSWorkspaceRole dssRole = dssWorkspaceRoleService.addWorkspaceRole(roleName, workspaceId, menuIds, componentIds, username);
        String workspaceName = dssWorkspaceService.getWorkspaceName((long) workspaceId);
        AuditLogUtils.printLog(username, workspaceId,workspaceName, TargetTypeEnum.WORKSPACE_ROLE,dssRole.getId(),roleName,
                OperateTypeEnum.CREATE, addWorkspaceRoleRequest);
        return Message.ok("创建角色成功");
    }

    @RequestMapping(path = "deleteWorkspaceRole", method = RequestMethod.POST)
    public Message deleteWorkspaceRole() {

        return null;
    }

}
