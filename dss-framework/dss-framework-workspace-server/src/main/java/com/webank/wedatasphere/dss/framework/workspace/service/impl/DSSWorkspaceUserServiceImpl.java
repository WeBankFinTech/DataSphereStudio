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
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceUser;
import com.webank.wedatasphere.dss.framework.workspace.bean.StaffInfo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentUserTreeVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentUserVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.StaffInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceUserMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceAddUserHook;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.dss.framework.workspace.service.StaffInfoGetter;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceServerConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkspaceUser(String userName, int workspaceId) {
        dssWorkspaceUserMapper.removeAllRolesForUser(userName, workspaceId);
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
                String departmentName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[0];
                String officeName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[1];
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
                String departmentName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[0];
                String officeName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[1];
                vo.setDepartment(departmentName);
                vo.setOffice(officeName);
            } catch (Exception e) {
                //LOGGER.warn("fail to get department and office {} ", e.getMessage());
                vo.setDepartment(WorkspaceServerConstant.DEFAULT_DEPARTMENT);
                vo.setOffice(WorkspaceServerConstant.DEFAULT_OFFICE);
            }
        }
        return vo;
    }

    @Override
    public PageInfo<String> getAllWorkspaceUsersPage(long workspaceId, Integer pageNow, Integer pageSize) {
        PageHelper.startPage(pageNow, pageSize);
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeUserRoles(String userName, Integer[] workspaceIds, Integer[] roleIds) {
        dssWorkspaceUserMapper.deleteUserRoles(userName, workspaceIds, roleIds);
    }

    @Override
    public List<String> getWorkspaceUserByRoleId(Long workSpaceId, Integer roleId) {
        return dssWorkspaceUserMapper.getWorkspaceUserByRoleId(workSpaceId, roleId);
    }
}
