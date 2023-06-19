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

package com.webank.wedatasphere.dss.standard.app.sso.origin.plugin

import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import scala.beans.BeanProperty


@DWSHttpMessageResult("/api/rest_j/v\\d+/user/userInfo")
class UserInfoResult extends DWSResult {

  @BeanProperty var userName: String = _

}

@DWSHttpMessageResult("/api/rest_j/v\\d+/dss/getUsersOfWorkspace")
class WorkspaceUsersResult extends DWSResult {

  @BeanProperty var users: java.util.List[String] = _

}

@DWSHttpMessageResult("/api/rest_j/v\\d+/dss/framework/proxy/getProxyUser")
class ProxyUserInfoResult extends DWSResult {

  @BeanProperty var userName: String = _

  @BeanProperty var proxyUser: String = _

}