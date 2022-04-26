package com.webank.wedatasphere.dss.standard.app.sso.origin

import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.sso.origin.plugin.OriginSSOPluginServiceImpl
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.HttpSSORequestServiceImpl
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOPluginService
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService
import com.webank.wedatasphere.dss.standard.app.sso.user.SSOUserService
import com.webank.wedatasphere.dss.standard.app.sso.user.impl.SSOUserServiceImpl
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance

import scala.collection.mutable

/**
 *
 * @date 2022-03-17
 * @author enjoyyin
 * @since 1.1.0
 */
class HttpSSOIntegrationStandard extends SSOIntegrationStandard {

  private val ssoPluginService: SSOPluginService = new OriginSSOPluginServiceImpl
  private val ssoRequestService: SSORequestService = createSSORequestService()
  private val ssoUserServices = new mutable.ArrayBuffer[SSOUserService]()

  protected def createSSORequestService(): SSORequestService = new HttpSSORequestServiceImpl

  override def getSSORequestService: SSORequestService = ssoRequestService

  override def getSSOPluginService: SSOPluginService = ssoPluginService

  override def getSSOUserService(appInstance: AppInstance): SSOUserService =
    ssoUserServices.find(_.getAppInstance == appInstance).getOrElse(ssoUserServices synchronized {
      ssoUserServices.find(_.getAppInstance == appInstance).getOrElse {
        val service = new SSOUserServiceImpl
        service.setAppInstance(appInstance)
        ssoUserServices += service
        service
      }
    })

  override def init(): Unit = {
    ssoPluginService.setSSOBuilderService(getSSOBuilderService)
  }

  override def close(): Unit = HttpClient.close()

}
