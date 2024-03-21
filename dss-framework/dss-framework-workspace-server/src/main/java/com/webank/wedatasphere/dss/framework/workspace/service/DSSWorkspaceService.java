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

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSUserRoleComponentPriv;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceAssociateDepartments;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceDepartmentVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomePageVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomepageSettingVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceOverviewVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspacePrivVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceUserVO;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import org.apache.linkis.common.exception.ErrorException;

import java.util.List;


public interface DSSWorkspaceService {


    int createWorkspace(String workspaceName, String tags, String userName, String description, String department, String productName,String workspaceType) throws ErrorException;


    List<DSSWorkspace> getWorkspaces(String userName);

    DSSWorkspaceHomePageVO getWorkspaceHomePage(String userName,String moduleName) throws DSSErrorException;

    List<DSSWorkspaceUserVO> getWorkspaceUsers(String workspaceId, String department, String username,
                                               String roleName, int pageNow, int pageSize, List<Long> total);

    /**
     * 获取空间内所有成员，已用户名返回
     */
    List<String> getWorkspaceUsers(String workspaceId);

    List<DSSWorkspaceRoleVO> getWorkspaceRoles(int workspaceId);

    DSSWorkspacePrivVO getWorkspaceMenuPrivs(String workspaceId);

    DSSWorkspaceOverviewVO getOverview(String username, int workspaceId, boolean isEnglish);

    DSSWorkspaceHomepageSettingVO getWorkspaceHomepageSettings(int workspaceId);

    String getWorkspaceName(Long workspaceId);

    /**
     * 工作空间管理员权限判断(is_admin=1的用户才拥有工作空间操作权限)
     * @param userName
     * @return
     */
    boolean checkAdmin(String userName);

    /**
     * 判断是否拥有该工作空间管理员权限
     * @param workspaceId
     * @param username
     * @return
     */
    public boolean isAdminUser(Long workspaceId, String username);

    List<String> getAllDepartmentWithOffices();

    void associateDepartments(Long workspaceId, String departments, String roles,String user) throws DSSErrorException;

    DSSWorkspaceAssociateDepartments getAssociateDepartmentsInfo(Long workspaceId);

    List<DSSWorkspaceUserVO> getWorkspaceUsersByRole(int workspaceId, String roleName, List<Long> totals,
                                                     int pageNow, int pageSize);

    Long addWorkspace(String userName, String name, String department, String label, String description);

    boolean existWorkspaceName(String name);

    List<WorkspaceDepartmentVo> getWorkSpaceDepartments();

    List<WorkspaceMenuVo> getWorkspaceAppConns(Workspace workspace, Long workspaceId,
                                               String username, boolean isChinese) throws DSSErrorException;

    DSSWorkspace getWorkspacesById(Long id, String username) throws DSSErrorException;

    DSSWorkspace getWorkspacesByName(String workspaceName, String username) throws DSSErrorException;

    List<WorkspaceFavoriteVo> getWorkspaceFavorites(Long workspaceId, String username, boolean isChinese,String type);

    Long addFavorite(String username, Long workspaceId, Long menuApplicationId,String type);

    Long deleteFavorite(String username, Long appconnId, Long workspaceId,String type);


    boolean checkAdminByWorkspace(String username, int workspaceId);

    PageInfo<DSSUserRoleComponentPriv> getAllUserPrivs(Integer currentPage, Integer pageSize);
}
