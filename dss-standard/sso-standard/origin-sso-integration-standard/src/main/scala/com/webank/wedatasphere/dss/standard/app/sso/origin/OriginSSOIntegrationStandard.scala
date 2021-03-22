/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.standard.app.sso.origin


import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.sso.origin.plugin.OriginSSOPluginServiceImpl
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestServiceImpl
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOPluginService
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc

/**
  * Created by enjoyyin on 2020/9/3.
  */
class OriginSSOIntegrationStandard private() extends SSOIntegrationStandard {

  private var appDesc: AppDesc = _
  private val ssoPluginService: SSOPluginService = new OriginSSOPluginServiceImpl
  private val ssoRequestService: SSORequestService = new OriginSSORequestServiceImpl

  override def getSSORequestService: SSORequestService = ssoRequestService

  override def getSSOPluginService: SSOPluginService = ssoPluginService

  override def getAppDesc: AppDesc = appDesc

  override def setAppDesc(appDesc: AppDesc): Unit = this.appDesc = appDesc

  override def init(): Unit = {
    ssoPluginService.setSSOBuilderService(getSSOBuilderService)
  }

  override def close(): Unit = HttpClient.close()
}
object OriginSSOIntegrationStandard {
  private val ssoIntegrationStandard = new OriginSSOIntegrationStandard
  ssoIntegrationStandard.init()
  def getSSOIntegrationStandard: SSOIntegrationStandard = ssoIntegrationStandard
}