package com.webank.wedatasphere.dss.standard.app.sso.origin.request

import java.util
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSHttpAction
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException
import com.webank.wedatasphere.dss.standard.app.sso.origin.conf.OriginSSOIntegrationConfiguration._
import org.apache.commons.io.IOUtils
import org.apache.linkis.common.utils.{ByteTimeUtils, Logging, Utils}
import org.apache.linkis.httpclient.Client
import org.apache.linkis.httpclient.response.impl.DefaultHttpResult
import org.apache.linkis.httpclient.response.{HttpResult, Result}

/**
 * 用于通过使用类似 token 的方式，访问没有打通一级规范的第三方系统，不推荐使用。
 * @author enjoyyin
 * @since 1.1.0
 */
class HttpSSORequestOperation(private val appName: String) extends SSORequestOperation[DSSHttpAction, HttpResult] with Logging {

  /**
   * 用于通过使用类似 token 的方式，访问没有打通一级规范的第三方系统，不推荐使用。
   * 为了能正常访问 第三方系统，请保证 req 已在 header 或 cookies 中注入了 token。
   * @param urlBuilder 由于第三方系统没有打通一级规范，所以 urlBuilder 一般为空
   * @param req Http 请求实体
   * @return Http 请求结果
   */
  override def requestWithSSO(urlBuilder: SSOUrlBuilderOperation, req: DSSHttpAction): HttpResult = {
    val key = getKey(urlBuilder, req)
    val url = getUrl(urlBuilder, req)
    val httpClient = HttpSSORequestOperation.getHttpClient(appName, key, url)
    httpClient.execute(req) match {
      case result: HttpResult => result
      case result => if (Result.isSuccessResult(result)) {
        val defaultHttpResult = new DefaultHttpResult
        defaultHttpResult.set(null, 200, url, null)
        defaultHttpResult
      } else throw new AppStandardErrorException(20300, s"Not support Result => ${result.getClass.getName}.")
    }
  }

  protected def getKey(urlBuilder: SSOUrlBuilderOperation, req: DSSHttpAction): String = {
    val baseUrl = HttpClient.getBaseUrl(getUrl(urlBuilder, req))
    appName + req.getUser + baseUrl
  }

  protected def getUrl(urlBuilder: SSOUrlBuilderOperation, req: DSSHttpAction): String =
    if(urlBuilder == null) {
      req.getURL
    } else {
      urlBuilder.getBuiltUrl
    }

}
object HttpSSORequestOperation extends Logging {

  val MAX_ACTIVE_TIME = ByteTimeUtils.timeStringAsMs("35m")

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

  def getHttpClient(appName: String, key: String, url: String): Client = {
    if (httpClientMap.containsKey(key) && System.currentTimeMillis - httpClientLastAccessMap.get(key) < MAX_ACTIVE_TIME) {
      httpClientLastAccessMap.put(key, System.currentTimeMillis)
      httpClientMap.get(key)
    } else httpClientMap synchronized {
      if (httpClientMap.containsKey(key) && System.currentTimeMillis - httpClientLastAccessMap.get(key) < MAX_ACTIVE_TIME) {
        httpClientLastAccessMap.put(key, System.currentTimeMillis)
        return httpClientMap.get(key)
      } else {
        if (httpClientMap.containsKey(key)) {
          IOUtils.closeQuietly(httpClientMap.get(key))
        }
        info(s"The appName $appName try to create http client with key $key.")
        HttpClient.setConnectTimeout(SSO_MAX_HTTP_CONNECT_TIMEOUT.getValue)
        HttpClient.setReadTimeout(SSO_MAX_HTTP_READ_TIMEOUT.getValue)
        val httpClient = HttpClient.getHttpClient(url, appName)
        httpClientMap.put(key, httpClient)
        httpClientLastAccessMap.put(key, System.currentTimeMillis)
        info(s"SSORequestOperation created a new http client for appName $appName with key($key).")
        return httpClient
      }
    }
  }
}