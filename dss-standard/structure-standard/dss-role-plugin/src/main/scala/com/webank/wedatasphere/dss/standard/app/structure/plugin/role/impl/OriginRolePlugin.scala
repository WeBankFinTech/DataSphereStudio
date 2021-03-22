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

package com.webank.wedatasphere.dss.standard.app.structure.plugin.role.impl

import java.util

import com.webank.wedatasphere.dss.standard.app.sso.origin.OriginSSOIntegrationStandard
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.structure.plugin.role.{DSSUserRoles, RolePlugin}
import javax.servlet.http.HttpServletRequest

/**
  * Created by enjoyyin on 2020/8/10.
  */
class OriginRolePlugin private() extends RolePlugin {

  override def getRoles(request: HttpServletRequest): DSSUserRoles = {
    val dssMsg = OriginSSOIntegrationStandard.getSSOIntegrationStandard.getSSOPluginService
      .createDssMsgCacheOperation().getDSSMsgInSession(request)
    val dwsHttpClient = HttpClient.getDSSClient(dssMsg.getDSSUrl)
    val dssPrivilege = new DSSUserRolesImpl
    dssPrivilege.setWorkspaceName(dssMsg.getWorkspaceName)
    val userRoleInfoAction = new RoleInfoOfUserAction
    userRoleInfoAction.setWorkspace(dssMsg.getWorkspaceName)
    HttpClient.addCookies(dssMsg, userRoleInfoAction)
    dwsHttpClient.execute(userRoleInfoAction) match {
      case userRoleInfoResult: RoleInfoOfUserResult =>
        dssPrivilege.setRoles(userRoleInfoResult.getRoles)
        dssPrivilege.setUser(userRoleInfoResult.getUser)
    }
    dssPrivilege
  }

  override def getUsers(request: HttpServletRequest, role: String): util.List[String] = {
    val dssMsg = OriginSSOIntegrationStandard.getSSOIntegrationStandard.getSSOPluginService
      .createDssMsgCacheOperation().getDSSMsgInSession(request)
    val dwsHttpClient = HttpClient.getDSSClient(dssMsg.getDSSUrl)
    val userInfoOfRole = new UserInfoOfRoleAction
    userInfoOfRole.setWorkspace(dssMsg.getWorkspaceName)
    HttpClient.addCookies(dssMsg, userInfoOfRole)
    dwsHttpClient.execute(userInfoOfRole) match {
      case result: UserInfoOfRoleResult =>
        result.getUsers
    }
  }
}
object OriginRolePlugin {
  private val rolePlugin = new OriginRolePlugin
  def getRolePlugin: RolePlugin = rolePlugin
}