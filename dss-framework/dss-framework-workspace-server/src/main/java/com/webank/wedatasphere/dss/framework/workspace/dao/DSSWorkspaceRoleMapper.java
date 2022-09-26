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
public interface DSSWorkspaceRoleMapper {

    @Select("select * from dss_workspace_role")
    @Results({
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "frontName", column = "front_name"),
            @Result(property = "createTime", column = "update_time")
    })
    List<DSSWorkspaceRole> getRoles();


    @Select("select a.`name` from dss_workspace_role a join dss_workspace_user_role b on a.id = b.role_id" +
            " and b.`username` = #{username} and b.workspace_id = #{workspaceId}")
    List<String> getAllRoles(@Param("username") String username, @Param("workspaceId") int workspaceId);


    @Select("select * from dss_workspace_menu")
    @Results({
            @Result(property = "titleCn", column = "title_cn"),
            @Result(property = "titleEn", column = "title_En")
    })
    List<DSSWorkspaceMenu> getWorkspaceMenus();


    @Select("select a.id id, a.appconn_name name,b.url url, b.homepage_uri homepage_uri, b.label label from dss_appconn a join dss_appconn_instance b on a.id=b.appconn_id")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "url", column = "url"),
            @Result(property = "homepageUri", column = "homepage_uri"),
            @Result(property = "label", column = "label")
    })
    List<DSSApplicationBean> getDSSAppConns();

    @Insert("insert into dss_workspace_role(workspace_id, name, front_name, update_time) " +
            "values(#{dssRole.workspaceId}, #{dssRole.name}, #{dssRole.frontName}, #{dssRole.createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "dssRole.id", keyColumn = "id")
    int addNewRole(@Param("dssRole") DSSWorkspaceRole dssRole);


    @Insert({
            "<script>",
            "insert into dss_workspace_menu_role (workspace_id, menu_id, role_id, priv, updateby, update_time)",
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
            "insert into dss_workspace_appconn_role (workspace_id, appconn_id, role_id, priv, updateby, update_time)",
            "values",
            "<foreach collection='componentIds' item='componentId' open='(' separator='),(' close=')'>",
            "#{workspaceId}, #{componentId}, #{roleId}, #{priv}, #{username}, now()",
            "</foreach>",
            "</script>"
    })
    void updateRoleComponent(@Param("roleId") int roleId, @Param("workspaceId") int workspaceId,
                             @Param("componentIds") List<Integer> componentIds, @Param("username") String username,
                             @Param("priv") Integer priv);


    @Select("select distinct workspace_id from dss_workspace_user_role where username = #{username}")
    List<Integer> getWorkspaceIds(@Param("username") String username);

    @Select("select workspace_id from dss_workspace where `name` = #{defaultWorkspaceName}")
    Integer getDefaultWorkspaceId(@Param("defaultWorkspaceName") String defaultWorkspaceName);

    @Select("select id from dss_workspace_role where workspace_id = #{workspaceId} and name = #{apiUser}")
    Integer getRoleId(@Param("apiUser") String apiUser, @Param("workspaceId") int workspaceId);

    @Select("Select count(*) from dss_workspace_appconn_role where workspace_id = #{workspaceId} and role_id = #{roleId} and appconn_id = #{componentId}")
    int getCount(@Param("workspaceId") Integer workspaceId, @Param("componentId") int componentId, @Param("roleId") int roleId);

    @Select("select priv from dss_workspace_appconn_role where workspace_id = #{workspaceId} and role_id = #{roleId} and " +
            "appconn_id = #{componentId}")
    Integer getPriv(@Param("workspaceId") Integer workspaceId, @Param("roleId") int roleId, @Param("componentId") int componentId);

}
