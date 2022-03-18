package com.webank.wedatasphere.dss.standard.app.sso.origin

import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.sso.origin.plugin.OriginSSOPluginServiceImpl
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.{HttpSSORequestServiceImpl, OriginSSORequestServiceImpl}
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOPluginService
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService

/**
 *
 * @date 2022-03-17
 * @author enjoyyin
 * @since 1.1.0
 */
class HttpSSOIntegrationStandard extends SSOIntegrationStandard {

  private val ssoPluginService: SSOPluginService = new OriginSSOPluginServiceImpl
  private val ssoRequestService: SSORequestService = createSSORequestService()

  protected def createSSORequestService(): SSORequestService = new HttpSSORequestServiceImpl

  override def getSSORequestService: SSORequestService = ssoRequestService

  override def getSSOPluginService: SSOPluginService = ssoPluginService

  override def init(): Unit = {
    ssoPluginService.setSSOBuilderService(getSSOBuilderService)
  }

  override def close(): Unit = HttpClient.close()

}
