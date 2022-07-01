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

package org.apache.linkis.manager.engineplugin.appconn.conf

import org.apache.linkis.common.conf.CommonVars


object AppConnEngineConnConfiguration {

  val GATEWAY_SPRING_APPLICATION = CommonVars("wds.linkis.gateway.spring.name", "dataworkcloud-gateway")

  val CONCURRENT_LIMIT = CommonVars("wds.linkis.engineconn.appconn.conncurrent.limit", 100)

  val ENGINE_DEFAULT_LIMIT = CommonVars("wds.linkis.jdbc.default.limit", 5000)

}
