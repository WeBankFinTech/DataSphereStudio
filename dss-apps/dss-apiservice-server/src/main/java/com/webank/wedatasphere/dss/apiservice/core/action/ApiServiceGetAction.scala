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

package com.webank.wedatasphere.dss.apiservice.core.action

import com.webank.wedatasphere.linkis.httpclient.request.GetAction
import com.webank.wedatasphere.linkis.ujes.client.request.UJESJobAction

/**
 * @author allenlliu
 * @date 2020/09/14 10:32 AM
 * @version 2.0.0
 */
class ApiServiceGetAction extends GetAction with  UJESJobAction {
  override def suffixURLs: Array[String] =  Array("dss","framework","workspace", "getWorkspaceIdByUserName")
}
