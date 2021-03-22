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

package com.webank.wedatasphere.dss.flow.execution.entrance.job.parser
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow
import com.webank.wedatasphere.dss.common.protocol.RequestQueryWorkFlow
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.FlowExecutionCode
import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob
import com.webank.wedatasphere.dss.flow.execution.entrance.parser.FlowExecutionProjectParser
import com.webank.wedatasphere.dss.flow.execution.entrance.tuning.FlowExecutionProjectTuning
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils
import com.webank.wedatasphere.dss.workflow.common.entity.DSSJsonFlow
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseQueryWorkflow
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.rpc.Sender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._


@Order(1)
@Component
class FlowJobFlowParser extends FlowEntranceJobParser with Logging {


  @Autowired
  var flowExecutionProjectParser: FlowExecutionProjectParser = _

  @Autowired
  var flowExecutionProjectTuning: FlowExecutionProjectTuning = _


  override def parse(flowEntranceJob: FlowEntranceJob): Unit = {
    info(s"${flowEntranceJob.getId} start to parse flow")
    val code = flowEntranceJob.jobToExecuteRequest().code
    val flowExecutionCode = LinkisJobExecutionUtils.gson.fromJson(code, classOf[FlowExecutionCode])

    getDSSScheduleFlowById(flowEntranceJob.getUser,flowExecutionCode.getFlowId) match {
      case dssFlow: SchedulerFlow => flowEntranceJob.setFlow(dssFlow)
    }
    info(s"${flowEntranceJob.getId} finished to parse flow")
  }

  private def getDSSScheduleFlowById(userName: String , flowId:Long):SchedulerFlow = {
    val req =  RequestQueryWorkFlow(userName,flowId)
    val response: ResponseQueryWorkflow= Sender.getSender(FlowExecutionEntranceConfiguration.WORKFLOW_APPLICATION_NAME.getValue)
                                               .ask(req).asInstanceOf[ResponseQueryWorkflow]
    val dssJsonFlow:DSSJsonFlow = flowExecutionProjectParser.toDSSJsonFlow(response.getDssFlow)
    val schedulerFlow: SchedulerFlow = flowExecutionProjectParser.invokeFlowParser(dssJsonFlow,flowExecutionProjectParser.getFlowParsers)
    flowExecutionProjectTuning.tuningSchedulerFlow(schedulerFlow)
  }




}
