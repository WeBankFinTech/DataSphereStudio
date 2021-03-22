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

package com.webank.wedatasphere.dss.apiservice.core.service;

import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceQuery;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiServiceQueryException;
import com.webank.wedatasphere.dss.apiservice.core.vo.*;

import java.util.List;

/**
 * @author zhulixin
 */
public interface ApiService {

    /**
     * Save
     *
     * @param oneService oneService info
     */
    void save(ApiServiceVo oneService) throws Exception;



    void saveByApp(ApiServiceVo apiService) throws Exception;

    /**
     * Update
     *
     * @param oneService oneService info
     */
    void update(ApiServiceVo oneService) throws Exception;

    /**
     * query
     *
     * @param apiServiceQuery
     * @return
     */
    List<ApiServiceVo> query(ApiServiceQuery apiServiceQuery) throws ApiServiceQueryException;

    List<ApiServiceVo> queryByWorkspaceId(Integer workspaceId, String userName);

    List<String> queryAllTags(String userName,Integer workspaceId);


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
    Boolean enableApi(Long id,String userName);

    /**
     * disable api
     * @param id api record id
     * @return
     */
    Boolean disableApi(Long id,String userName);

    Boolean deleteApi(Long id,String userName);

    Boolean updateComment(Long id,String comment,String userName);

    ApiServiceVo queryById(Long id,String userName);

    ApiVersionVo getMaxVersion(long serviceId);

    boolean checkUserWorkspace(String userName,Integer workspaceId);

//    List<DataMapApplyContentData> genDataMapApplyContentDatas(ApiServiceVo apiServiceVo, ApiVersionVo apiVersionVo, String metaDtaInfo);


}
