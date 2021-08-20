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

package com.webank.wedatasphere.dss.standard.app.sso.origin.request

import java.util

import com.webank.wedatasphere.dss.standard.app.sso.request.{SSORequestOperation, SSORequestService}
import com.webank.wedatasphere.dss.standard.common.service.AppServiceImpl
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult


class OriginSSORequestServiceImpl extends AppServiceImpl with SSORequestService {

  private val ssoRequestServices = new util.HashMap[String, OriginSSORequestOperation]

  override def createSSORequestOperation(appName: String): SSORequestOperation[HttpAction, HttpResult] = {
    if(!ssoRequestServices.containsKey(appName)) synchronized {
      if(!ssoRequestServices.containsKey(appName)) ssoRequestServices.put(appName, new OriginSSORequestOperation(appName))
    }
    ssoRequestServices.get(appName)
  }
}
