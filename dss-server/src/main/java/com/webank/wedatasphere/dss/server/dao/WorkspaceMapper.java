package com.webank.wedatasphere.dss.server.dao;

import com.webank.wedatasphere.dss.server.dto.response.*;
import com.webank.wedatasphere.dss.server.entity.*;
import com.webank.wedatasphere.dss.server.dto.response.HomepageDemoInstanceVo;
import com.webank.wedatasphere.dss.server.dto.response.HomepageDemoMenuVo;
import com.webank.wedatasphere.dss.server.dto.response.HomepageVideoVo;
import com.webank.wedatasphere.dss.server.dto.response.WorkspaceFavoriteVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by schumiyi on 2020/6/22
 */
public interface WorkspaceMapper {

    List<DWSWorkspace> getWorkspaces();

    List<DWSWorkspace> findByWorkspaceName(String name);

    void addWorkSpace(DWSWorkspace dwsWorkspace);

    List<HomepageDemoMenuVo> getHomepageDemoMenusEn();
    List<HomepageDemoMenuVo> getHomepageDemoMenusCn();

    List<HomepageDemoInstanceVo> getHomepageInstancesByMenuIdCn(Long id);
    List<HomepageDemoInstanceVo> getHomepageInstancesByMenuIdEn(Long id);

    List<HomepageVideoVo> getHomepageVideosEn();
    List<HomepageVideoVo> getHomepageVideosCn();

    DWSWorkspace getWorkspaceById(Long workspaceId);

    List<OnestopMenuVo> getManagementMenuCn();
    List<OnestopMenuVo> getManagementMenuEn();

    List<OnestopMenuVo> getApplicationMenuCn();
    List<OnestopMenuVo> getApplicationMenuEn();

    List<OnestopMenuAppInstanceVo> getMenuAppInstancesCn(Long id);
    List<OnestopMenuAppInstanceVo> getMenuAppInstanceEn(Long id);

    List<WorkspaceFavoriteVo> getWorkspaceFavoritesCn(@Param("username") String username, @Param("workspaceId") Long workspaceId);

    List<WorkspaceFavoriteVo> getWorkspaceFavoritesEn(@Param("username") String username, @Param("workspaceId") Long workspaceId);

    void addFavorite(DWSFavorite dwsFavorite);

    void deleteFavorite(Long favouritesId);
}
