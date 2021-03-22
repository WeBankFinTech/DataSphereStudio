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

package com.webank.wedatasphere.linkis.manager.engineplugin.appconn.executor

import java.util

import com.webank.wedatasphere.dss.standard.app.development.execution.conf.RefExecutionConfiguration
import com.webank.wedatasphere.dss.standard.app.development.execution.core.LinkisJob
import com.webank.wedatasphere.dss.standard.app.development.execution.exception.AppConnExecutionErrorException
import com.webank.wedatasphere.linkis.common.exception.ErrorException
import com.webank.wedatasphere.linkis.common.utils.Utils
import com.webank.wedatasphere.linkis.engineconn.computation.executor.execute.EngineExecutionContext
import com.webank.wedatasphere.linkis.governance.common.entity.task.{RequestPersistTask, RequestQueryTask, ResponsePersist}
import com.webank.wedatasphere.linkis.manager.label.entity.Label
import com.webank.wedatasphere.linkis.protocol.UserWithCreator
import com.webank.wedatasphere.linkis.rpc.Sender
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper

/**
  * 只适配Linkis1.0-RC1的JobHistory
  * Created by enjoyyin on 2021/1/25.
  */
class ExecutionRequestRefContextImpl(engineExecutorContext: EngineExecutionContext, userWithCreator: UserWithCreator)
  extends AbstractExecutionRequestRefContext(engineExecutorContext, userWithCreator) {

  private val jobHistorySender:Sender = Sender.getSender(RefExecutionConfiguration.JOB_HISTORY_APPLICATION_NAME.getValue)

  override def fetchLinkisJob(jobId: Long): LinkisJob = {
    val requestQueryTask = new RequestQueryTask()
    requestQueryTask.setTaskID(jobId)
    requestQueryTask.setSource(null)
    val linkisJob:RequestPersistTask = Utils.tryThrow(
      jobHistorySender.ask(requestQueryTask) match {
        case responsePersist: ResponsePersist =>
          val status = responsePersist.getStatus
          if (status != 0){
            error(s"Fetch linkisJob from jobHistory failed, errorMsg: ${responsePersist.getMsg}.")
            throw new AppConnExecutionErrorException(95541, s"Fetch linkisJob from jobHistory failed, errorMsg: ${responsePersist.getMsg}.")
          } else {
            val data = responsePersist.getData
            data.get("task") match {
              case tasks: util.List[RequestPersistTask] =>
                if (tasks.size() > 0) tasks.get(0) else throw new AppConnExecutionErrorException(95542, s"query from jobhistory not a correct List type taskId is $jobId")


              case _ => throw new AppConnExecutionErrorException(95541, s"query from jobhistory not a correct List type taskId is $jobId")
            }
          }


        case r =>
          error(s"Fetch linkisJob from jobHistory incorrectly, response is $r.")
          throw new AppConnExecutionErrorException(95541, s"Fetch linkisJob from jobHistory incorrectly, response is $r.")
      }
    ){
      case errorException: ErrorException => errorException
      case e: Exception =>
        new AppConnExecutionErrorException(95541, s"query taskId $jobId error.", e)
    }
    new LinkisJob {
      override def getResultLocation: String = linkisJob.getResultLocation
      override def getSubmitUser: String = linkisJob.getSubmitUser
      override def getExecuteUser: String = linkisJob.getUmUser
      override def getStatus: String = linkisJob.getStatus
      override def getSource: util.Map[String, String] = linkisJob.getSource
      override def getParams: util.Map[String, AnyRef] = linkisJob.getParams
      override def getLabels: util.List[Label[_]] = linkisJob.getLabels
    }
  }

}
