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

package com.webank.wedatasphere.dss.apiservice.core.dao;


import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceQuery;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jinyangrao
 */
public interface ApiServiceDao {

    /**
     * Insert
     */
    void insert(ApiServiceVo apiServiceVo);

    /**
     * Update
     */
    void update(ApiServiceVo apiServiceVo);


    void updateToTarget(ApiServiceVo apiServiceVo);

    /**
     * query
     */
    List<ApiServiceVo> query(ApiServiceQuery query);

    /**
     * query
     */

    List<ApiServiceVo> queryByScriptPath(@Param("scriptPath") String scriptPath);

    /**
     * query
     */
    ApiServiceVo queryByPath(String path);

    /**
     * query api path count
     */
    Integer queryCountByPath(@Param("scriptPath") String scriptPath, @Param("path") String path);

    /**
     * query api name
     */
    Integer queryCountByName(String name);
    
    Integer enableApi(@Param("id") Long id);
    
    Integer disableApi(@Param("id") Long id);

    Integer deleteApi(@Param("id") Long id);

    List <String> queryAllTags(@Param("userName")String userName,@Param("workspaceId")Integer workspaceId);

    ApiServiceVo queryById(@Param("id") Long id);

    List<ApiServiceVo> queryByWorkspaceId(@Param("workspaceId") Integer workspaceId, @Param("userName") String userName);

    Integer updateApiServiceComment(@Param("id") Long id, @Param("comment") String comment);
}
