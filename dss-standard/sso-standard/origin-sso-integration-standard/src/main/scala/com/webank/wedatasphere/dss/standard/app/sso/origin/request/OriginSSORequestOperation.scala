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

import java.net.URI
import java.util.Date

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSHttpAction
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.httpclient.response.HttpResult

import scala.collection.convert.wrapAsScala._

/**
 * 用于请求与 DSS 打通了一级规范的第三方系统
 * @param appName AppConn 名称
 */
class OriginSSORequestOperation private[request](appName: String) extends HttpSSORequestOperation(appName) {

  /**
   * 用于请求与 DSS 打通了一级规范的第三方系统，也可以是请求 DSS 内嵌的数据应用工具。
   * @param urlBuilder 不能为空，且必须为 SSOUrlBuilderOperationImpl 的实现类
   * @param req DSSHttpAction 实现类
   * @return HTTP 请求的结果
   */
  override def requestWithSSO(urlBuilder: SSOUrlBuilderOperation, req: DSSHttpAction): HttpResult = {
    urlBuilder match {
      case urlBuilderOperationImpl: SSOUrlBuilderOperationImpl =>
        val cookies = urlBuilderOperationImpl.getCookies
        cookies.foreach {
          case (key, value) =>
            val basicClientCookie = new BasicClientCookie(key, value)
            val domain = Utils.tryCatch(new URI(urlBuilder.getBuiltUrl).getHost)(_ => "")
            basicClientCookie.setDomain(domain)
            basicClientCookie.setPath("/")
            basicClientCookie.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30L))
            info("Add cookie for get user info " + basicClientCookie.toString)
            req.addCookie(basicClientCookie)
        }
      case _ if urlBuilder != null =>
        throw new AppStandardErrorException(20300, s"Not support SSOUrlBuilderOperation => ${urlBuilder.getClass.getName}.")
      case _ =>
        throw new AppStandardErrorException(20300, "SSOUrlBuilderOperation is null.")
    }
    super.requestWithSSO(urlBuilder, req)
  }

  override protected def getKey(urlBuilder: SSOUrlBuilderOperation, req: DSSHttpAction): String = {
    val baseUrl = HttpClient.getBaseUrl(urlBuilder.getBuiltUrl)
    urlBuilder match {
      case builder: SSOUrlBuilderOperationImpl =>
        builder.getCookies.find(_._1 == DSSCommonConf.DSS_TOKEN_TICKET_KEY.getValue).foreach{ case (_, ticketId) =>
          return appName + ticketId + baseUrl
        }
    }
    throw new AppStandardErrorException(20300, "User has not login, please login first.")
  }
}