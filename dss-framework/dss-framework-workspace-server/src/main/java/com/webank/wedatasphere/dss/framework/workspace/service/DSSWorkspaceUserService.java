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


import com.webank.wedatasphere.dss.common.entity.PageInfo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentUserVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.StaffInfoVO;

import java.util.List;


public interface DSSWorkspaceUserService {
    /**
     * 在工作空间内添加用户
     * @param roleIds 角色
     * @param workspaceId 空间id
     * @param userName 用户名
     * @param creater 添加人
     * @param userId
     */

    void addWorkspaceUser(List<Integer> roleIds, long workspaceId, String userName, String creater, String userId);

    /**
     * 更新用户在某个工作空间的角色。采用的方法是删除所有旧角色，再重新添加新角色。
     * @param roles 新角色列表
     * @param workspaceId 工作空间id
     * @param userName 用户名
     * @param creator 操作者
     */
    void updateWorkspaceUser(List<Integer> roles, long workspaceId, String userName, String creator);

    void deleteWorkspaceUser(String userName, int workspaceId);

    List<StaffInfoVO> listAllDSSUsers();

    List<DepartmentUserVo> getAllWorkspaceUsers(long workspaceId);
    PageInfo<String> getAllWorkspaceUsersPage(long workspaceId, Integer pageNow, Integer pageSize);

    List<Integer> getUserWorkspaceIds(String userName);

    List<String> getWorkspaceEditUsers(int workspaceId);

    List<String> getWorkspaceReleaseUsers(int workspaceId);

    Long getCountByUsername(String username,int workspaceId);
    Long getUserCount(long workspaceId);

    List<DSSWorkspaceRoleVO> getUserRoleByUserName(String userName);

    void clearUserByUserName(String userName);

    void revokeUserRoles(String userName, Integer[] workspaceIds, Integer[] roleIds);
}
