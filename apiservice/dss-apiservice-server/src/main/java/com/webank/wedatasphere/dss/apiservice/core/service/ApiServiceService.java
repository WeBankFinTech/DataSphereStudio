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

package com.webank.wedatasphere.dss.apiservice.core.service;

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceQuery;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceVo;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

/**
 * @author zhulixin
 */
public interface ApiServiceService {

    /**
     * Save
     *
     * @param apiService apiService info
     */
    void save(ApiServiceVo apiService) throws ErrorException;

    /**
     * Update
     *
     * @param apiService apiService info
     */
    void update(ApiServiceVo apiService) throws ErrorException;

    /**
     * query
     *
     * @param apiServiceQuery
     * @return
     */
    PageInfo<ApiServiceVo> query(ApiServiceQuery apiServiceQuery);

    /**
     * query
     *
     * @param scriptPath
     * @return
     */
    ApiServiceVo queryByScriptPath(String scriptPath);

    Integer queryCountByPath(String scriptPath, String path);

    Integer queryCountByName(String name);

    /**
     * enable api
     * @param id api record id
     * @return
     */
    Boolean enableApi(Integer id);

    /**
     * disable api
     * @param id api record id
     * @return
     */
    Boolean disableApi(Integer id);
}
