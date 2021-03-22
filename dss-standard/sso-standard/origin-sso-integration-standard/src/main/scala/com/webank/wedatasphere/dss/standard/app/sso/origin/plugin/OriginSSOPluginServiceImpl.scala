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

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOBuilderService
import com.webank.wedatasphere.dss.standard.app.sso.plugin.{SSOMsgParseOperation, SSOPluginService, WorkspacePlugin}
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationServiceImpl
import com.webank.wedatasphere.linkis.common.utils.Logging


/**
  * Created by enjoyyin on 2019/11/6.
  */
class OriginSSOPluginServiceImpl extends AppIntegrationServiceImpl with SSOPluginService with Logging {

  private var ssoBuilderService: SSOBuilderService = _
  private val ssoMsgParseOperation = new OriginSSOMsgParseOperation
  private val workspacePlugin = new OriginWorkspacePlugin
  workspacePlugin.setDssMsgCacheOperation(createDssMsgCacheOperation())

  override def setSSOBuilderService(ssoBuilderService: SSOBuilderService): Unit = {
    this.ssoBuilderService = ssoBuilderService
    ssoMsgParseOperation.setSSOBuilderService(ssoBuilderService)

  }

  override def createSSOMsgParseOperation(): SSOMsgParseOperation = ssoMsgParseOperation

  override def close(): Unit = {}

  override def createWorkspacePluginOperation(): WorkspacePlugin = workspacePlugin
}