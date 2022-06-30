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

package com.webank.wedatasphere.dss.standard.app.sso;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOBuilderService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOBuilderServiceImplImpl;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOPluginService;
import com.webank.wedatasphere.dss.standard.app.sso.user.SSOUserService;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

/**
 * DSS 与 第三方系统的 SSO 免登录跳转规范，已提供默认实现，用户无需实现该 AppConn 的任何方法。
 */
public interface SSOIntegrationStandard extends AppStandard {

    default SSOBuilderService getSSOBuilderService() {
        return SSOBuilderServiceImplImpl.getSSOBuilderService();
    }

    /**
     * 提供通用的、可以向与 DSS 集成的第三方 AppConn 系统发送前端或后台请求的服务能力。
     * @return SSORequestService 实现类
     */
    SSORequestService getSSORequestService();

    SSOPluginService getSSOPluginService();

    /**
     * DSS 用户与第三方 AppConn 的用户同步服务
     * @return SSOUserService 实现类
     */
    SSOUserService getSSOUserService(AppInstance appInstance);

    @Override
    default String getStandardName() {
        return "ssoIntegrationStandard";
    }

    @Override
    default int getGrade() {
        return 1;
    }

    @Override
    default boolean isNecessary() {
        return true;
    }
}
