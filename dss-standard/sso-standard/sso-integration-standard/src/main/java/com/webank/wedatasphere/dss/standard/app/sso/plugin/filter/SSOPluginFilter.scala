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

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf.DSS_TOKEN_TICKET_KEY
import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.proxy.{DSSInternalProxyUserInterceptor, HttpRequestProxyUserInterceptor, HttpSessionProxyUserInterceptor}
import com.webank.wedatasphere.dss.standard.sso.utils.ProxyUserSSOUtils
import javax.servlet._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.lang.StringUtils
import org.apache.linkis.protocol.util.ImmutablePair


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
    val workspaceOperation = ssoPluginService.createDssMsgCacheOperation()
    if (ssoPluginService.createSSOMsgParseOperation.isDssRequest(req)) {
      val isFirstLogin = !userInterceptor.isUserExistInSession(req) || (workspaceOperation.getDSSMsgInSession(req) != null &&
        req.getCookies.find(_.getName == DSS_TOKEN_TICKET_KEY.getValue).map(_.getValue).exists(_ != workspaceOperation.getDSSMsgInSession(req).getCookies.get(DSS_TOKEN_TICKET_KEY.getValue)))
      // 存在一种场景：第一次登陆时没有带上代理用户（DSS登陆成功后就开始请求第三方节点），这时为了使第三方节点能够正常使用，允许完成SSO认证。
      // 这时，如果DSS绑定了代理用户，再来请求这个第三方节点时，由于已经完成认证会直接放行，所以这里加上一个判断逻辑，
      // 就是就算完成了登录认证，但是如果之前的登录认证没有包含代理用户，而本次请求的cookie中包含代理用户，则触发再次认证。
      val isSecondLoginWithProxyUser = userInterceptor.isUserExistInSession(req) && !workspaceOperation.isExistsProxyUser(req) &&
        ProxyUserSSOUtils.existsProxyUser(dssMsg)
      val (redirectUrl, workspaceName, wrappedReq) = if(isFirstLogin || isSecondLoginWithProxyUser) {
        val ssoMsg = ssoPluginService.createSSOMsgParseOperation.getSSOMsg(req)
        val username = ssoMsg.getUser
        val pair = if(ProxyUserSSOUtils.existsProxyUser(username)) {
          workspaceOperation.setExistsProxyUser(req)
          ProxyUserSSOUtils.getUserAndProxyUser(username)
        } else new ImmutablePair[String, String](username, username)
        val wrappedReq = userInterceptor match {
          case interceptor: HttpSessionProxyUserInterceptor =>
            interceptor.addUserToSession(pair.getKey, pair.getValue, req)
            req
          case interceptor: HttpRequestProxyUserInterceptor =>
            interceptor.addUserToRequest(pair.getKey, pair.getValue, req)
          case interceptor: DSSInternalProxyUserInterceptor =>
            interceptor.addCookiesToRequest(dssMsg, pair.getKey, pair.getValue, req)
          case interceptor: HttpSessionUserInterceptor =>
            interceptor.addUserToSession(pair.getValue, req)
            req
          case interceptor: HttpRequestUserInterceptor =>
            interceptor.addUserToRequest(pair.getValue, req)
          case interceptor: DSSInternalUserInterceptor =>
            interceptor.addCookiesToRequest(dssMsg, req)
        }
        if(wrappedReq != req) {
          wrappedReq.getCookies.foreach(resp.addCookie)
        }
        if(ProxyUserSSOUtils.existsProxyUser(username)) {
          info(s"DSS proxy user mode is opened. DSS user: ${pair.getKey} proxy to proxyUser: ${pair.getValue} login succeed.")
        } else {
          info(s"DSS User: $username succeed to login. ")
        }
        (ssoMsg.getRedirectUrl, ssoMsg.getWorkspaceName, wrappedReq)
      } else {
        (dssMsg.getRedirectUrl, dssMsg.getWorkspaceName, req)
      }

      if(workspaceOperation.getDSSMsgInSession(req) == null) {
        workspaceOperation.setDSSMsgToSession(dssMsg, req)
      }
      if(workspaceName != workspaceOperation.getWorkspaceInSession(req)) {
        info(s"Set DSS workspace to: $workspaceName.")
        workspaceOperation.setWorkspaceToSession(req, workspaceName)
      }
      if(StringUtils.isNotBlank(redirectUrl)) resp.sendRedirect(redirectUrl)
      else filterChain.doFilter(wrappedReq, servletResponse)
    } else {
      filterChain.doFilter(servletRequest, servletResponse)
    }
  }

  override def destroy(): Unit = {}

}