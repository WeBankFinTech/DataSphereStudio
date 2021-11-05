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

package com.webank.wedatasphere.dss.standard.app.structure.project.plugin.conf

import com.webank.wedatasphere.linkis.common.conf.{CommonVars, TimeType}


object ProjectCooperateConfiguration {

  val EXPIRE_PROJECT_AUTH_TIMEOUT = CommonVars("wds.dss.project.auth.timeout", new TimeType("10m"))
  val EXPIRE_PROJECT_AUTH_SCAN_INTERVAL = CommonVars("wds.dss.project.auth.scan.interval", new TimeType("5m"))

}
