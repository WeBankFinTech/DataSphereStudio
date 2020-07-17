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

package com.webank.wedatasphere.dss.oneservice.core.dao;


import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceQuery;
import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * dao
 *
 * @author zhulixin
 */
public interface OneServiceConfigDao {

    /**
     * Insert
     *
     * @param oneServiceVo oneServiceVo
     */
    void insert(OneServiceVo oneServiceVo);

    /**
     * Update
     *
     * @param oneServiceVo oneServiceVo
     */
    void update(OneServiceVo oneServiceVo);

    /**
     * query
     *
     * @param query query info
     * @return query list
     */
    List<OneServiceVo> query(OneServiceQuery query);

    /**
     * query
     *
     * @param scriptPath
     * @return data
     */
    OneServiceVo queryByScriptPathVersion(@Param("scriptPath") String scriptPath, @Param("version") String version);
    
    OneServiceVo queryByScriptPath(@Param("scriptPath") String scriptPath);

    /**
     * query
     *
     * @param path
     * @return data
     */
    OneServiceVo queryByPath(String path);

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
