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


import com.webank.wedatasphere.dss.standard.app.sso.{SSOIntegrationStandard, SSOIntegrationStandardFactory}
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.sso.origin.plugin.OriginSSOPluginServiceImpl
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestServiceImpl
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOPluginService
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc


class OriginSSOIntegrationStandard private[origin]() extends SSOIntegrationStandard {

  private val ssoPluginService: SSOPluginService = new OriginSSOPluginServiceImpl
  private val ssoRequestService: SSORequestService = new OriginSSORequestServiceImpl

  override def getSSORequestService: SSORequestService = ssoRequestService

  override def getSSOPluginService: SSOPluginService = ssoPluginService

  override def init(): Unit = {
    ssoPluginService.setSSOBuilderService(getSSOBuilderService)
  }

  override def close(): Unit = HttpClient.close()
}

// SSO 工厂，通过该工程获取sso集成规范
class OriginSSOIntegrationStandardFactory extends SSOIntegrationStandardFactory {

  private val ssoIntegrationStandard = new OriginSSOIntegrationStandard

  override def init(): Unit = {
    ssoIntegrationStandard.init()
  }

  override def getSSOIntegrationStandard: SSOIntegrationStandard = ssoIntegrationStandard
}