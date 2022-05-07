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

package com.webank.wedatasphere.dss.standard.app.sso.plugin;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOBuilderService;
import com.webank.wedatasphere.dss.standard.common.service.AppService;
import java.io.Closeable;

/**
 * 与 `SSOBuilderService` 对应，第三方系统在引入 DSS SSO Jar 后，
 * 该Jar 会使用该 Service 解析第三方系统的后台 `HttpServletRequest`，获取所需的 DSS 实体信息
 */
public interface SSOPluginService extends AppService, Closeable {

    void setSSOBuilderService(SSOBuilderService ssoBuilderService);

    SSOMsgParseOperation createSSOMsgParseOperation();

    WorkspacePlugin createWorkspacePluginOperation();

    default DssMsgCacheOperation createDssMsgCacheOperation() {
        return DssMsgCacheOperationImpl.getDssMsgCacheOperation();
    }

}
