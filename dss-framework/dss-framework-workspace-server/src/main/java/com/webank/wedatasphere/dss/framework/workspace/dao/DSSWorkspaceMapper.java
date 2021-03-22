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
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * created by cooperyang on 2020/3/13
 * Description:
 */
@Mapper
public interface DSSWorkspaceMapper {

    void createWorkSpace(DSSWorkspace dssWorkspace);

    List<DSSWorkspace> getWorkspaces(String username);

    List<Integer> getMenuId(int roleId, String workspaceId);

    DSSMenu getSpaceMenu(int menuId);

    List<DSSWorkspaceMenuRolePriv> getDSSWorkspaceMenuPriv(String workspaceId);

    @Insert({
            "<script>",
            "insert into dss_component_role (workspace_id, component_id, role_id, priv, update_time, updateby)",
            "values",
            "<foreach collection='privs' item='priv' open='(' separator='),(' close=')'>",
            "#{priv.workspaceId}, #{priv.componentId}, #{priv.roleId}, #{priv.priv}, #{priv.updateTime}, #{priv.updateBy}",
            "</foreach>",
            "</script>"
    })
    void setDefaultComponentRoles(@Param("privs") List<DSSWorkspaceComponentPriv> dssWorkspaceComponentPrivs);
}
