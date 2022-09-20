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

package com.webank.wedatasphere.dss.standard.app.sso.origin.plugin

import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation.DSSMsg
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.sso.plugin.AbstractSSOMsgParseOperation
import com.webank.wedatasphere.dss.standard.sso.utils.ProxyUserSSOUtils
import org.apache.linkis.common.utils.Utils


class OriginSSOMsgParseOperation extends AbstractSSOMsgParseOperation {

  override protected def getUser(dssMsg: DSSMsg): String = {
    val dssUrl = dssMsg.getDSSUrl
    val dwsHttpClient = HttpClient.getHttpClient(dssUrl, "DSS")
    val userInfoAction = if(ProxyUserSSOUtils.existsProxyUser(dssMsg)) new ProxyUserInfoAction
      else new UserInfoAction
    Utils.tryFinally {
      HttpClient.addCookies(dssMsg, userInfoAction)
      dwsHttpClient.execute(userInfoAction) match {
        case userInfoResult: UserInfoResult =>
          userInfoResult.getUserName
        case proxyUserInfoResult: ProxyUserInfoResult =>
          ProxyUserSSOUtils.setUserAndProxyUser(proxyUserInfoResult.getUserName, proxyUserInfoResult.getProxyUser)
      }
    }(Utils.tryQuietly(dwsHttpClient.close()))
  }

}
