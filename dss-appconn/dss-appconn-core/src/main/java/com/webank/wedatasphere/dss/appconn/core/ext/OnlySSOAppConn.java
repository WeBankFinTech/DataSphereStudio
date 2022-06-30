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

package com.webank.wedatasphere.dss.appconn.core.ext;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard;

/**
 * 如果您的第三方系统想与 DSS 完成 SSO 免登录跳转，则需使用该 AppConn 提供的能力。
 *
 * {@code OnlySSOAppConn} 提供了默认的抽象类 {@code AbstractOnlySSOAppConn}，该抽象类已提供了一级规范的默认实现。
 *
 * 请注意：一般情况下，相关的 {@code AppConn} 子类会主动继承该抽象类，因此您无需实现 {@code OnlySSOAppConn} 的任何方法。
 *
 * {@code OnlySSOAppConn} 的核心，是需要第三方 AppConn 按照要求引入 DSS 的 SSO Jar 包，完成相关接口的代码实现和引入。
 *
 * <br/>
 *
 * 考虑到一种特殊情况：如果您的第三方系统只想与 DSS 实现 SSO 免登录跳转，那您完全无需重新写一个 OnlySSOAppConn 实现，
 * 只需直接使用 {@code SSOAppConn}即可。您只需在 DSS 的 dss_appconn 表中新增一条记录，将 reference 字段指定为
 * sso 即可。更多关于 dss_appconn 表的介绍，请参考：第三方系统接入 DSS 开发指南#331-dss_appconn-表。
 */
public interface OnlySSOAppConn extends AppConn {

    SSOIntegrationStandard getOrCreateSSOStandard();

}
