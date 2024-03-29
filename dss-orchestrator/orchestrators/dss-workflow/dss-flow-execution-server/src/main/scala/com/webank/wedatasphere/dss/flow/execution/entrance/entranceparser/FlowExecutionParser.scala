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

package com.webank.wedatasphere.dss.flow.execution.entrance.entranceparser


import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob
import org.apache.linkis.common.utils.Logging
import org.apache.linkis.entrance.parser.CommonEntranceParser
import org.apache.linkis.entrance.persistence.PersistenceManager
import org.apache.linkis.governance.common.entity.job.JobRequest
import org.apache.linkis.governance.common.entity.task.RequestPersistTask
import org.apache.linkis.protocol.task.Task
import org.apache.linkis.scheduler.queue.Job
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._


class FlowExecutionParser(persistenceManager: PersistenceManager) extends CommonEntranceParser(persistenceManager) with Logging {

  override def parseToJob(jobRequest: JobRequest): Job = {
    val job = new FlowEntranceJob(persistenceManager)
    job.setJobRequest(jobRequest)
    job.setUser(jobRequest.getExecuteUser)
    job.setCreator("FlowEntrance")
    job.setParams(jobRequest.getParams)
    job.setEntranceListenerBus(getEntranceContext.getOrCreateEventListenerBus)
    job.setListenerEventBus(null)
    job.setProgress(0f)
    job.setEntranceContext(getEntranceContext)
    job
  }
}
