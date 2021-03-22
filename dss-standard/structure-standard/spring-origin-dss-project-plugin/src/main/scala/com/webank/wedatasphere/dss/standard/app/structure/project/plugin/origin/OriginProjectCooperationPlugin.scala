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

package com.webank.wedatasphere.dss.standard.app.structure.project.plugin.origin

import com.webank.wedatasphere.dss.standard.app.sso.origin.OriginSSOIntegrationStandard
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation.DSSMsg
import com.webank.wedatasphere.dss.standard.app.structure.project.plugin.{ProjectAuth, ProjectCooperationPlugin}
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction
import javax.servlet.http.HttpServletRequest

/**
  * Created by enjoyyin on 2020/8/11.
  */
class OriginProjectCooperationPlugin private() extends ProjectCooperationPlugin {

  private def getProjectAuth(action: HttpAction, dssMsg: DSSMsg): ProjectAuthImpl = {
    val dwsHttpClient = HttpClient.getDSSClient(dssMsg.getDSSUrl)
    val projectAuth = new ProjectAuthImpl
    projectAuth.setWorkspaceName(dssMsg.getWorkspaceName)
    HttpClient.addCookies(dssMsg, action)
    dwsHttpClient.execute(action) match {
      case projectAuthResult: ProjectAuthResult =>
        projectAuth.setProjectId(projectAuthResult.getProjectId)
        projectAuth.setProjectName(projectAuthResult.getProjectName)
        projectAuth.setEditUsers(projectAuthResult.getEditUsers)
        projectAuth.setAccessUsers(projectAuthResult.getAccessUsers)
        projectAuth.setDeleteUsers(projectAuthResult.getDeleteUsers)
    }
    projectAuth
  }

  override def getProjectAuth(request: HttpServletRequest, projectId: String): ProjectAuth = {
    val dssMsg = OriginSSOIntegrationStandard.getSSOIntegrationStandard.getSSOPluginService
      .createDssMsgCacheOperation().getDSSMsgInSession(request)
    val projectAuthAction = new ProjectAuthByIdAction
    projectAuthAction.setWorkspace(dssMsg.getWorkspaceName)
    projectAuthAction.setProjectId(projectId)
    projectAuthAction.setComponentName(dssMsg.getAppName)
    val projectAuth = getProjectAuth(projectAuthAction, dssMsg)
    projectAuth.setProjectId(projectId)
    projectAuth
  }

  override def getProjectAuthByName(request: HttpServletRequest, projectName: String): ProjectAuth = {
    val dssMsg = OriginSSOIntegrationStandard.getSSOIntegrationStandard.getSSOPluginService
      .createDssMsgCacheOperation().getDSSMsgInSession(request)
    val projectAuthAction = new ProjectAuthByNameAction
    projectAuthAction.setProjectName(projectName)
    projectAuthAction.setWorkspace(dssMsg.getWorkspaceName)
    val projectAuth = getProjectAuth(projectAuthAction, dssMsg)
    projectAuth.setProjectName(projectName)
    projectAuth
  }
}
object OriginProjectCooperationPlugin {
  private val projectCooperatePlugin = new OriginProjectCooperationPlugin
  def getProjectCooperatePlugin: ProjectCooperationPlugin = projectCooperatePlugin
}
