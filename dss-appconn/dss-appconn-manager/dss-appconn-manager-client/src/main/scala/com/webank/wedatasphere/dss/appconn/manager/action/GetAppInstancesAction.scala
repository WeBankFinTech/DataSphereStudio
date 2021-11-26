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

package com.webank.wedatasphere.dss.appconn.manager.action

import com.webank.wedatasphere.linkis.httpclient.dws.request.DWSHttpAction
import com.webank.wedatasphere.linkis.httpclient.request.{GetAction, UserAction}

class GetAppInstancesAction extends GetAction with DWSHttpAction with UserAction {

  private var user:String = _

  private var appConnName: String = _

  override def suffixURLs: Array[String] = Array("dss", "framework", "project", "appconn", appConnName, "getAppInstances")

  def setAppConnName(appConnName: String): Unit = this.appConnName = appConnName

  override def setUser(user: String): Unit =  this.user = user

  override def getUser: String = this.user

}
