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

package com.webank.wedatasphere.dss.framework.workspace.dao;

import com.webank.wedatasphere.dss.framework.workspace.bean.*;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface DSSWorkspaceMapper {
    @Select("select name from dss_workspace where id = #{workspaceId} ")
    String getWorkspaceNameById(@Param("workspaceId") long workspaceId);

    @Select("select id from dss_workspace where name = #{workspaceName}")
    Integer getWorkspaceIdByName(@Param("workspaceName") String workspaceName);

    @Select("select * from dss_workspace where id = #{workspaceId}")
    @Results({
            @Result(property = "createBy", column = "create_by"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "lastUpdateTime", column = "last_update_time"),
            @Result(property = "lastUpdateUser", column = "last_update_user"),
    })
    DSSWorkspace getWorkspace(@Param("workspaceId") int workspaceId);

    void createWorkSpace(DSSWorkspace dssWorkspace);

    List<DSSWorkspace> getWorkspaces(String username);

    List<Long> getUserMenuAppConnId(@Param("username") String username, @Param("workspaceId") Long workspaceId);

    List<Integer> getMenuId(int roleId, String workspaceId);

    List<DSSWorkspaceMenuRolePriv> getDSSWorkspaceMenuPriv(String workspaceId);

    @Select("select -1 as workspaceId, id as menu_id, 1 as role_id, 1 as priv from dss_workspace_menu")
    @Results({
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "menuId", column = "menu_id"),
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "priv", column = "priv")
    })
    List<DSSWorkspaceMenuRolePriv> getDefaultWorkspaceMenuPriv();

    @Select("select count(1) from dss_workspace_user_favorites_appconn where menu_appconn_id=#{menuAppId} and workspace_id=#{workspaceId}" +
            " and username=#{userName} and type=#{type}")
    int getByMenuAppIdAndUser(@Param("menuAppId") Long menuAppId, @Param("workspaceId") Long workspaceId,
                              @Param("userName") String userName, @Param("type") String type);

    @Select("select id from dss_workspace_menu_appconn where title_en=#{appName}")
    Long getMenuAppIdByName(@Param("appName") String appName);

    @Insert("insert into dss_workspace_associate_departments(workspace_id,departments,role_ids,create_time,create_by) values(#{workspaceId},#{departments},#{roleIds},now(),#{user})")
    void addDepartmentsForWorkspace(@Param("workspaceId") Long workspaceId, @Param("departments") String departments,
                                    @Param("roleIds") String roleIds, @Param("user") String user);

    @Update("update dss_workspace_associate_departments set departments=#{departments},role_ids=#{roleIds},update_time=now(),update_by=#{user} where workspace_id=#{workspaceId}")
    void updateDepartmentsForWorkspace(@Param("workspaceId") Long workspaceId, @Param("departments") String departments,
                                       @Param("roleIds") String roleIds, @Param("user") String user);

    @Select("select * from dss_workspace_associate_departments where workspace_id=#{workspaceId}")
    DSSWorkspaceAssociateDepartments getAssociateDepartmentsByWorkspaceId(@Param("workspaceId") Long workspaceId);

    @Select("select * from dss_workspace_associate_departments")
    List<DSSWorkspaceAssociateDepartments> getWorkspaceAssociateDepartments();
}
