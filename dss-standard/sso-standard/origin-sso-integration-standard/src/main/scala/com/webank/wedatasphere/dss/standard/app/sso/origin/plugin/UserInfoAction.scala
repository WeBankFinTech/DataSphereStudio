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

package com.webank.wedatasphere.dss.standard.app.sso.origin.plugin

import com.webank.wedatasphere.linkis.httpclient.dws.request.DWSHttpAction
import com.webank.wedatasphere.linkis.httpclient.request.GetAction
import com.webank.wedatasphere.linkis.httpclient.request.POSTAction

/**
  * Created by enjoyyin on 2019/11/7.
  */
class UserInfoAction extends GetAction with DWSHttpAction {
  override def suffixURLs: Array[String] = Array("user", "userInfo")
}
class WorkspaceUsersAction extends POSTAction with DWSHttpAction {
  override def getRequestPayload: String = ""

  override def suffixURLs: Array[String] = Array("dss", "getUsersOfWorkspace")

  def setWorkspace(workspace: String): Unit = addRequestPayload("workspaceName", workspace)
}