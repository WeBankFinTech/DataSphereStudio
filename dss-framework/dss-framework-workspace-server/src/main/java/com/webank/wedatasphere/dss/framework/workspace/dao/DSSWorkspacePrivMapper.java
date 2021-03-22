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

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * created by cooperyang on 2020/3/18
 * Description:
 */
@Mapper
public interface DSSWorkspacePrivMapper {

    @Update("update dss_menu_role set priv = #{priv}, update_time = now() " +
            "where workspace_id = #{workspaceId} and menu_id = #{menuId} and role_id = #{roleId}")
    void updateRoleMenuPriv(@Param("workspaceId") int workspaceId, @Param("menuId") int menuId,
                            @Param("roleId") int roleId, @Param("priv") int priv);

    @Update("update dss_component_role set priv = #{priv} , update_time = now()" +
            "where workspace_id = #{workspaceId} and component_id = #{componentId} and role_id = #{roleId}")
    void updateRoleComponentPriv(@Param("workspaceId") int workspaceId, @Param("componentId") int componentId,
                                 @Param("roleId") int roleId, @Param("priv") int priv);

    @Select("select id from dss_role where workspace_id = #{workspaceId} and name = #{key}")
    Integer getRoleId(@Param("workspaceId") int workspaceId, @Param("key") String key);

    @Select("select count(*) from dss_component_role where workspace_id = #{workspaceId} and component_id = #{componentId} and role_id = #{roleId}")
    int queryCntOfRCP(@Param("workspaceId") int workspaceId, @Param("componentId") int componentId, @Param("roleId") int roleId);

    @Select("insert into dss_component_role (`workspace_id`, `component_id`, `role_id`, `priv`, `update_time`, `updateby`) " +
            "values(#{workspaceId}, #{componentId}, #{roleId}, #{priv}, now(), 'cooperyang')")
    void insertRolComponentPriv(@Param("workspaceId") int workspaceId, @Param("componentId") int componentId, @Param("roleId") int roleId,
                                @Param("priv") int priv);
}
