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


import com.webank.wedatasphere.dss.framework.workspace.bean.DSSUser;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * created by cooperyang on 2020/3/15
 * Description:
 */
@Mapper
public interface DSSWorkspaceUserMapper {

    Long getUserID(String userName);

    String getUserName(Long userID);

    @Insert("insert into dss_workspace_user(workspace_id, username, join_time, created_by)" +
            "values(#{workspaceId}, #{username}, now(), #{creator})")
    void insertUser(@Param("username") String username,
                    @Param("workspaceId") int workspaceId, @Param("creator") String creator);


    @Insert("insert into dss_workspace_user_role(workspace_id, username, role_id, create_time, created_by)" +
            "values(#{workspaceId}, #{username}, #{roleId}, now(), #{createdBy})")
    void setUserRoleInWorkspace(@Param("workspaceId") int workspaceId, @Param("roleId") int roleId,
                                @Param("username") String username, @Param("createdBy") String createdBy);

    @Select("select role_id from dss_workspace_user_role where workspace_id = #{workspaceId} and username = #{username}")
    List<Integer> getRoleInWorkspace(@Param("workspaceId") int workspaceId, @Param("username") String username);


    @Delete("delete from dss_workspace_user_role where username = #{username} and workspace_id = #{workspaceId}")
    void removeAllRolesForUser(@Param("username") String username, @Param("workspaceId") int workspaceId);

    @Delete("delete from dss_workspace_user where username = #{username} and workspace_id = #{workspaceId}")
    void removeUserInWorkspace(@Param("username") String username, @Param("workspaceId") int workspaceId);

    @Select("select workspace_id from dss_workspace_user where username = #{username}")
    List<Integer> getWorkspaceIds(@Param("username") String username);

    @Select("select homepage_url from dss_workspace_homepage where workspace_id = #{workspaceId} and role_id = #{roleId}")
    String getHomepageUrl(@Param("workspaceId") int workspaceId, @Param("roleId") int roleId);

    @Select("select * from dss_user")
    List<DSSUser> listAllDSSUsers();

    @Select("select username from dss_workspace_user where workspace_id = #{workspaceId}")
    List<String> getAllWorkspaceUsers(@Param("workspaceId") int workspaceId);

    @Select("select username from dss_flow_user where flow_id = #{flowId}")
    List<String> getFlowUser(@Param("flowId") Long flowId);

    @Select("select is_admin from dss_user where username = #{userName}")
    boolean isAdmin(@Param("userName") String userName);

    @Select({
            "<script>",
            "select * from dss_workspace_user where workspace_id = #{workspaceId} ",
            "<if test='username != null'>and username=#{username}</if>",
            "</script>"
    })
    @Results({
            @Result(property = "creator", column = "created_by"),
            @Result(property = "username", column = "username"),
            @Result(property = "joinTime", column = "join_time"),
            @Result(property = "workspaceId", column = "workspace_id")
    })
    List<DSSWorkspaceUser> getWorkspaceUsers(@Param("workspaceId") String workspaceId,
                                             @Param("department") String department,
                                             @Param("username") String username, @Param("roleId") int roleId);

    @Select("select * from dss_workspace_user where username in " +
            "(select username from dss_workspace_user_role where role_id = #{roleId} and workspace_id = #{workspaceId})" +
            "and workspace_id = #{workspaceId}")
    @Results({
            @Result(property = "creator", column = "created_by"),
            @Result(property = "username", column = "username"),
            @Result(property = "joinTime", column = "join_time"),
            @Result(property = "workspaceId", column = "workspace_id")
    })
    List<DSSWorkspaceUser> getWorkspaceUsersByRole(@Param("workspaceId") int workspaceId, @Param("roleId") int roleId);
}
