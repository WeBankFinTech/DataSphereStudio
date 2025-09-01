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

package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.github.pagehelper.PageHelper;
import com.webank.wedatasphere.dss.common.entity.PageInfo;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSUserDefaultWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceUser;
import com.webank.wedatasphere.dss.common.StaffInfo;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.UpdateUserDefaultWorkspaceRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentUserTreeVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentUserVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.StaffInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceUserMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceAddUserHook;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.dss.common.StaffInfoGetter;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.common.conf.WorkspaceServerConstant;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
public class DSSWorkspaceUserServiceImpl implements DSSWorkspaceUserService {


    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceUserServiceImpl.class);
    @Autowired
    private DSSWorkspaceUserMapper dssWorkspaceUserMapper;

    @Autowired
    private StaffInfoGetter staffInfoGetter;

    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;
    @Autowired
    private DssAdminUserService dssUserService;
    @Autowired
    private DSSWorkspaceAddUserHook dssWorkspaceAddUserHook;

    private static final String NODE_TYPE_DEPARTMENT = "department";

    private static final String NODE_TYPE_OFFICE = "office";

    private static final String NODE_TYPE_USER = "user";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWorkspaceUser(List<Integer> roleIds, long workspaceId, String userName, String creator, String userId) {

        //保存 - 保存用户角色关系 dss_workspace_user_role
        for (Integer roleId : roleIds) {
            dssWorkspaceUserMapper.insertUserRoleInWorkspace((int) workspaceId, roleId, new Date(), userName, creator, userId == null ? null : Long.parseLong(userId), creator);
        }
        dssWorkspaceAddUserHook.afterAdd(userName, workspaceId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateWorkspaceUser(List<Integer> roles, long workspaceId, String userName, String creator) {
        //获取用户创建时间
        DSSWorkspaceUser workspaceUsers = dssWorkspaceUserMapper.getWorkspaceUsers(String.valueOf(workspaceId), userName, null).stream().findFirst().get();
        dssWorkspaceUserMapper.removeAllRolesForUser(userName, workspaceId);
        roles.forEach(role -> {
            dssWorkspaceUserMapper.insertUserRoleInWorkspace(workspaceId, role, workspaceUsers.getJoinTime(), userName, workspaceUsers.getCreator(), 0L, creator);
        });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateWorkspaceRole(List<Integer> roles, long workspaceId,String newOwner){
        //获取用户创建时间
        dssWorkspaceUserMapper.removeAllRolesForUser(newOwner, workspaceId);
        roles.forEach(role -> {
            dssWorkspaceUserMapper.insertUserRoleInWorkspace(workspaceId, role, new Date() , newOwner, newOwner, 0L, newOwner);
        });
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkspaceUser(String userName, int workspaceId) {
        dssWorkspaceUserMapper.removeAllRolesForUser(userName, workspaceId);
        // 删除用户的默认工作空间信息
        dssWorkspaceUserMapper.deleteUserDefaultWorkspace(userName, Long.valueOf(workspaceId));
    }

    @Override
    public List<StaffInfoVO> listAllDSSUsers() {
        //需要将esb带来的所有用户进行返回
        List<StaffInfo> staffInfos = staffInfoGetter.getAllUsers();
        return staffInfos.stream().map(this::staffToDSSUser).collect(Collectors.toList());
    }

    private StaffInfoVO staffToDSSUser(StaffInfo staffInfo) {
        StaffInfoVO staffInfoVO = new StaffInfoVO();
        String orgFullName = staffInfo.getOrgFullName();
        if (StringUtils.isNotEmpty(orgFullName)) {
            try {
                String[] fullNameArray = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT);
                String departmentName = fullNameArray[0];
                String officeName = "";
                if(fullNameArray.length>1){
                    officeName = fullNameArray[1];
                }
                staffInfoVO.setDepartment(departmentName);
                staffInfoVO.setOffice(officeName);
            } catch (Exception e) {
                //LOGGER.warn("fail to get department and office {} ", e.getMessage());
                staffInfoVO.setDepartment(WorkspaceServerConstant.DEFAULT_DEPARTMENT);
                staffInfoVO.setOffice(WorkspaceServerConstant.DEFAULT_OFFICE);
            }
        }
        staffInfoVO.setUsername(staffInfo.getEnglishName());
        staffInfoVO.setId(staffInfo.getId());
        return staffInfoVO;
    }

    @Override
    public List<String> getAllWorkspaceUsers(long workspaceId) {
        return dssWorkspaceUserMapper.getAllWorkspaceUsers(workspaceId);
    }

    @Override
    public List<DepartmentUserTreeVo> getAllWorkspaceUsersDepartment(long workspaceId) {
        List<DepartmentUserVo> userList = dssWorkspaceUserMapper.getAllWorkspaceUsers(workspaceId).stream().map(
                        this::changeToUserVO)
                .collect(Collectors.toList());
        return structureVo2Tree(userList);
    }

    private List<DepartmentUserTreeVo> structureVo2Tree(List<DepartmentUserVo> userList) {
        AtomicReference<Integer> id = new AtomicReference<>(0);
        Map<String, Map<String, List<DepartmentUserTreeVo>>> userMap = userList.stream().collect(Collectors.groupingBy(DepartmentUserVo::getDepartment,
                Collectors.groupingBy(DepartmentUserVo::getOffice, Collectors.mapping(user ->
                        new DepartmentUserTreeVo(id.getAndSet(id.get() + 1), user.getName(), NODE_TYPE_USER, null), Collectors.toList()))));
        return userMap.keySet().stream().map(key ->
                new DepartmentUserTreeVo(id.getAndSet(id.get() + 1), key, NODE_TYPE_DEPARTMENT, buildTree4Office(userMap.get(key), id))
        ).collect(Collectors.toList());
    }

    private List<DepartmentUserTreeVo> buildTree4Office(Map<String, List<DepartmentUserTreeVo>> officeMap, AtomicReference<Integer> id) {
        return officeMap.keySet().stream().map(key ->
                new DepartmentUserTreeVo(id.getAndSet(id.get() + 1), key, NODE_TYPE_OFFICE, officeMap.get(key))
        ).collect(Collectors.toList());
    }

    private DepartmentUserVo changeToUserVO(String userName) {
        DepartmentUserVo vo = new DepartmentUserVo();
        vo.setName(userName);
        String orgFullName = staffInfoGetter.getFullOrgNameByUsername(userName);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(orgFullName)) {
            try {
                String[] fullNameArray = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT);
                String departmentName = fullNameArray[0];
                String officeName = WorkspaceServerConstant.DEFAULT_STAFF_SPLIT;
                if(fullNameArray.length>1){
                    officeName = fullNameArray[1];
                }
                vo.setDepartment(departmentName);
                vo.setOffice(officeName);
            } catch (Exception e) {
                //LOGGER.warn("fail to get department and office {} ", e.getMessage());
                vo.setDepartment(WorkspaceServerConstant.DEFAULT_DEPARTMENT);
                vo.setOffice(WorkspaceServerConstant.DEFAULT_OFFICE);
            }
        }else{
            vo.setDepartment(WorkspaceServerConstant.DEFAULT_RESIGNED);
            vo.setOffice(WorkspaceServerConstant.DEFAULT_RESIGNED);
        }
        return vo;
    }

    @Override
    public PageInfo<String> getAllWorkspaceUsersPage(long workspaceId, Integer pageNow, Integer pageSize,boolean paged) {
        if(paged) {
            PageHelper.startPage(pageNow, pageSize);
        }
        List<String> dos = dssWorkspaceUserMapper.getAllWorkspaceUsers(workspaceId);
        com.github.pagehelper.PageInfo<String> doPage = new com.github.pagehelper.PageInfo<>(dos);
        return new PageInfo<>(doPage.getList(), doPage.getTotal());
    }

    @Override
    public List<Integer> getUserWorkspaceIds(String userName) {
        List<Integer> tempWorkspaceIds = dssWorkspaceUserMapper.getWorkspaceIds(userName);
        return tempWorkspaceIds;
    }

    @Override
    public List<String> getWorkspaceEditUsers(int workspaceId) {
        return dssWorkspaceUserMapper.getWorkspaceEditUsers(workspaceId);
    }

    @Override
    public List<String> getWorkspaceReleaseUsers(int workspaceId) {
        return dssWorkspaceUserMapper.getWorkspaceReleaseUsers(workspaceId);
    }

    @Override
    public Long getCountByUsername(String username, int workspaceId) {
        return dssWorkspaceUserMapper.getCountByUsername(username, workspaceId);
    }

    @Override
    public Long getUserCount(long workspaceId) {
        return dssWorkspaceUserMapper.getUserCountByWorkspaceId(workspaceId);
    }

    @Override
    public List<DSSWorkspaceRoleVO> getUserRoleByUserName(String userName) {
        List<DSSWorkspaceRoleVO> workspaceUserRoles = dssWorkspaceUserMapper.getWorkspaceRoleByUsername(userName);
        workspaceUserRoles.forEach(workspaceUserRole -> workspaceUserRole.setRoleName(workspaceDBHelper.getRoleFrontName(workspaceUserRole.getRoleId())));
        return workspaceUserRoles;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearUserByUserName(String userName) {
        dssWorkspaceUserMapper.deleteUserByUserName(userName);
        dssWorkspaceUserMapper.deleteUserRolesByUserName(userName);
        dssWorkspaceUserMapper.deleteProxyUserByUserName(userName);
        dssWorkspaceUserMapper.deleteProjectUserByUserName(userName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeUserRoles(String userName, Integer[] workspaceIds, Integer[] roleIds) {
        dssWorkspaceUserMapper.deleteUserRoles(userName, workspaceIds, roleIds);
    }

    @Override
    public List<String> getWorkspaceUserByRoleId(Long workspaceId, Integer roleId) {
        return dssWorkspaceUserMapper.getWorkspaceUserByRoleId(workspaceId, roleId);
    }

    @Override
    public DSSUserDefaultWorkspace updateUserDefaultWorkspace(UpdateUserDefaultWorkspaceRequest request) throws DSSErrorException {

        String username = request.getUsername();
        Long  workspaceId = request.getWorkspaceId();

        Long count = getCountByUsername(request.getUsername(), request.getWorkspaceId().intValue());
        // 判断是否有权限操作工作空间  -> 无权限则抛错
        if (count == null || count == 0) {
            throw new DSSErrorException(30021, String.format("user: %s have no permission to access this workspace %s",
                    username,request.getWorkspaceName()));
        }

        DSSUserDefaultWorkspace dssUserDefaultWorkspace = new DSSUserDefaultWorkspace();

        // isDefaultWorkspace： 是  更新默认工作空间信息
        if(Boolean.TRUE.equals(request.getIsDefaultWorkspace())){

            dssUserDefaultWorkspace = dssWorkspaceUserMapper.getDefaultWorkspaceByUsername(username);

            if(dssUserDefaultWorkspace == null){

                dssUserDefaultWorkspace = new DSSUserDefaultWorkspace();
                dssUserDefaultWorkspace.setWorkspaceId(workspaceId);
                dssUserDefaultWorkspace.setUsername(username);
                dssUserDefaultWorkspace.setCreateUser(username);
                dssUserDefaultWorkspace.setUpdateUser(username);
                dssWorkspaceUserMapper.insertUserDefaultWorkspace(dssUserDefaultWorkspace);

            }else{

                dssUserDefaultWorkspace.setWorkspaceId(workspaceId);
                dssUserDefaultWorkspace.setUpdateUser(username);
                dssWorkspaceUserMapper.updateUserDefaultWorkspace(dssUserDefaultWorkspace);
            }

        }else {
            // isDefaultWorkspace:  否  取消默认工作空间信息
            dssUserDefaultWorkspace.setWorkspaceId(workspaceId);
            dssUserDefaultWorkspace.setUsername(username);
            dssWorkspaceUserMapper.deleteUserDefaultWorkspace(username,workspaceId);
        }

        return  dssUserDefaultWorkspace;
    }
}
