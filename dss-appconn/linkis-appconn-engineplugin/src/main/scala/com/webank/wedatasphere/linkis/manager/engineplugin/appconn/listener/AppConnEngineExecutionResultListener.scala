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

import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionResultListener
import com.webank.wedatasphere.linkis.common.io.resultset.ResultSetWriter
import com.webank.wedatasphere.linkis.common.io.{MetaData, Record}
import com.webank.wedatasphere.linkis.engineconn.computation.executor.execute.EngineExecutionContext

class AppConnEngineExecutionResultListener(val engineExecutionContext: EngineExecutionContext) extends ExecutionResultListener {

  private var resultSetWriter: ResultSetWriter[_ <: MetaData, _ <: Record] = _

  override def setResultSetType(resultSetType: String): Unit = {
    resultSetWriter = engineExecutionContext.createResultSetWriter(resultSetType)
  }

  override def onResultMetaData(metaData: MetaData): Unit = {
    if(resultSetWriter == null){
      resultSetWriter = engineExecutionContext.createDefaultResultSetWriter()
    }
    resultSetWriter.addMetaData(metaData)
  }

  override def onResultSetRecord(record: Record): Unit = {
    if(resultSetWriter == null){
      resultSetWriter = engineExecutionContext.createDefaultResultSetWriter()
    }
    resultSetWriter.addRecord(record)
  }

}
