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


import com.webank.wedatasphere.dss.standard.common.service.AppService;

/**
 * 提供通用的、可以向与 DSS 集成的第三方 AppConn 系统发送前端或后台请求的服务能力。
 * <br/>
 * 强烈推荐不要修改 AbstractOnlySSOAppConn 的体系结构，直接使用 DSS 系统默认提供的 OriginSSORequestServiceImpl
 */
public interface SSORequestService extends AppService {

    SSORequestOperation createSSORequestOperation(String appName);

}
