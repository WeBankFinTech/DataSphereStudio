/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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
package com.webank.wedatasphere.dss.oneservice.core.service;

import com.webank.wedatasphere.dss.oneservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.dss.oneservice.core.vo.TestParamVo;
import com.webank.wedatasphere.dss.oneservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.dss.oneservice.core.vo.TestParamVo;

import java.util.List;
import java.util.Map;

/**
 * 接口调用service
 *
 * @author lidongzhang
 */
public interface OneServiceQueryService {
    /**
     * 执行查询
     *
     * @param path       path
     * @param params     params
     * @param moduleName moduleName
     * @param httpMethod httpMethod
     * @return 查询接口
     */
    List<Map<String, Object>> query(String path, Map<String, Object> params, String moduleName, String httpMethod);

    List<TestParamVo> query(String scriptPath, String version);

    /**
     * 查询api 版本信息
     *
     * @param scriptPath
     * @return
     */
    List<ApiVersionVo> queryApiVersion(String scriptPath);
}
