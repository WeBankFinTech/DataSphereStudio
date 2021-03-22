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

import com.webank.wedatasphere.dss.framework.workspace.bean.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * created by cooperyang on 2020/3/17
 * Description:
 */
@Mapper
public interface DSSWorkspaceRoleMapper {

    @Select("select * from dss_role")
    @Results({
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "frontName", column = "front_name"),
            @Result(property = "createTime", column = "update_time")
    })
    List<DSSRole> getRoles();


    @Select("select a.`name` from dss_role a join dss_workspace_user_role b on a.id = b.role_id" +
            " and b.`username` = #{username} and b.workspace_id = #{workspaceId}")
    List<String> getAllRoles(@Param("username") String username, @Param("workspaceId") int workspaceId);


    @Select("select * from dss_menu")
    @Results({
            @Result(property = "upperMenuId", column = "upper_menu_id"),
            @Result(property = "frontName", column = "front_name"),
            @Result(property = "isActive", column = "is_active")
    })
    List<DSSMenu> getMenus();


    @Select("select * from dss_menu_component_url")
    @Results({
            @Result(property = "menuId", column = "menu_id"),
            @Result(property = "dssApplicationId", column = "dss_application_id"),
            @Result(property = "manulUrl", column = "manul_url"),
            @Result(property = "operationUrl", column = "operation_url"),
            @Result(property = "updateTime", column = "update_time"),
    })
    List<DSSWorkspaceMenuComponentUrl> getMenuComponentUrl();


    @Select("select homepage_url from dss_application where id = #{applicationId}")
    String getEntryUrl(@Param("applicationId") int applicationId);



    @Select("select * from dss_application")
    @Results(id = "dss_application_map", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "homepageUrl", column = "hoempage_url"),
            @Result(property = "componentName", column = "name")
    })
    List<DSSWorkspaceComponent> getComponents();


    @Select("select * from dss_application")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "homepageUrl", column = "homepage_url"),
            @Result(property = "projectUrl", column = "project_url")
    })
    List<DSSApplicationBean> getDSSApplications();

    @Insert("insert into dss_role(workspace_id, name, front_name, update_time) " +
            "values(#{dssRole.workspaceId}, #{dssRole.name}, #{dssRole.frontName}, #{dssRole.createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "dssRole.id", keyColumn = "id")
    int addNewRole(@Param("dssRole") DSSRole dssRole);


    @Insert({
            "<script>",
            "insert into dss_menu_role (workspace_id, menu_id, role_id, priv, updateby, update_time)",
            "values",
            "<foreach collection='menuIds' item='menuId' open='(' separator='),(' close=')'>",
            "#{workspaceId}, #{menuId}, #{roleId}, #{priv}, #{username},now()",
            "</foreach>",
            "</script>"
    })
    void updateRoleMenu(@Param("roleId") int roleId, @Param("workspaceId") int workspaceId,
                        @Param("menuIds") List<Integer> menuIds, @Param("username") String username,
                        @Param("priv") Integer priv);



    @Insert({
            "<script>",
            "insert into dss_component_role (workspace_id, component_id, role_id, priv, updateby, update_time)",
            "values",
            "<foreach collection='componentIds' item='componentId' open='(' separator='),(' close=')'>",
            "#{workspaceId}, #{componentId}, #{roleId}, #{priv}, #{username}, now()",
            "</foreach>",
            "</script>"
    })
    void updateRoleComponent(@Param("roleId") int roleId, @Param("workspaceId") int workspaceId,
                             @Param("componentIds") List<Integer> componentIds, @Param("username") String username,
                             @Param("priv") Integer priv);


    @Select("select workspace_id from dss_workspace_user where username = #{username}")
    List<Integer> getWorkspaceIds(@Param("username") String username);

    @Select("select workspace_id from dss_workspace where `name` = #{defaultWorkspaceName}")
    Integer getDefaultWorkspaceId(@Param("defaultWorkspaceName") String defaultWorkspaceName);

    @Select("select id from dss_role where workspace_id = #{workspaceId} and name = #{apiUser}")
    int getRoleId(@Param("apiUser") String apiUser, @Param("workspaceId") int workspaceId);

    @Select("Select count(*) from dss_component_role where workspace_id = #{workspaceId} and role_id = #{roleId} and component_id = #{componentId}")
    int getCount(@Param("workspaceId") Integer workspaceId, @Param("componentId") int componentId, @Param("roleId") int roleId);

    @Select("select priv from dss_component_role where workspace_id = #{workspaceId} and role_id = #{roleId} and " +
            "component_id = #{componentId}")
    Integer getPriv(@Param("workspaceId") Integer workspaceId, @Param("roleId") int roleId, @Param("componentId") int componentId);

    @Select("select id from dss_application where `name` = #{appName}")
    int getComponentId(@Param("appName") String appName);
}
