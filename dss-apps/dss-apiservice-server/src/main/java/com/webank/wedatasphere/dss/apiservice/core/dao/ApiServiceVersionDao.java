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


import com.webank.wedatasphere.dss.apiservice.core.vo.ApiVersionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jinyangrao
 */
public interface ApiServiceVersionDao {

    /**
     *  inster apiversionVo
     * */
    void insert(ApiVersionVo apiVersionVo);

    /**
     *  query version
     * */
    ApiVersionVo queryApiVersionByVersionId(@Param("id") Long apiVersionId);

    List<ApiVersionVo> queryApiVersionByApiServiceId(@Param("api_id") Long apiServiceId);

    /**
     *  update api version
     * */
    void updateApiVersionStatusById(@Param("id") Long apiVersionId, @Param("status") int status);

    void updateApiVersionStatus(ApiVersionVo apiVersionVo);

    /**
     *  update all api version status by service id
     * */
    void updateAllApiVersionStatusByApiServiceId(@Param("api_id") Long apiServiceId, @Param("status") int status);

    void disableApiVersionStatusByApiId(@Param("api_id") Long apiServiceId);

    /**
     * delete api version
     * */
    void deleteApiVersionById(@Param("id") Long apiVersionId);
}
