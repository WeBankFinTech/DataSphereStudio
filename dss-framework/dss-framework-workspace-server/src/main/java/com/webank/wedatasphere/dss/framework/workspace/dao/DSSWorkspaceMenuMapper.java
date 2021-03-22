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


import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceComponentRolePriv;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceHomepageSetting;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * created by cooperyang on 2020/3/17
 * Description:
 */
@Mapper
public interface DSSWorkspaceMenuMapper {



    @Select("select url from dss_menu_component_url where menu_id = #{menuId}")
    String getSubMenuUrl(@Param("menuId") int menuId);


    @Select("select * from dss_component_role where workspace_id = #{workspaceId}")
    @Results({
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "componentId", column = "component_id"),
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "lastUpdateTime", column = "last_update_time"),
            @Result(property = "updateBy", column = "update_by")
    })
    List<DSSWorkspaceComponentRolePriv> getComponentRolePriv(@Param("workspaceId") int workspaceId);

    @Select("select * from dss_workspace_homepage where workspace_id = #{workspaceId}")
    @Results({
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "homepageUrl", column = "homepage_url"),
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "updateTime", column = "update_time")
    })
    List<DSSWorkspaceHomepageSetting> getWorkspaceHompageSettings(@Param("workspaceId") int workspaceId);

    @Select("select priv from dss_component_role where workspace_id = #{workspaceId} " +
            "and role_id = #{roleId} and " +
            "component_id = #{applicationId}")
    Integer getOneCompoentRolePriv(@Param("workspaceId") int workspaceId,
                                   @Param("roleId") int roleId, @Param("applicationId") int applicationId);

    /**
     * 如果在dss_component_role中是有workspace_id=-1的情况,
     * 默认是-1的时候，就要全部拿出来
     * @return
     */
    @Select("select * from dss_component_role where workspace_id = -1")
    @Results({
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "componentId", column = "component_id"),
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "lastUpdateTime", column = "last_update_time"),
            @Result(property = "updateBy", column = "update_by")
    })
    List<DSSWorkspaceComponentRolePriv> getDefaultComponentRolePriv();
}
