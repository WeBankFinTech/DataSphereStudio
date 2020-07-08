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

    List<DSSWorkspace> getWorkspaces();

    List<DSSWorkspace> findByWorkspaceName(String name);

    void addWorkSpace(DSSWorkspace dssWorkspace);

    List<HomepageDemoMenuVo> getHomepageDemoMenusEn();
    List<HomepageDemoMenuVo> getHomepageDemoMenusCn();

    List<HomepageDemoInstanceVo> getHomepageInstancesByMenuIdCn(Long id);
    List<HomepageDemoInstanceVo> getHomepageInstancesByMenuIdEn(Long id);

    List<HomepageVideoVo> getHomepageVideosEn();
    List<HomepageVideoVo> getHomepageVideosCn();

    DSSWorkspace getWorkspaceById(Long workspaceId);

    List<OnestopMenuVo> getManagementMenuCn();
    List<OnestopMenuVo> getManagementMenuEn();

    List<OnestopMenuVo> getApplicationMenuCn();
    List<OnestopMenuVo> getApplicationMenuEn();

    List<OnestopMenuAppInstanceVo> getMenuAppInstancesCn(Long id);
    List<OnestopMenuAppInstanceVo> getMenuAppInstancesEn(Long id);

    List<WorkspaceFavoriteVo> getWorkspaceFavoritesCn(@Param("username") String username, @Param("workspaceId") Long workspaceId);

    List<WorkspaceFavoriteVo> getWorkspaceFavoritesEn(@Param("username") String username, @Param("workspaceId") Long workspaceId);

    void addFavorite(DSSFavorite dssFavorite);

    void deleteFavorite(Long favouritesId);
}
