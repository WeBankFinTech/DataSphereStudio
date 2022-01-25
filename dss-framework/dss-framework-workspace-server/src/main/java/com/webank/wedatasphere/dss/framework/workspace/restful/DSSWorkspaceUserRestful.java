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
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceUser01;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.DeleteWorkspaceUserRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.UpdateWorkspaceUserRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceUserVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceUsersVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.StaffInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceMenuService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;


@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
public class DSSWorkspaceUserRestful {

    @Autowired
    private DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSWorkspaceMenuService dssWorkspaceMenuService;
    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;
    @Autowired
    private DSSWorkspaceUserService dssWorkspaceUserService;

    @RequestMapping(path ="getWorkspaceUsers", method = RequestMethod.GET)
    public Message getWorkspaceUsers(HttpServletRequest request, @RequestParam(WORKSPACE_ID_STR) String workspaceId,
                                     @RequestParam(required = false, name = "pageNow") Integer pageNow, @RequestParam(required = false, name = "pageSize") Integer pageSize,
                                     @RequestParam(required = false, name = "department") String department, @RequestParam(required = false, name = "username") String username,
                                     @RequestParam(required = false, name = "roleName") String roleName){
        //todo 获取工作空间中所有的用户以及他们的角色信息
        if(pageNow == null){
            pageNow = 1;
        }
        if(pageSize == null){
            //默认改成20
            pageSize = 20;
        }
        pageSize = 500;
        if(StringUtils.isNotEmpty(roleName)){
            //如果roleName不是空的话，就按照roleName来吧
            List<Long> totals = new ArrayList<>();
            List<DSSWorkspaceUserVO> workspaceUsers =
                    dssWorkspaceService.getWorkspaceUsersByRole(Integer.parseInt(workspaceId),roleName, totals, pageNow, pageSize);
            PageInfo<DSSWorkspaceUserVO> pageInfo = new PageInfo<>(workspaceUsers);
            List<DSSWorkspaceUserVO> list = pageInfo.getList();
            long total = pageInfo.getTotal();
            List<DSSWorkspaceRoleVO> dssRoles = workspaceDBHelper.getRoleVOs(Integer.parseInt(workspaceId));
            return Message.ok().data("roles", dssRoles).data("workspaceUsers", list).data("total", totals.get(0));
        }else{
            List<Long> totals = new ArrayList<>();
            List<DSSWorkspaceUser01> workspaceUsers =
                    dssWorkspaceService.getWorkspaceUsers(workspaceId, department, username, roleName, pageNow, pageSize, totals);
            PageInfo<DSSWorkspaceUser01> pageInfo = new PageInfo<>(workspaceUsers);
            List<DSSWorkspaceUser01> list = pageInfo.getList();
            long total = pageInfo.getTotal();
            List<DSSWorkspaceRoleVO> dssRoles = workspaceDBHelper.getRoleVOs(Integer.parseInt(workspaceId));
            return Message.ok().data("roles", dssRoles).data("workspaceUsers", list).data("total", totals.get(0));
        }
    }

    @RequestMapping(path ="getAllWorkspaceUsers", method = RequestMethod.GET)
    public Message getAllWorkspaceUsers(@Context HttpServletRequest request, @RequestParam(WORKSPACE_ID_STR) int workspaceId ){
        DSSWorkspaceUsersVo dssWorkspaceUsersVo = new DSSWorkspaceUsersVo();
        dssWorkspaceUsersVo.setAccessUsers(dssWorkspaceUserService.getAllWorkspaceUsers(workspaceId));
        dssWorkspaceUsersVo.setEditUsers(dssWorkspaceUserService.getWorkspaceEditUsers(workspaceId));
        dssWorkspaceUsersVo.setReleaseUsers(dssWorkspaceUserService.getWorkspaceReleaseUsers(workspaceId));
        return Message.ok().data("users", dssWorkspaceUsersVo);
    }

    @RequestMapping(path ="addWorkspaceUser", method = RequestMethod.POST)
    public Message addWorkspaceUser(HttpServletRequest request,@RequestBody UpdateWorkspaceUserRequest updateWorkspaceUserRequest){
        //todo 工作空间添加用户
        String creator = SecurityFilter.getLoginUsername(request);
        List<Integer> roles = updateWorkspaceUserRequest.getRoles();
        int workspaceId = updateWorkspaceUserRequest.getWorkspaceId();
        String userName = updateWorkspaceUserRequest.getUserName();
        String userId = updateWorkspaceUserRequest.getUserId();

        dssWorkspaceService.addWorkspaceUser(roles, workspaceId, userName, creator,userId);
        return Message.ok();
    }

    @RequestMapping(path ="updateWorkspaceUser", method = RequestMethod.POST)
    public Message updateWorkspaceUser(HttpServletRequest request,@RequestBody UpdateWorkspaceUserRequest updateWorkspaceUserRequest){
        String creator = SecurityFilter.getLoginUsername(request);
        List<Integer> roles = updateWorkspaceUserRequest.getRoles();
        int workspaceId = updateWorkspaceUserRequest.getWorkspaceId();
        String userName = updateWorkspaceUserRequest.getUserName();
        dssWorkspaceUserService.updateWorkspaceUser(roles, workspaceId, userName, creator);
        return Message.ok();
    }

    @RequestMapping(path ="deleteWorkspaceUser", method = RequestMethod.POST)
    public Message deleteWorkspaceUser(HttpServletRequest request,@RequestBody DeleteWorkspaceUserRequest deleteWorkspaceUserRequest){
        //todo 删除工作空间中的用户
        String userName = deleteWorkspaceUserRequest.getUsername();
        int workspaceId = deleteWorkspaceUserRequest.getWorkspaceId();
        dssWorkspaceUserService.deleteWorkspaceUser(userName,workspaceId);
        return Message.ok();
    }

    @RequestMapping(path ="listAllUsers", method = RequestMethod.GET)
    public Message listAllUsers(HttpServletRequest request){
        List<StaffInfoVO> dssUsers = dssWorkspaceUserService.listAllDSSUsers();
        return Message.ok().data("users", dssUsers);
    }

    @RequestMapping(path ="getWorkspaceIdByUserName", method = RequestMethod.GET)
    public Message getWorkspaceIdByUserName(HttpServletRequest request,@RequestParam(required = false, name = "userName") String userName){
        String loginUserName = SecurityFilter.getLoginUsername(request);
        String queryUserName = userName;
        if(StringUtils.isEmpty(userName)){
            queryUserName = loginUserName;
        }
        List<Integer> userWorkspaceIds = dssWorkspaceUserService.getUserWorkspaceIds(queryUserName);
        String userWorkspaceIdStr = userWorkspaceIds.stream().map(x->x.toString()).collect(Collectors.joining(","));
        return Message.ok().data("userWorkspaceIds", userWorkspaceIdStr);
    }
}
