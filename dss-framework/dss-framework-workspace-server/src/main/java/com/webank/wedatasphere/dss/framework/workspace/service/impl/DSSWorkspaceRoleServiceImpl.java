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

package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.webank.wedatasphere.dss.framework.workspace.bean.DSSRole;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceRoleMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleService;
import com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * created by cooperyang on 2020/3/23
 * Description:
 */
@Service
public class DSSWorkspaceRoleServiceImpl implements DSSWorkspaceRoleService {


    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceRoleServiceImpl.class);

    @Autowired
    private DSSWorkspaceRoleMapper dssWorkspaceRoleMapper;

    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWorkspaceRole(String roleName, int workspaceId, List<Integer> menuIds, List<Integer> componentIds, String username) {
        DSSRole dssRole = new DSSRole();
        dssRole.setWorkspaceId(workspaceId);
        dssRole.setFrontName(roleName);
        dssRole.setName(roleName);
        dssRole.setCreateTime(new Date());
        dssRole.setDescription("workspace{ " + workspaceId + " }添加的role");
        dssWorkspaceRoleMapper.addNewRole(dssRole);
        List<Integer> allMenuIds = workspaceDBHelper.getAllMenuIds();
        if (menuIds.size() > 0){
            dssWorkspaceRoleMapper.updateRoleMenu(dssRole.getId(), workspaceId, menuIds, username, 1);
        }
        if (menuIds.size() < allMenuIds.size()){
            allMenuIds.removeAll(menuIds);
            dssWorkspaceRoleMapper.updateRoleMenu(dssRole.getId(), workspaceId, allMenuIds, username, 0);
        }
        List<Integer> allComponentIds = workspaceDBHelper.getAllComponentIds();
        if (componentIds.size() > 0){
            dssWorkspaceRoleMapper.updateRoleComponent(dssRole.getId(), workspaceId, componentIds, username, 1);
        }
        if (componentIds.size() < allComponentIds.size()){
            allComponentIds.removeAll(componentIds);
            dssWorkspaceRoleMapper.updateRoleComponent(dssRole.getId(), workspaceId, allComponentIds, username, 0);
        }
        workspaceDBHelper.retrieveFromDB();
    }

    @Override
    public List<String> getRoleInWorkspace(String username, int workspaceId) {
        LOGGER.info("get roles for user {} in workspace {}", username, workspaceId);
        return dssWorkspaceRoleMapper.getAllRoles(username, workspaceId);
    }

    @Override
    public Integer getWorkspaceIdByUser(String username) {
        List<Integer> workspaceIds = dssWorkspaceRoleMapper.getWorkspaceIds(username);
        Integer defaultWorkspaceId = dssWorkspaceRoleMapper.getDefaultWorkspaceId(DSSWorkspaceConstant.DEFAULT_WORKSPACE_NAME.getValue());
        if (workspaceIds.isEmpty()){
            return defaultWorkspaceId;
        } else if (workspaceIds.size() == 1){
            return workspaceIds.get(0);
        } else {
            workspaceIds.remove(defaultWorkspaceId);
            return workspaceIds.get(0);
        }
    }

    @Override
    public int getApiPriv(String username, Integer workspaceId, String roleName, String appName) {
        int roleId = dssWorkspaceRoleMapper.getRoleId(roleName, -1);
        int componentId = dssWorkspaceRoleMapper.getComponentId(appName);
        int count = dssWorkspaceRoleMapper.getCount(workspaceId, componentId, roleId);
        if (count >= 1){
            Integer tmpPriv = dssWorkspaceRoleMapper.getPriv(workspaceId, roleId, componentId);
            return tmpPriv!=null ? tmpPriv.intValue() : 0;
        }else{
            Integer tmpPriv = dssWorkspaceRoleMapper.getPriv(-1,roleId, componentId);
            return tmpPriv!=null ? tmpPriv.intValue() : 0;
        }
    }
}
