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

package com.webank.wedatasphere.dss.standard.app.sso.request;

import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;

/**
 * 向与 DSS 集成的第三方 AppConn 系统发送前端或后台请求。
 * @param <T>
 * @param <R>
 */
public interface SSORequestOperation<T, R> extends Operation {

    /**
     * 提供通用的、可以向与 DSS 集成的第三方 AppConn 系统发送前端或后台请求的操作能力。
     * @param urlBuilder 向第三方 AppConn 系统发起请求的 URL 构造器
     * @param req Http 请求实体
     * @return Http 请求结果
     * @throws AppStandardErrorException Http 请求失败则抛出该异常
     */
    R requestWithSSO(SSOUrlBuilderOperation urlBuilder, T req) throws AppStandardErrorException;

}
