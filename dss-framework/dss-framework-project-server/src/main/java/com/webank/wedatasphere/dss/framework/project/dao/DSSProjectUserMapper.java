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

package com.webank.wedatasphere.dss.framework.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * created by v_wbzwchen  on 2020/12/16
 * Description:
 */
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

}
