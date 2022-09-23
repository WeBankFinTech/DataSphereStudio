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

package com.webank.wedatasphere.dss.framework.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface DSSProjectUserMapper extends BaseMapper<DSSProjectUser> {
    @Insert({
            "<script>",
            "insert into dss_project_user",
            "(project_id, username, workspace_id, priv, last_update_time)",
            "values",
            "<foreach collection='list' item='item' open='(' separator='),(' close=')'>",
            " #{item.projectId}, #{item.username}, #{item.workspaceId}, #{item.priv}, now()",
            "</foreach>",
            "</script>"
    })
    void insertBatchProjectUser(@Param("list") List<DSSProjectUser> projectUserList);

    @Delete("delete from dss_project_user where project_id = #{projectID}")
    void deleteAllPriv(@Param("projectID") long projectID);

    @Select("SELECT COUNT(0) FROM dss_workspace_user_role WHERE workspace_id = #{workspaceId} AND username = #{username} AND role_id = #{roleId} ")
    Long isAdminByUsername(@Param("workspaceId")Long workspaceId,@Param("username")String username,@Param("roleId")int roleId);
    List<Long> getUserWorkspaceAdminRole(@Param("workspaceId") Long workspaceId, @Param("username") String username);


    @Select("SELECT COUNT(0) FROM dss_workspace_user_role WHERE workspace_id = #{workspaceId} AND username = #{username}")
    Long isWorkspaceUser(@Param("workspaceId")Long workspaceId,@Param("username")String username);

    List<DSSProjectUser> getPrivsByProjectId(@Param("projectId") Long projectId);

    @Select("SELECT admin_permission FROM dss_workspace WHERE id = #{id}")
    Integer getWorkspaceAdminPermission(@Param("id") Long id);
}
