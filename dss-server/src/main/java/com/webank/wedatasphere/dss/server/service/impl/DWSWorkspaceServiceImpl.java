package com.webank.wedatasphere.dss.server.service.impl;

import com.webank.wedatasphere.dss.server.constant.DSSServerConstant;
import com.webank.wedatasphere.dss.server.dao.WorkspaceMapper;
import com.webank.wedatasphere.dss.server.dto.response.*;
import com.webank.wedatasphere.dss.server.entity.*;
import com.webank.wedatasphere.dss.server.service.DWSWorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schumiyi on 2020/6/22
 */
@Service
public class DWSWorkspaceServiceImpl implements DWSWorkspaceService {

    @Autowired
    private WorkspaceMapper workspaceMapper;

    @Override
    public List<DWSWorkspace> getWorkspaces() {

        return workspaceMapper.getWorkspaces();
    }

    @Override
    public DWSWorkspace getWorkspacesById(Long id) {
        return workspaceMapper.getWorkspaceById(id);
    }


    @Override
    public Long addWorkspace(String userName, String name, String department, String label, String description) {
        DWSWorkspace dwsWorkspace = new DWSWorkspace();
        dwsWorkspace.setName(name);
        dwsWorkspace.setDepartment(department);
        dwsWorkspace.setDescription(description);
        dwsWorkspace.setLabel(label);
        dwsWorkspace.setCreateBy(userName);
        dwsWorkspace.setSource(DSSServerConstant.DWS_WORKSPACE_SOURCE);
        dwsWorkspace.setLastUpdateUser(userName);
        workspaceMapper.addWorkSpace(dwsWorkspace);
        return dwsWorkspace.getId();
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
            List<OnestopMenuAppInstanceVo> menuAppInstanceVos = isChinese ? workspaceMapper.getMenuAppInstancesCn(menuId) : workspaceMapper.getMenuAppInstanceEn(menuId);
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
        DWSFavorite dwsFavorite = new DWSFavorite();
        dwsFavorite.setUsername(username);
        dwsFavorite.setWorkspaceId(workspaceId);
        dwsFavorite.setMenuApplicationId(menuApplicationId);
        // todo: order will from the front end
        dwsFavorite.setOrder(1);
        dwsFavorite.setCreateBy(username);
        dwsFavorite.setLastUpdateUser(username);
        workspaceMapper.addFavorite(dwsFavorite);
        return dwsFavorite.getId();
    }

    @Override
    public Long deleteFavorite(String username, Long favouritesId) {
        workspaceMapper.deleteFavorite(favouritesId);
        return favouritesId;
    }

    private boolean isAdminUser(Long workspaceId, String username) {
        DWSWorkspace workspace = workspaceMapper.getWorkspaceById(workspaceId);
        return username != null && workspace != null && username.equals(workspace.getCreateBy());
    }


}
