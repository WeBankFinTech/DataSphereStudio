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

import com.webank.wedatasphere.linkis.httpclient.dws.request.DWSHttpAction
import com.webank.wedatasphere.linkis.httpclient.request.POSTAction

/**
  * Created by enjoyyin on 2020/8/11.
  */
class ProjectAuthByIdAction extends POSTAction with DWSHttpAction {

  override def suffixURLs: Array[String] = Array("dss", "getProjectAuthOfWorkspace")

  def setWorkspace(workspaceName: String): Unit = addRequestPayload("workspaceName", workspaceName)

  def setProjectId(projectId: String): Unit = addRequestPayload("projectId", projectId)

  def setComponentName(componentName: String): Unit = addRequestPayload("componentName", componentName)

  override def getRequestPayload: String = ""
}
