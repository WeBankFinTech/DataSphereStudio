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

package com.webank.wedatasphere.dss.server.service;

import com.webank.wedatasphere.dss.server.dto.response.HomepageDemoMenuVo;
import com.webank.wedatasphere.dss.server.dto.response.HomepageVideoVo;
import com.webank.wedatasphere.dss.server.dto.response.OnestopMenuVo;
import com.webank.wedatasphere.dss.server.entity.DSSWorkspace;
import com.webank.wedatasphere.dss.server.dto.response.WorkspaceDepartmentVo;
import com.webank.wedatasphere.dss.server.dto.response.*;

import java.util.List;

/**
 * Created by schumiyi on 2020/6/22
 */
public interface DSSWorkspaceService {
    List<DSSWorkspace> getWorkspaces(String userName);

    Long addWorkspace(String userName, String name, String department, String label, String description);

    boolean existWorkspaceName(String name, String username);

    List<WorkspaceDepartmentVo> getWorkSpaceDepartments();

    List<HomepageDemoMenuVo> getHomepageDemos(boolean isChinese);

    List<HomepageVideoVo> getHomepageVideos(boolean isChinese);

    List<OnestopMenuVo> getWorkspaceManagements(Long workspaceId, String username, boolean isChinese);

    List<OnestopMenuVo> getWorkspaceApplications(Long workspaceId, String username, boolean isChinese);

    DSSWorkspace getWorkspacesById(Long id);

    /**
     * 查询用户收藏的应用，如果是新用户，就在数据库给它插入默认两个收藏：脚本开发与工作流 workflow scriptis
     * @param workspaceId
     * @param username
     * @param isChinese
     * @return
     */
    List<WorkspaceFavoriteVo> getWorkspaceFavorites(Long workspaceId, String username, boolean isChinese);

    Long addFavorite(String username, Long workspaceId, Long menuApplicationId);

    Long deleteFavorite(String username, Long favouritesId);
}
