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

package com.webank.wedatasphere.linkis.manager.engineplugin.appconn.listener

import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionLogListener
import com.webank.wedatasphere.linkis.engineconn.computation.executor.execute.EngineExecutionContext

class AppConnEngineExecutionLogListener(val engineExecutionContext: EngineExecutionContext) extends ExecutionLogListener {

  override def onInfo(log: String): Unit = {
    engineExecutionContext.info(log)
  }

  override def onERROR(log: String): Unit = {
    engineExecutionContext.error(log)
  }

  override def onWarn(log: String): Unit = {
    engineExecutionContext.warn(log)
  }

  override def onSystemInfo(log: String): Unit = {
    engineExecutionContext.info(log)
  }

  override def onSystemError(log: String): Unit = {
    engineExecutionContext.error(log)
  }

  override def onSystemWarn(log: String): Unit = {
    engineExecutionContext.warn(log)
  }

}
