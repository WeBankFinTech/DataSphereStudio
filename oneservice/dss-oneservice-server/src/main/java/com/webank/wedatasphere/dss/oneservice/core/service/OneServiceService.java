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

package com.webank.wedatasphere.dss.oneservice.core.service;

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceQuery;
import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceVo;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceQuery;
import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceVo;

/**
 * @author zhulixin
 */
public interface OneServiceService {

    /**
     * Save
     *
     * @param oneService oneService info
     */
    void save(OneServiceVo oneService) throws ErrorException;

    /**
     * Update
     *
     * @param oneService oneService info
     */
    void update(OneServiceVo oneService) throws ErrorException;

    /**
     * query
     *
     * @param oneServiceQuery
     * @return
     */
    PageInfo<OneServiceVo> query(OneServiceQuery oneServiceQuery);

    /**
     * query
     *
     * @param scriptPath
     * @return
     */
    OneServiceVo queryByScriptPath(String scriptPath);

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
