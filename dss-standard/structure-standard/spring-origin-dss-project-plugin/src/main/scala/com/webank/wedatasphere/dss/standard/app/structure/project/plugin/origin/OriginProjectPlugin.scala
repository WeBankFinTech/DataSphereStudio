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

package com.webank.wedatasphere.dss.standard.app.structure.project.plugin.origin

import java.util

import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard
import com.webank.wedatasphere.dss.standard.app.sso.origin.OriginSSOIntegrationStandardFactory
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient
import com.webank.wedatasphere.dss.standard.app.structure.project.plugin.ProjectPlugin
import javax.servlet.http.HttpServletRequest


class OriginProjectPlugin private() extends ProjectPlugin {

  override def getProjects(request: HttpServletRequest): util.List[String] = {
    val ssoIntegrationStandard:SSOIntegrationStandard =new OriginSSOIntegrationStandardFactory().getSSOIntegrationStandard
    val dssMsg =ssoIntegrationStandard.getSSOPluginService.createDssMsgCacheOperation().getDSSMsgInSession(request)
    val dwsHttpClient = HttpClient.getDSSClient(dssMsg.getDSSUrl)
    val projectListAction = new ProjectListByWorkspaceAction
    projectListAction.setWorkspace(dssMsg.getWorkspaceName)
    HttpClient.addCookies(dssMsg, projectListAction)
    dwsHttpClient.execute(projectListAction) match {
      case projectListResult: ProjectListByWorkspaceResult =>
        projectListResult.getProjectIds
    }
  }

}
object OriginProjectPlugin {
  private val projectPlugin = new OriginProjectPlugin
  def getProjectPlugin: ProjectPlugin = projectPlugin
}