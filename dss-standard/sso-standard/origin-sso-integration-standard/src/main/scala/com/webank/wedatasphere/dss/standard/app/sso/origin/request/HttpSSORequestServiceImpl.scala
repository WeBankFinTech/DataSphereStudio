package com.webank.wedatasphere.dss.standard.app.sso.origin.request

import java.util

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService
import com.webank.wedatasphere.dss.standard.common.service.AppServiceImpl

/**
 *
 * @date 2022-03-17
 * @author enjoyyin
 * @since 1.1.0
 */
class HttpSSORequestServiceImpl extends AppServiceImpl with SSORequestService {

  private val ssoRequestServices = new util.HashMap[String, HttpSSORequestOperation]

  protected def createHttpSSORequestOperation(appName: String): HttpSSORequestOperation =
    new OriginSSORequestOperation(appName)

  override def createSSORequestOperation(appName: String): HttpSSORequestOperation = {
    if(!ssoRequestServices.containsKey(appName)) synchronized {
      if(!ssoRequestServices.containsKey(appName)) ssoRequestServices.put(appName, createHttpSSORequestOperation(appName))
    }
    ssoRequestServices.get(appName)
  }

}
