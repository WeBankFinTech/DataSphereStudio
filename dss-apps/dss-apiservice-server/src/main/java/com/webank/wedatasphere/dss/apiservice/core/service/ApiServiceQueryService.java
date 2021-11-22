/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.webank.wedatasphere.dss.apiservice.core.service;

import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceJob;
import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceToken;
import com.webank.wedatasphere.dss.apiservice.core.bo.LinkisExecuteResult;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiServiceQueryException;
import com.webank.wedatasphere.dss.apiservice.core.vo.*;

import java.util.List;
import java.util.Map;


public interface ApiServiceQueryService {
    /**
     * 执行查询
     *
     * @param path       path
     * @param params     params
     * @param moduleName moduleName
     * @param httpMethod httpMethod
     * @return 查询接口
     */
    LinkisExecuteResult query(String path, Map<String, Object> params, String moduleName, String httpMethod, ApiServiceToken tokenDetail, String loginUser);

    List<QueryParamVo> queryParamList(String scriptPath, Long versionId);

    /**
     * 查询api 版本信息
     *
     * @param serviceId
     * @return
     */
    List<ApiVersionVo> queryApiVersionById(Long  serviceId);

    ApiServiceVo queryByVersionId(String userName,Long versionId) throws ApiServiceQueryException;

    ApiServiceJob getJobByTaskId(String taskId);

}
