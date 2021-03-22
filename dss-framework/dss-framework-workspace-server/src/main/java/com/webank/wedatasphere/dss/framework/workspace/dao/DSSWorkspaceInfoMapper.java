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


import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import org.apache.ibatis.annotations.*;

/**
 * created by cooperyang on 2020/3/18
 * Description:
 */
@Mapper
public interface DSSWorkspaceInfoMapper {

    @Select("select name from dss_workspace where id = #{workspaceId} ")
    String getWorkspaceNameById(@Param("workspaceId") int workspaceId);

    @Select("select id from dss_workspace where name = #{workspaceName}")
    int getWorkspaceIdByName(@Param("workspaceName") String workspaceName);

    @Select("select * from dss_workspace where id = #{workspaceId}")
    @Results({
            @Result(property = "createBy", column = "create_by"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "lastUpdateTime", column = "last_update_time"),
            @Result(property = "lastUpdateUser", column = "last_update_user"),
    })
    DSSWorkspace getWorkspace(@Param("workspaceId") int workspaceId);
}
