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

package com.webank.wedatasphere.dss.flow.execution.entrance.engine

import java.util

import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.scheduler.executer.{Executor, ExecutorManager}
import com.webank.wedatasphere.linkis.scheduler.listener.ExecutorListener
import com.webank.wedatasphere.linkis.scheduler.queue.SchedulerEvent

import scala.concurrent.duration.Duration

/**
 * created by chaogefeng on 2019/11/4 17:25
 * Description:
 */
class FlowExecutionExecutorManagerImpl(flowEntranceEngine: FlowEntranceEngine)  extends ExecutorManager with Logging{
  logger.info("FlowExecutionExecutorManagerImpl Registered")
  private val idToEngines = new util.HashMap[Long, Executor]

  override def setExecutorListener(executorListener: ExecutorListener): Unit = {

  }



  protected override def createExecutor(schedulerEvent: SchedulerEvent): Executor = schedulerEvent match {
    case job: FlowEntranceJob =>
      idToEngines.put(this.flowEntranceEngine.getId, this.flowEntranceEngine)
      this.flowEntranceEngine
    case _ => null
  }

  override def askExecutor(schedulerEvent: SchedulerEvent): Option[Executor] = schedulerEvent match {
    case job: FlowEntranceJob =>
      Some(createExecutor(schedulerEvent))
  }

  override def askExecutor(schedulerEvent: SchedulerEvent, wait: Duration): Option[Executor] = schedulerEvent match {
    case job: FlowEntranceJob =>
      Some(createExecutor(schedulerEvent))
  }


  override def getById(id: Long): Option[Executor] = {
    Option(idToEngines.get(id))
  }

  override def getByGroup(groupName: String): Array[Executor] = {
    null
  }



  override def shutdown(): Unit = {}

  override def delete(executor: Executor): Unit = {
    if (null != executor) {
      executor.close()
      idToEngines.remove(executor.getId)
    }else {
      logger.info("remove executor failed!")
    }
  }
}
