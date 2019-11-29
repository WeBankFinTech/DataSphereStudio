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

package com.webank.wedatasphere.dss.appjoint.visualis.execution

import com.webank.wedatasphere.linkis.common.conf.CommonVars
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration

/**
  * Created by enjoyyin on 2019/10/12.
  */
object VisualisNodeExecutionConfiguration {

  val DISPLAY_PREVIEW_URL_FORMAT = CommonVars("wds.dss.appjoint.visualis.display.preview.url",
    "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION + "/visualis/displays/%s/preview")
  val DASHBOARD_PREVIEW_URL_FORMAT = CommonVars("wds.dss.appjoint.visualis.dashboard.preview.url",
    "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION + "/visualis/dashboard/%s/preview")

  val VISUALIS_THREAD_MAX = CommonVars("wds.dss.appjoint.visualis.thread.max", 20)

}
