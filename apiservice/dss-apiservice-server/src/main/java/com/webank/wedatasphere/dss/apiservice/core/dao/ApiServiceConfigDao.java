/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.dss.apiservice.core.dao;


import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceQuery;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * dao
 *
 * @author zhulixin
 */
public interface ApiServiceConfigDao {

    /**
     * Insert
     *
     * @param apiServiceVo apiServiceVo
     */
    void insert(ApiServiceVo apiServiceVo);

    /**
     * Update
     *
     * @param apiServiceVo apiServiceVo
     */
    void update(ApiServiceVo apiServiceVo);

    /**
     * query
     *
     * @param query query info
     * @return query list
     */
    List<ApiServiceVo> query(ApiServiceQuery query);

    /**
     * query
     *
     * @param scriptPath
     * @return data
     */
    ApiServiceVo queryByScriptPathVersion(@Param("scriptPath") String scriptPath, @Param("version") String version);
    
    ApiServiceVo queryByScriptPath(@Param("scriptPath") String scriptPath);

    /**
     * query
     *
     * @param path
     * @return data
     */
    ApiServiceVo queryByPath(String path);

    /**
     * query api path count
     *
     * @param scriptPath
     * @param path
     * @return
     */
    Integer queryCountByPath(@Param("scriptPath") String scriptPath, @Param("path") String path);

    /**
     * query api name
     * @param name
     * @return
     */
    Integer queryCountByName(String name);
    
    Integer enableApi(@Param("id") Integer id);
    
    Integer disableApi(@Param("id") Integer id);
}
