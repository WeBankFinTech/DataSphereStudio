/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.auth.impl

import com.webank.wedatasphere.dss.appjoint.auth.RedirectMsg

/**
  * Created by enjoyyin on 2019/11/7.
  */
class RedirectMsgImpl extends RedirectMsg {

  private var redirectUrl: String = _
  private var user: String = _

  override def getRedirectUrl: String = redirectUrl

  override def setRedirectUrl(redirectUrl: String): Unit = this.redirectUrl = redirectUrl

  override def getUser: String = user

  override def setUser(user: String): Unit = this.user = user
}
