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

import java.net.{URI, URL, URLDecoder}
import java.util
import java.util.Date
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException
import com.webank.wedatasphere.linkis.common.utils.{ByteTimeUtils, Logging, Utils}
import com.webank.wedatasphere.linkis.httpclient.Client
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction
import com.webank.wedatasphere.linkis.httpclient.response.impl.DefaultHttpResult
import com.webank.wedatasphere.linkis.httpclient.response.{HttpResult, Result}
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.apache.http.impl.cookie.BasicClientCookie

import scala.collection.convert.wrapAsScala._


class OriginSSORequestOperation private[request](appName: String) extends SSORequestOperation[HttpAction, HttpResult] with Logging {

  override def requestWithSSO(urlBuilder: SSOUrlBuilderOperation, req: HttpAction): HttpResult = {

    val httpClient = HttpClient.getHttpClient(urlBuilder.getBuiltUrl, appName)
    urlBuilder match {
      case urlBuilderOperationImpl: SSOUrlBuilderOperationImpl => val cookies = urlBuilderOperationImpl.getCookies
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
    }
    Utils.tryFinally({
      httpClient.execute(req) match {
        case result: HttpResult => result
        case result => if (Result.isSuccessResult(result)) {
          val defaultHttpResult = new DefaultHttpResult
          defaultHttpResult.set(null, 200, null, null)
          defaultHttpResult
        } else throw new AppStandardErrorException(20300, s"Not support Result => ${result.getClass.getName}.")
      }
    })(IOUtils.closeQuietly(httpClient))
  }

}

object OriginSSORequestOperation extends Logging {

  val MAX_ACTIVE_TIME = ByteTimeUtils.timeStringAsMs("15m")

  private val httpClientLastAccessMap = new ConcurrentHashMap[String, Long]
  private val httpClientMap = new util.HashMap[String, Client]

  Utils.defaultScheduler.scheduleWithFixedDelay(new Runnable {
    override def run(): Unit = httpClientLastAccessMap.keySet().toArray.foreach {
      case key: String =>
        if (System.currentTimeMillis - httpClientLastAccessMap.get(key) >= MAX_ACTIVE_TIME) httpClientMap synchronized {
          if (httpClientLastAccessMap.containsKey(key)) {
            httpClientLastAccessMap.remove(key)
            IOUtils.closeQuietly(httpClientMap.get(key))
            httpClientMap.remove(key)
            info(s"SSORequestOperation removed expired key($key).")
          }
        }
    }
  }, MAX_ACTIVE_TIME, MAX_ACTIVE_TIME, TimeUnit.MILLISECONDS)

  def getHttpClient(urlBuilder: SSOUrlBuilderOperation, appName: String): Client = urlBuilder match {
    case builder: SSOUrlBuilderOperationImpl =>
      builder.getCookies.find(_._1 == "bdp-user-ticket-id").foreach { case (_, ticketId) =>
        val key = getKey(ticketId, appName)
        if (httpClientMap.containsKey(key) && System.currentTimeMillis - httpClientLastAccessMap.get(key) < MAX_ACTIVE_TIME) {
          httpClientLastAccessMap.put(key, System.currentTimeMillis)
          return httpClientMap.get(key)
        }
        else httpClientMap synchronized {
          if (httpClientMap.containsKey(key) && System.currentTimeMillis - httpClientLastAccessMap.get(key) < MAX_ACTIVE_TIME) {
            httpClientLastAccessMap.put(key, System.currentTimeMillis)
            return httpClientMap.get(key)
          } else {
            if (httpClientMap.containsKey(key)) {
              httpClientMap.get(key).close()
            }

            val httpClient = HttpClient.getHttpClient(urlBuilder.getBuiltUrl, appName)
            httpClientMap.put(key, httpClient)
            httpClientLastAccessMap.put(key, System.currentTimeMillis)
            info(s"SSORequestOperation add a new HttpClient for key($key).")
            return httpClient
          }
        }
      }
      throw new AppStandardErrorException(20300, "User has not login, please login first.")
    case _ =>
      throw new AppStandardErrorException(20300, s"Not support SSOUrlBuilderOperation => ${urlBuilder.getClass.getName}.")
  }


  private def getKey(ticketId: String, appName: String): String = appName + ticketId

}