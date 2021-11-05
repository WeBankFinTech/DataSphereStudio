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

package com.webank.wedatasphere.dss.standard.app.sso.plugin.filter

import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard
import javax.servlet._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.lang.StringUtils


abstract class SSOPluginFilter extends Filter {

  private var userInterceptor: UserInterceptor = _

  override def init(filterConfig: FilterConfig): Unit = {
    userInterceptor = getUserInterceptor(filterConfig)
    getSSOIntegrationStandard.init()
  }

  def info(str: String): Unit

  protected def getUserInterceptor(filterConfig: FilterConfig): UserInterceptor

  protected def getSSOIntegrationStandard: SSOIntegrationStandard

  override def doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse,
                        filterChain: FilterChain): Unit = {
    val req = servletRequest.asInstanceOf[HttpServletRequest]
    val resp = servletResponse.asInstanceOf[HttpServletResponse]
    val ssoPluginService = getSSOIntegrationStandard.getSSOPluginService
    val dssMsg = ssoPluginService.createSSOMsgParseOperation().getDSSMsg(req)
    if (ssoPluginService.createSSOMsgParseOperation.isDssRequest(req)) {
      val (redirectUrl, workspaceName, wrappedReq) = if(!userInterceptor.isUserExistInSession(req)) {
        val ssoMsg = ssoPluginService.createSSOMsgParseOperation.getSSOMsg(req)
        val username = ssoMsg.getUser
        val wrappedReq = userInterceptor match {
          case interceptor: HttpSessionUserInterceptor =>
            interceptor.addUserToSession(username, req)
            req
          case interceptor: HttpRequestUserInterceptor => interceptor.addUserToRequest(username, req)
          case interceptor: DSSInternalUserInterceptor => interceptor.addCookiesToRequest(dssMsg, req)
        }
        if(wrappedReq != req) {
          wrappedReq.getCookies.foreach(resp.addCookie)
        }
        info(s"DSS User: $username succeed to login. and wrappedReq cookies is "+wrappedReq.getCookies.toString)
        (ssoMsg.getRedirectUrl, ssoMsg.getWorkspaceName, wrappedReq)
      } else {
        (dssMsg.getRedirectUrl, dssMsg.getWorkspaceName, req)
      }
      val workspaceOperation = ssoPluginService.createDssMsgCacheOperation()
      if(workspaceName != workspaceOperation.getWorkspaceInSession(req)) {
        info(s"Set DSS workspace to: $workspaceName.")
        workspaceOperation.setWorkspaceToSession(req, workspaceName)
      }
      if(workspaceOperation.getDSSMsgInSession(req) == null) {
        workspaceOperation.setDSSMsgToSession(dssMsg, req)
      }
      if(StringUtils.isNotBlank(redirectUrl)) resp.sendRedirect(redirectUrl)
      else filterChain.doFilter(wrappedReq, servletResponse)
    } else {
      filterChain.doFilter(servletRequest, servletResponse)
    }
  }

  override def destroy(): Unit = {}

}