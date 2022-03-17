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


class OriginSSOIntegrationStandard private[origin]() extends HttpSSOIntegrationStandard {

  override protected def createSSORequestService(): SSORequestService = new OriginSSORequestServiceImpl

}

// SSO 工厂，通过该工程获取此默认的 sso 集成规范
class OriginSSOIntegrationStandardFactory extends SSOIntegrationStandardFactory {

  private val ssoIntegrationStandard = new OriginSSOIntegrationStandard

  override def init(): Unit = {
    ssoIntegrationStandard.init()
  }

  override def getSSOIntegrationStandard: SSOIntegrationStandard = ssoIntegrationStandard
}