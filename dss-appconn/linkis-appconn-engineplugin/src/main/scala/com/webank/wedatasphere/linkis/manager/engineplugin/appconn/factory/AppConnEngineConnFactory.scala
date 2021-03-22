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

package com.webank.wedatasphere.linkis.manager.engineplugin.appconn.factory

import com.webank.wedatasphere.linkis.engineconn.common.creation.EngineCreationContext
import com.webank.wedatasphere.linkis.engineconn.common.engineconn.{DefaultEngineConn, EngineConn}
import com.webank.wedatasphere.linkis.engineconn.core.executor.ExecutorManager
import com.webank.wedatasphere.linkis.engineconn.executor.entity.Executor
import com.webank.wedatasphere.linkis.manager.engineplugin.appconn.executor.AppConnEngineConnExecutor
import com.webank.wedatasphere.linkis.manager.engineplugin.common.creation.{ExecutorFactory, SingleExecutorEngineConnFactory}
import com.webank.wedatasphere.linkis.manager.label.entity.Label
import com.webank.wedatasphere.linkis.manager.label.entity.cluster.ClusterLabel
import com.webank.wedatasphere.linkis.manager.label.entity.engine.EngineRunTypeLabel

import scala.collection.JavaConversions._

class AppConnEngineConnFactory extends SingleExecutorEngineConnFactory{

  override def createExecutor(engineCreationContext: EngineCreationContext, engineConn: EngineConn): Executor = {
    val id = ExecutorManager.getInstance().generateId()
    val executor = new AppConnEngineConnExecutor(id)
//    val userWithCreator = ExecutorFactory.parseUserWithCreator(engineCreationContext.getLabels.toArray[Label[_]])
//    executor.setUserWithCreator(userWithCreator)
    val runTypeLabel = getDefaultEngineRunTypeLabel()
    val clusterLabel = new ClusterLabel
    clusterLabel.setClusterName("DEV")
    clusterLabel.setClusterType("DEV")
    executor.getExecutorLabels().add(clusterLabel)
    executor.getExecutorLabels().add(runTypeLabel)
    executor
  }

  override def createEngineConn(engineCreationContext: EngineCreationContext): EngineConn = {
    val engineConn = new DefaultEngineConn(engineCreationContext)
    engineConn.setEngineType("appconn")
    engineConn
  }

  override def getDefaultEngineRunTypeLabel(): EngineRunTypeLabel = {
    val runTypeLabel = new EngineRunTypeLabel
    runTypeLabel.setRunType("appconn")
    runTypeLabel
  }
}
