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

import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard
import com.webank.wedatasphere.dss.standard.app.sso.origin.OriginSSOIntegrationStandardFactory
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.SSOPluginFilter
import org.apache.linkis.common.utils.Logging


abstract class OriginSSOPluginFilter extends SSOPluginFilter with Logging{

  private val factory = new OriginSSOIntegrationStandardFactory
  factory.init()

  override def info(str: String): Unit = logger.info(str)

  override protected def getSSOIntegrationStandard: SSOIntegrationStandard = factory.getSSOIntegrationStandard
}
