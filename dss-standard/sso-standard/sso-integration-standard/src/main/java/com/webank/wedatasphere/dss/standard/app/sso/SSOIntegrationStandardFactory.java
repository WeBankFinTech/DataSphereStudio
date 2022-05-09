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

import java.io.Serializable;

/**
 * DSS SSO 一级规范工厂，通过该工厂可获取 DSS 的一级 SSO 免密互通规范，{@code AbstractOnlySSOAppConn} 会使用该工厂
 * 来加载 {@code SSOIntegrationStandard}。
 * 已提供了默认的实现类 {@code OriginSSOIntegrationStandardFactory}，为 DSS 默认提供、且强烈推荐使用的实现类。
 * <br/>
 * 如有特殊原因，如第三方系统为 Python 系统，用户无法对接 DSS 一级规范，
 * 只能通过 token 的方式访问第三方系统，则推荐用户重写 SSOIntegrationStandardFactory。
 * 如何重写？请实现一个 {@code SSOIntegrationStandardFactory} 的实现类即可，DSS 框架会自动找到这个实现类，
 * 并加载您指定的 {@code SSOIntegrationStandard}（推荐直接返回 {@code HttpSSOIntegrationStandard}）。
 */
public interface SSOIntegrationStandardFactory extends Serializable {

    void init();

    SSOIntegrationStandard getSSOIntegrationStandard();

}
