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

package org.apache.linkis.manager.engineplugin.appconn.factory

import java.io.File

import com.webank.wedatasphere.dss.appconn.loader.utils.AppConnUtils
import org.apache.linkis.DataWorkCloudApplication
import org.apache.linkis.engineconn.common.conf.EngineConnConf
import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconn.common.engineconn.EngineConn
import org.apache.linkis.engineconn.computation.executor.creation.ComputationSingleExecutorEngineConnFactory
import org.apache.linkis.engineconn.executor.entity.LabelExecutor
import org.apache.linkis.governance.common.exception.engineconn.EngineConnExecutorErrorCode
import org.apache.linkis.manager.engineplugin.appconn.executor.AppConnEngineConnExecutor
import org.apache.linkis.manager.engineplugin.common.exception.EngineConnPluginErrorException
import org.apache.linkis.manager.label.entity.engine.EngineType.EngineType
import org.apache.linkis.manager.label.entity.engine.RunType.RunType
import org.apache.linkis.manager.label.entity.engine.{EngineType, RunType}

class AppConnEngineConnFactory extends ComputationSingleExecutorEngineConnFactory {

  private val appConnHomePath = new File(EngineConnConf.getWorkHome, AppConnUtils.APPCONN_DIR_NAME)
  if(!appConnHomePath.exists() && !appConnHomePath.mkdir())
    throw new EngineConnPluginErrorException(EngineConnExecutorErrorCode.INIT_EXECUTOR_FAILED,
      s"Cannot mkdir ${appConnHomePath.getPath}, please make sure the permission is ok.")
  DataWorkCloudApplication.setProperty(AppConnUtils.APPCONN_HOME_PATH.key, appConnHomePath.getPath)
  warn(s"Set ${AppConnUtils.APPCONN_HOME_PATH.key}=${appConnHomePath.getPath}.")

  override protected def newExecutor(id: Int,
                                     engineCreationContext: EngineCreationContext,
                                     engineConn: EngineConn): LabelExecutor =
    new AppConnEngineConnExecutor(id)


  override protected def getEngineConnType: EngineType = EngineType.APPCONN

  override protected def getRunType: RunType = RunType.APPCONN

}