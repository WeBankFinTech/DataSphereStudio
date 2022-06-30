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

package com.webank.wedatasphere.dss.standard.app.sso.origin


import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestServiceImpl
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService
import com.webank.wedatasphere.dss.standard.app.sso.{SSOIntegrationStandard, SSOIntegrationStandardFactory}

/**
 * OriginSSOIntegrationStandard 与 HttpSSOIntegrationStandard 的唯一区别，在于 SSORequestService 的不同。
 *
 * 请注意，DSS 框架强烈建议集成的第三方 AppConn 打通一级规范，并强烈推荐使用本一级规范实现类。
 *
 * 1. OriginSSORequestServiceImpl：通常用于请求与 DSS 打通了一级规范的第三方系统，推荐使用；
 * 2. HttpSSORequestServiceImpl：用于通过使用类似 token 的方式，访问没有打通一级规范的第三方系统，不推荐使用。
 *
 */
class OriginSSOIntegrationStandard private[origin]() extends HttpSSOIntegrationStandard {

  override protected def createSSORequestService(): SSORequestService = new OriginSSORequestServiceImpl

}

/**
 * 默认的 SSO 规范工厂，通过该工厂可获取默认的 sso 集成规范 OriginSSOIntegrationStandard。
 * <br/>
 * 为 DSS 框架强烈推荐使用的 一级规范 实现类，非特殊原因，如第三方系统为 Python 系统，用户无法对接 DSS 一级规范，
 * 只能通过 token 的方式访问第三方系统，否则不推荐用户重写 SSOIntegrationStandardFactory。
 * 如何重写？请实现一个 {@code SSOIntegrationStandardFactory} 的实现类即可，DSS 框架会自动找到这个实现类，
 * 并加载您指定的 {@code SSOIntegrationStandard}（推荐直接返回 {@code HttpSSOIntegrationStandard}）
 *
 */
class OriginSSOIntegrationStandardFactory extends SSOIntegrationStandardFactory {

  private val ssoIntegrationStandard = new OriginSSOIntegrationStandard

  override def init(): Unit = {
    ssoIntegrationStandard.init()
  }

  override def getSSOIntegrationStandard: SSOIntegrationStandard = ssoIntegrationStandard
}