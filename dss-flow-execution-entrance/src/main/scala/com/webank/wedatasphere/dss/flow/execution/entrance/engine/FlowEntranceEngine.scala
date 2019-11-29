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

package com.webank.wedatasphere.dss.flow.execution.entrance.engine

import java.util

import com.webank.wedatasphere.dss.flow.execution.entrance.exception.FlowExecutionErrorException
import com.webank.wedatasphere.dss.flow.execution.entrance.execution.FlowExecution
import com.webank.wedatasphere.dss.flow.execution.entrance.job.{FlowEntranceJob, FlowExecutionRequest}
import com.webank.wedatasphere.dss.flow.execution.entrance.job.parser.FlowEntranceJobParser
import com.webank.wedatasphere.dss.flow.execution.entrance.resolver.FlowDependencyResolver
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.entrance.EntranceServer
import com.webank.wedatasphere.linkis.entrance.annotation.EntranceServerBeanAnnotation
import com.webank.wedatasphere.linkis.entrance.execute.{EngineExecuteAsynReturn, EntranceEngine, StorePathExecuteRequest}
import com.webank.wedatasphere.linkis.entrance.persistence.EntranceResultSetEngine
import com.webank.wedatasphere.linkis.protocol.engine.{JobProgressInfo, RequestTask}
import com.webank.wedatasphere.linkis.scheduler.executer._
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._


@Component
class FlowEntranceEngine extends EntranceEngine(id = 0) with ConcurrentTaskOperateSupport with Logging {


  @Autowired
  private var flowParsers: util.List[FlowEntranceJobParser] = _

  @Autowired
  private var flowDependencyResolvers: util.List[FlowDependencyResolver] = _

  @Autowired
  private var flowExecution: FlowExecution = _


  protected def executeLine(code: String, storePath: String, alias: String): ExecuteResponse = {
    val realCode = code.trim()
    logger.info(s"flowExecutionEngine begins to run code:\n ${realCode.trim}")

    SuccessExecuteResponse()
  }

  override def execute(executeRequest: ExecuteRequest): ExecuteResponse = {
    executeRequest match {
      case flowExecutionRequest: FlowExecutionRequest =>
        flowExecutionRequest.job match {
          case job: FlowEntranceJob =>
            if (job.isCompleted) {
              info(s"Flow(${job.getId}) isCompleted status is ${job.getState}")
              return SuccessExecuteResponse()
            }
            Utils.tryCatch {
              if (null == job.getFlow) {
                Utils.tryCatch{
                  for (flowParser <- flowParsers) {
                    flowParser.parse(job)
                  }
                }{ t =>
                   throw new FlowExecutionErrorException(90101, s"Failed to parser flow of job(${job.getId})", t)
                }
              }
              for (flowDependencyResolver <- flowDependencyResolvers) {
                flowDependencyResolver.resolvedFlow(job)
              }
              flowExecution.runJob(job)
            } { t =>
              job.kill()
              error(s"Failed to execute job: ${job.getId}", t)
            }
        }
    }
    SuccessExecuteResponse()
  }

  override protected def callExecute(request: RequestTask): EngineExecuteAsynReturn = null

  override def kill(jobId: String): Boolean = {
    true
  }

  override def killAll(): Boolean = {
    true
  }

  override def pause(jobId: String): Boolean = {
    true
  }

  override def pauseAll(): Boolean = {
    true
  }

  override def resume(jobId: String): Boolean = {
    true
  }

  override def resumeAll(): Boolean = {
    true
  }

  override def close(): Unit = {

  }
}
