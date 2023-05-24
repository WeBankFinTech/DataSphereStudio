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

import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceUser;
import com.webank.wedatasphere.dss.framework.workspace.bean.StaffInfo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.StaffInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceUserMapper;
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

import java.util.*;
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

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateWorkspaceUser(List<Integer> roles, int workspaceId, String userName, String creator) {
        //获取用户创建时间
        DSSWorkspaceUser workspaceUsers = dssWorkspaceUserMapper.getWorkspaceUsers(String.valueOf(workspaceId), userName, null).stream().findFirst().get();
        dssWorkspaceUserMapper.removeAllRolesForUser(userName, workspaceId);
        roles.forEach(role ->{
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

    private StaffInfoVO staffToDSSUser(StaffInfo staffInfo){
        StaffInfoVO staffInfoVO = new StaffInfoVO();
        String orgFullName = staffInfo.getOrgFullName();
        if (StringUtils.isNotEmpty(orgFullName)){
            try{
                String departmentName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[0];
                String officeName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[1];
                staffInfoVO.setDepartment(departmentName);
                staffInfoVO.setOffice(officeName);
            }catch(Exception e){
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
    public List<String> getAllWorkspaceUsers(int workspaceId) {
        return dssWorkspaceUserMapper.getAllWorkspaceUsers(workspaceId);
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
    public Long getCountByUsername(String username,int workspaceId){
        return dssWorkspaceUserMapper.getCountByUsername(username,workspaceId);
    }

    @Override
    public List<Map<String,Object>> getUserRoleByUserName(String userName) {
        List<DSSWorkspaceUser> workspaceRoles = dssWorkspaceUserMapper.getWorkspaceRoleByUsername(userName);
        List<Map<String,Object>> list = new ArrayList<>();
        workspaceRoles.forEach(workspaceRole -> {
            Map<String, Object> map = new HashMap<>();
            map.put("workspaceId", workspaceRole.getWorkspaceId());
            map.put("roleId", workspaceRole.getRoleIds());
            map.put("roleName", workspaceDBHelper.getRoleFrontName(Integer.parseInt(workspaceRole.getRoleIds())));
            list.add(map);
        });
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearUserByUserName(String userName) {
        if(staffInfoGetter.getAllUsers().stream().anyMatch(staffInfo -> staffInfo.getEnglishName().equals(userName))) {
            dssWorkspaceUserMapper.deleteUserByUserName(userName);
            dssWorkspaceUserMapper.deleteUserRolesByUserName(userName);
            dssWorkspaceUserMapper.deleteProxyUserByUserName(userName);
            return true;
        }else{
            return false;
        }
    }
}
