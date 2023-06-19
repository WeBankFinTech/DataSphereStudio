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
 * HttpSSOIntegrationStandard 与 OriginSSOIntegrationStandard 的唯一区别，在于 SSORequestService 的不同。
 *
 * 请注意，DSS 框架不推荐使用该一级规范实现类。
 *
 * 1. OriginSSORequestServiceImpl：通常用于请求与 DSS 打通了一级规范的第三方系统，推荐使用；
 * 2. HttpSSORequestServiceImpl：用于通过使用类似 token 的方式，访问没有打通一级规范的第三方系统，不推荐使用。
 *
 * @author enjoyyin
 * @since 1.1.0
 */
class HttpSSOIntegrationStandard extends SSOIntegrationStandard {

  private val ssoPluginService: SSOPluginService = createSSOPluginService()
  private val ssoRequestService: SSORequestService = createSSORequestService()
  private val ssoUserServices = new mutable.ArrayBuffer[SSOUserService]()

  protected def createSSOPluginService(): SSOPluginService = new OriginSSOPluginServiceImpl

  protected def createSSORequestService(): SSORequestService = new HttpSSORequestServiceImpl

  override def getSSORequestService: SSORequestService = ssoRequestService

  override def getSSOPluginService: SSOPluginService = ssoPluginService

  override def getSSOUserService(appInstance: AppInstance): SSOUserService =
    ssoUserServices.find(_.getAppInstance == appInstance).getOrElse(ssoUserServices synchronized {
      ssoUserServices.find(_.getAppInstance == appInstance).getOrElse {
        val service = new SSOUserServiceImpl
        service.setSSORequestService(getSSORequestService)
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
