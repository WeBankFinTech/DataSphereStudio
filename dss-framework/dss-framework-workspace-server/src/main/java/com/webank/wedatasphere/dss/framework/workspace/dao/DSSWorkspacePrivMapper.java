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

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface DSSWorkspacePrivMapper {

    @Update("update dss_workspace_menu_role set priv = #{priv}, update_time = now() " +
            "where workspace_id = #{workspaceId} and menu_id = #{menuId} and role_id = #{roleId}")
    void updateRoleMenuPriv(@Param("workspaceId") int workspaceId, @Param("menuId") int menuId,
                            @Param("roleId") int roleId, @Param("priv") int priv);

    @Select("select count(*) from dss_workspace_menu_role where workspace_id = #{workspaceId} and menu_id = #{menuId} and role_id = #{roleId}")
    int queryCntOfMenuRolePriv(@Param("workspaceId") int workspaceId, @Param("menuId") int menuId, @Param("roleId") int roleId);

    @Select("insert into dss_workspace_menu_role (`workspace_id`, `menu_id`, `role_id`, `priv`, `update_time`, `updateby`) " +
            "values(#{workspaceId}, #{menuId}, #{roleId}, #{priv}, now(), #{username})")
    void insertMenuRolePriv(@Param("workspaceId") int workspaceId, @Param("menuId") int menuId, @Param("roleId") int roleId,
                                @Param("priv") int priv,
                                @Param("username") String username);

    @Update("update dss_workspace_appconn_role set priv = #{priv} , update_time = now()" +
            "where workspace_id = #{workspaceId} and appconn_id = #{appconnId} and role_id = #{roleId}")
    void updateRoleComponentPriv(@Param("workspaceId") int workspaceId, @Param("appconnId") int appconnId,
                                 @Param("roleId") int roleId, @Param("priv") int priv);

    @Select("select id from dss_workspace_role where workspace_id = #{workspaceId} and name = #{key}")
    Integer getRoleId(@Param("workspaceId") int workspaceId, @Param("key") String key);

    @Select("select count(*) from dss_workspace_appconn_role where workspace_id = #{workspaceId} and appconn_id = #{appconnId} and role_id = #{roleId}")
    int queryCntOfRCP(@Param("workspaceId") int workspaceId, @Param("appconnId") int appconnId, @Param("roleId") int roleId);

    @Select("insert into dss_workspace_appconn_role (`workspace_id`, `appconn_id`, `role_id`, `priv`, `update_time`, `updateby`) " +
            "values(#{workspaceId}, #{appconnId}, #{roleId}, #{priv}, now(), #{username})")
    void insertRolComponentPriv(@Param("workspaceId") int workspaceId, @Param("appconnId") int appconnId, @Param("roleId") int roleId,
                                @Param("priv") int priv,
                                @Param("username") String username);
}
