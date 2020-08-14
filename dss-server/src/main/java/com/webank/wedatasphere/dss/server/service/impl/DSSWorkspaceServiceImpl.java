/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.server.service.impl;

import com.webank.wedatasphere.dss.server.constant.DSSServerConstant;
import com.webank.wedatasphere.dss.server.dao.WorkspaceMapper;
import com.webank.wedatasphere.dss.server.dto.response.*;
import com.webank.wedatasphere.dss.server.entity.*;
import com.webank.wedatasphere.dss.server.service.DSSWorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schumiyi on 2020/6/22
 */
@Service
public class DSSWorkspaceServiceImpl implements DSSWorkspaceService {

    @Autowired
    private WorkspaceMapper workspaceMapper;

    @Override
    public List<DSSWorkspace> getWorkspaces(String userName) {

        return workspaceMapper.getWorkspaces(userName);
    }

    @Override
    public DSSWorkspace getWorkspacesById(Long id) {
        return workspaceMapper.getWorkspaceById(id);
    }


    @Override
    public Long addWorkspace(String userName, String name, String department, String label, String description) {
        DSSWorkspace dssWorkspace = new DSSWorkspace();
        dssWorkspace.setName(name);
        dssWorkspace.setDepartment(department);
        dssWorkspace.setDescription(description);
        dssWorkspace.setLabel(label);
        dssWorkspace.setCreateBy(userName);
        dssWorkspace.setSource(DSSServerConstant.DSS_WORKSPACE_SOURCE);
        dssWorkspace.setLastUpdateUser(userName);
        workspaceMapper.addWorkSpace(dssWorkspace);
        return dssWorkspace.getId();
    }

    @Override
    public boolean existWorkspaceName(String name) {
        return !workspaceMapper.findByWorkspaceName(name).isEmpty();
    }

    @Override
    public List<WorkspaceDepartmentVo> getWorkSpaceDepartments() {
        // TODO: service层和dao层完善
        WorkspaceDepartmentVo dp = new WorkspaceDepartmentVo();
        dp.setId(1L);
        dp.setName("应用开发组");
        WorkspaceDepartmentVo di = new WorkspaceDepartmentVo();
        di.setId(2L);
        di.setName("平台研发组");
        List<WorkspaceDepartmentVo> departments = new ArrayList<>();
        departments.add(dp);
        departments.add(di);
        return departments;
    }

    @Override
    public List<HomepageDemoMenuVo> getHomepageDemos(boolean isChinese) {
        List<HomepageDemoMenuVo> demoMenuVos = isChinese ? workspaceMapper.getHomepageDemoMenusCn() : workspaceMapper.getHomepageDemoMenusEn();
        for (HomepageDemoMenuVo demoMenuVo : demoMenuVos) {
            Long menuId = demoMenuVo.getId();
            List<HomepageDemoInstanceVo> demoInstanceVos = isChinese ? workspaceMapper.getHomepageInstancesByMenuIdCn(menuId) : workspaceMapper.getHomepageInstancesByMenuIdEn(menuId);
            demoMenuVo.setDemoInstances(demoInstanceVos);
        }
        return demoMenuVos;
    }

    @Override
    public List<HomepageVideoVo> getHomepageVideos(boolean isChinese) {
        return isChinese ? workspaceMapper.getHomepageVideosCn() : workspaceMapper.getHomepageVideosEn();
    }

    @Override
    public List<OnestopMenuVo> getWorkspaceManagements(Long workspaceId, String username, boolean isChinese) {
        if (!isAdminUser(workspaceId, username)) {
            return new ArrayList<>();
        }
        List<OnestopMenuVo> managementMenuVos = isChinese ? workspaceMapper.getManagementMenuCn() : workspaceMapper.getManagementMenuEn();
        return getMenuAppInstances(managementMenuVos, isChinese);
    }

    private List<OnestopMenuVo> getMenuAppInstances(List<OnestopMenuVo> menuVos, boolean isChinese) {
        for (OnestopMenuVo menuVo : menuVos) {
            Long menuId = menuVo.getId();
            List<OnestopMenuAppInstanceVo> menuAppInstanceVos = isChinese ? workspaceMapper.getMenuAppInstancesCn(menuId) : workspaceMapper.getMenuAppInstancesEn(menuId);
            menuVo.setAppInstances(menuAppInstanceVos);
        }
        return menuVos;
    }

    @Override
    public List<OnestopMenuVo> getWorkspaceApplications(Long workspaceId, String username, boolean isChinese) {
        List<OnestopMenuVo> applicationMenuVos = isChinese ? workspaceMapper.getApplicationMenuCn() : workspaceMapper.getApplicationMenuEn();
        return getMenuAppInstances(applicationMenuVos, isChinese);
    }

    @Override
    public List<WorkspaceFavoriteVo> getWorkspaceFavorites(Long workspaceId, String username, boolean isChinese) {
        return isChinese ? workspaceMapper.getWorkspaceFavoritesCn(username, workspaceId) : workspaceMapper.getWorkspaceFavoritesEn(username, workspaceId);
    }

    @Override
    public Long addFavorite(String username, Long workspaceId, Long menuApplicationId) {
        DSSFavorite dssFavorite = new DSSFavorite();
        dssFavorite.setUsername(username);
        dssFavorite.setWorkspaceId(workspaceId);
        dssFavorite.setMenuApplicationId(menuApplicationId);
        // todo: order will from the front end
        dssFavorite.setOrder(1);
        dssFavorite.setCreateBy(username);
        dssFavorite.setLastUpdateUser(username);
        workspaceMapper.addFavorite(dssFavorite);
        return dssFavorite.getId();
    }

    @Override
    public Long deleteFavorite(String username, Long favouritesId) {
        workspaceMapper.deleteFavorite(favouritesId);
        return favouritesId;
    }

    private boolean isAdminUser(Long workspaceId, String username) {
        DSSWorkspace workspace = workspaceMapper.getWorkspaceById(workspaceId);
        return username != null && workspace != null && username.equals(workspace.getCreateBy());
    }
}
