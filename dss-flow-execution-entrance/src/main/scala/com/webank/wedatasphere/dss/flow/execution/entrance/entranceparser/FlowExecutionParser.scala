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

package com.webank.wedatasphere.dss.flow.execution.entrance.entranceparser


import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.entrance.parser.CommonEntranceParser
import com.webank.wedatasphere.linkis.protocol.query.RequestPersistTask
import com.webank.wedatasphere.linkis.protocol.task.Task
import com.webank.wedatasphere.linkis.scheduler.queue.Job
import scala.collection.JavaConversions._

/**
 * created by chaogefeng on 2019/11/4 17:11
 * Description:
 */
class FlowExecutionParser extends CommonEntranceParser with  Logging {
  logger.info("FlowExecutionParser Registered")

  override def parseToJob(task: Task): Job = {
    val job = new FlowEntranceJob
    task match {
      case requestPersistTask: RequestPersistTask =>
        job.setTask(task)
        job.setUser(requestPersistTask.getUmUser)
        job.setCreator(requestPersistTask.getRequestApplicationName)
        job.setParams(requestPersistTask.getParams.toMap[String, Any] )
        job.setEntranceListenerBus(getEntranceContext.getOrCreateEventListenerBus)
        job.setListenerEventBus(null)
        job.setProgress(0f)
    }
    job
  }
}
