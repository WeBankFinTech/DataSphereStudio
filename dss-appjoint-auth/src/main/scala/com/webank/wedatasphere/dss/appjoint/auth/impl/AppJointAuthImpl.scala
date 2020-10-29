/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.auth.impl

import java.net.URI
import java.util

import com.webank.wedatasphere.dss.appjoint.auth.{AppJointAuth, RedirectMsg}
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder
import javax.servlet.http.HttpServletRequest
import org.apache.commons.io.IOUtils
import org.apache.http.impl.cookie.BasicClientCookie

import scala.collection.JavaConversions._

/**
  * Created by enjoyyin on 2019/11/6.
  */
class AppJointAuthImpl private() extends AppJointAuth with Logging {

  private val dwsHttpClients = new util.HashMap[String, DWSHttpClient]

  private def getBaseUrl(dssUrl: String): String = {
    val uri = new URI(dssUrl)
    val dssPort = if(uri.getPort != -1) uri.getPort else 80
    uri.getScheme + "://" + uri.getHost + ":" + dssPort
  }

  protected def getDWSClient(dssUrl: String): DWSHttpClient = {
    val baseUrl = dssUrl
    if(!dwsHttpClients.containsKey(baseUrl)) baseUrl.intern synchronized {
      if(!dwsHttpClients.containsKey(baseUrl)) {
        info("create a new DSSClient for url " + baseUrl)
        val clientConfig = DWSClientConfigBuilder.newBuilder().setDWSVersion(AppJointAuthImpl.dssVersion)
          .addUJESServerUrl(baseUrl).connectionTimeout(30000).discoveryEnabled(false)
          .maxConnectionSize(AppJointAuthImpl.maxConnection).readTimeout(30000).build()
        val dwsHttpClient = new DWSHttpClient(clientConfig, "DSS-UserInfo-Ask-Client")
        dwsHttpClients.put(baseUrl, dwsHttpClient)
      }
    }
    dwsHttpClients.get(baseUrl)
  }

  override def isDssRequest(request: HttpServletRequest): Boolean = request.getParameterMap.containsKey(AppJointAuthImpl.DSS_URL_KEY)

  override def getRedirectMsg(request: HttpServletRequest): RedirectMsg = {
    val dssUrl = request.getParameter(AppJointAuthImpl.DSS_URL_KEY)
    val dwsHttpClient = getDWSClient(dssUrl)
    val userInfoAction = new UserInfoAction
    val cookies = request.getParameter(AppJointAuthImpl.COOKIES_KEY)
    cookies.split(";").foreach { cookie =>
      val index = cookie.indexOf("=")
      val key = cookie.substring(0, index).trim
      val value = cookie.substring(index + 1).trim
      userInfoAction.addCookie(new BasicClientCookie(key, value))
    }
    val redirectMsg = new RedirectMsgImpl
    redirectMsg.setRedirectUrl(request.getParameter(AppJointAuthImpl.REDIRECT_KEY))
    dwsHttpClient.execute(userInfoAction) match {
      case userInfoResult: UserInfoResult =>
        redirectMsg.setUser(userInfoResult.getUserName)
    }
    redirectMsg
  }

  override def close(): Unit = dwsHttpClients.values().foreach(IOUtils.closeQuietly)
}

object AppJointAuthImpl {
  private val appJointAuth = new AppJointAuthImpl
  private var dssVersion = "v1"
  private var maxConnection = 5

  private val REDIRECT_KEY = "redirect"
  private val DSS_URL_KEY = "dssurl"
  private val COOKIES_KEY = "cookies"

  def apply: AppJointAuthImpl = appJointAuth

  def setDSSVersion(version: String): Unit = this.dssVersion = version
  def setMaxConnection(maxConnection: Int): Unit = this.maxConnection = maxConnection
}