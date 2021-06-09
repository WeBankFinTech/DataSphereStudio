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

package com.webank.wedatasphere.dss.framework.workspace.dao;


import com.webank.wedatasphere.dss.framework.workspace.bean.DSSFavorite;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by schumiyi on 2020/6/22
 */
public interface WorkspaceMapper {

    List<DSSWorkspace> getWorkspaces();

    /**
     * 获取空间名称
     * @param name
     * @return
     */
    List<DSSWorkspace> findByWorkspaceName(@Param("name") String name);

    void addWorkSpace(DSSWorkspace dssWorkspace);

    List<HomepageDemoMenuVo> getHomepageDemoMenusEn();
    List<HomepageDemoMenuVo> getHomepageDemoMenusCn();

    List<HomepageDemoInstanceVo> getHomepageInstancesByMenuIdCn(Long id);
    List<HomepageDemoInstanceVo> getHomepageInstancesByMenuIdEn(Long id);

    List<HomepageVideoVo> getHomepageVideosEn();
    List<HomepageVideoVo> getHomepageVideosCn();

    DSSWorkspace getWorkspaceById(@Param("workspaceId") Long workspaceId);

    List<OnestopMenuVo> getManagementMenuCn();
    List<OnestopMenuVo> getManagementMenuEn();

    List<OnestopMenuVo> getApplicationMenuCn();
    List<OnestopMenuVo> getApplicationMenuEn();

    List<OnestopMenuAppInstanceVo> getMenuAppInstancesCn(Long id);
    List<OnestopMenuAppInstanceVo> getMenuAppInstancesEn(Long id);

    List<WorkspaceFavoriteVo> getWorkspaceFavoritesCn(@Param("username") String username, @Param("workspaceId") Long workspaceId);

    List<WorkspaceFavoriteVo> getWorkspaceFavoritesEn(@Param("username") String username, @Param("workspaceId") Long workspaceId);

    void addFavorite(DSSFavorite dssFavorite);

    void deleteFavorite(@Param("username") String username,@Param("applicationId") Long applicationId, @Param("workspaceId") Long workspaceId);
}
