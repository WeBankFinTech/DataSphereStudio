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

package com.webank.wedatasphere.dss.framework.workspace.service;

import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceUser01;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageDemoMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageVideoVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.OnestopMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceDepartmentVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomePageVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomepageSettingVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceOverviewVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspacePrivVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceUserVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;
import org.apache.linkis.common.exception.ErrorException;

import java.util.List;


public interface DSSWorkspaceService {


    int createWorkspace(String workspaceName, String tags, String userName, String description, String department, String productName,String workspaceType) throws ErrorException;

    void addWorkspaceUser(List<Integer> roleIds, int workspaceId, String userName, String creater,String userId);

    List<DSSWorkspace> getWorkspaces(String userName);

    DSSWorkspaceHomePageVO getWorkspaceHomePage(String userName,String moduleName) throws Exception;

    List<DSSWorkspaceUser01> getWorkspaceUsers(String workspaceId, String department, String username,
                                               String roleName, int pageNow, int pageSize, List<Long> total);

    List<DSSWorkspaceRoleVO> getWorkspaceRoles(int workspaceId);

    DSSWorkspacePrivVO getWorkspaceMenuPrivs(String workspaceId);

    DSSWorkspaceOverviewVO getOverview(String username, int workspaceId, boolean isEnglish);

    DSSWorkspaceHomepageSettingVO getWorkspaceHomepageSettings(int workspaceId);

    String getWorkspaceName(String workspaceId);

    boolean checkAdmin(String userName);

    List<DepartmentVO> getDepartments();

    List<DSSWorkspaceUserVO> getWorkspaceUsersByRole(int workspaceId, String roleName, List<Long> totals,
                                                     int pageNow, int pageSize);

    List<DSSWorkspace> getWorkspaces();

    Long addWorkspace(String userName, String name, String department, String label, String description);

    boolean existWorkspaceName(String name);

    List<WorkspaceDepartmentVo> getWorkSpaceDepartments();

    List<HomepageDemoMenuVo> getHomepageDemos(boolean isChinese);

    List<HomepageVideoVo> getHomepageVideos(boolean isChinese);

    List<OnestopMenuVo> getWorkspaceManagements(Long workspaceId, String username, boolean isChinese);

    List<OnestopMenuVo> getWorkspaceApplications(Long workspaceId, String username, boolean isChinese);

    DSSWorkspace getWorkspacesById(Long id);

    List<WorkspaceFavoriteVo> getWorkspaceFavorites(Long workspaceId, String username, boolean isChinese,String type);

    Long addFavorite(String username, Long workspaceId, Long menuApplicationId,String type);

    Long deleteFavorite(String username, Long applicationId, Long workspaceId,String type);


}
