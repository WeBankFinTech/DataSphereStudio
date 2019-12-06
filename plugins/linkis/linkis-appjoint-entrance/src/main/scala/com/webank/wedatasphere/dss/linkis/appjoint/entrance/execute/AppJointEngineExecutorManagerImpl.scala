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

package com.webank.wedatasphere.dss.linkis.appjoint.entrance.execute

import com.webank.wedatasphere.dss.linkis.appjoint.entrance.job.AppJointEntranceJob
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.entrance.execute._
import com.webank.wedatasphere.linkis.entrance.execute.impl.EntranceExecutorManagerImpl
import com.webank.wedatasphere.linkis.scheduler.executer.Executor
import com.webank.wedatasphere.linkis.scheduler.listener.ExecutorListener
import com.webank.wedatasphere.linkis.scheduler.queue.{GroupFactory, Job, SchedulerEvent}

import scala.concurrent.duration.Duration

class AppJointEngineExecutorManagerImpl(groupFactory: GroupFactory,
                                        engineBuilder: EngineBuilder,
                                        engineRequester: EngineRequester,
                                        engineSelector: EngineSelector,
                                        engineManager: EngineManager,
                                        entranceExecutorRulers: Array[EntranceExecutorRuler])
  extends EntranceExecutorManagerImpl(groupFactory, engineBuilder, engineRequester,
    engineSelector, engineManager, entranceExecutorRulers) with Logging {
  override def setExecutorListener(executorListener: ExecutorListener): Unit = super.setExecutorListener(executorListener)

  override def initialEntranceEngine(engine: EntranceEngine): Unit = super.initialEntranceEngine(engine)

  protected override def createExecutor(schedulerEvent: SchedulerEvent): EntranceEngine = schedulerEvent match {
    case job: AppJointEntranceJob =>
      val externalEngineParams = job.getParams
      val engine = new AppJointEntranceEngine(externalEngineParams)
      engine
    case _ => null
  }

  override protected def findExecutors(job: Job): Array[EntranceEngine] = super.findExecutors(job)

  override def askExecutor(schedulerEvent: SchedulerEvent): Option[Executor] = schedulerEvent match {
    case job: AppJointEntranceJob =>
      Some(createExecutor(schedulerEvent))
  }

  override def askExecutor(schedulerEvent: SchedulerEvent, wait: Duration): Option[Executor] = schedulerEvent match {
    case job: AppJointEntranceJob =>
      Some(createExecutor(schedulerEvent))
  }


  override def getById(id: Long): Option[Executor] = super.getById(id)

  override def getByGroup(groupName: String): Array[Executor] = super.getByGroup(groupName)

  protected override def delete(executor: Executor): Unit = super.delete(executor)

  override def shutdown(): Unit = super.shutdown()
}
