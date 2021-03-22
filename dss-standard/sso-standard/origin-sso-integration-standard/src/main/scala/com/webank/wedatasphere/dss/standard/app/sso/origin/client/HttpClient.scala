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

package com.webank.wedatasphere.dss.standard.app.sso.origin.client

import java.net.URI
import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.fasterxml.jackson.databind.ObjectMapper
import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation.DSSMsg
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.httpclient.Client
import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction
import org.apache.commons.io.IOUtils
import org.apache.http.impl.cookie.BasicClientCookie

import scala.collection.JavaConversions._

/**
  * Created by enjoyyin on 2020/8/10.
  */
object HttpClient extends Logging {

  private val dssClients = new util.HashMap[String, DWSHttpClient]
  private val httpClients = new util.HashMap[String, Client]



  def getBaseUrl(url: String): String = {
    val uri = new URI(url)
    if (uri.getPort > 0){
      uri.getScheme + "://" + uri.getHost + ":" + uri.getPort
    } else {
      uri.getScheme + "://" + uri.getHost
    }
  }

  private def getClient[T](url: String, cacheMap: util.HashMap[String, T], createNewClient: String => T): T = {
    val baseUrl = getBaseUrl(url)
    if(!cacheMap.containsKey(baseUrl)) baseUrl.intern synchronized {
      if(!cacheMap.containsKey(baseUrl)) {
        info("create a new Client for url " + baseUrl)
        val client = createNewClient(baseUrl)
        cacheMap.put(baseUrl, client)
      }
    }
    cacheMap.get(baseUrl)
  }

  def getHttpClient(url: String, appName: String): Client = getClient(url, httpClients, baseUrl => {
    val clientConfig = DWSClientConfigBuilder.
      newBuilder().
      addServerUrl(baseUrl).
      connectionTimeout(connectTimeout).
      setDWSVersion("v1").
      discoveryEnabled(false).
      maxConnectionSize(maxConnection).
      readTimeout(readTimeout).build()
    new DWSHttpClient(clientConfig, appName + "-SSO-Client")
  })

  def getDSSClient(dssUrl: String): DWSHttpClient = getClient(dssUrl, dssClients, baseUrl => {
    val clientConfig = DWSClientConfigBuilder.newBuilder().setDWSVersion(dssVersion)
      .addServerUrl(baseUrl).connectionTimeout(connectTimeout).discoveryEnabled(true)
      .maxConnectionSize(maxConnection).readTimeout(readTimeout).build()
    new DWSHttpClient(clientConfig, "DSS-Integration-Standard-Client")
  })

  def addCookies(dssMsg: DSSMsg, action: HttpAction): Unit =
    dssMsg.getCookies.foreach { case (key, value) =>
      val basicClientCookie = new BasicClientCookie(key, value)
      val domain = Utils.tryCatch(new URI(dssMsg.getDSSUrl).getHost)(_ => HttpClient.getBaseUrl(dssMsg.getDSSUrl))
      basicClientCookie.setDomain(domain)
      basicClientCookie.setPath("/")
      basicClientCookie.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30L))
      action.addCookie(basicClientCookie)
    }

  def close(): Unit = {
    dssClients.values().foreach(IOUtils.closeQuietly)
    httpClients.values().foreach(IOUtils.closeQuietly)
  }

  private var dssVersion = "v1"
  private var maxConnection = 5
  private var connectTimeout = 30000
  private var readTimeout = 30000

  //TODO 切换为linkis-common的JsonUtils
  val objectMapper = new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"))

  def setDSSVersion(version: String): Unit = this.dssVersion = version
  def setMaxConnection(maxConnection: Int): Unit = this.maxConnection = maxConnection
  def setConnectTimeout(connectTimeout: Int): Unit = this.connectTimeout = connectTimeout
  def setReadTimeout(readTimeout: Int): Unit = this.readTimeout = readTimeout

}
