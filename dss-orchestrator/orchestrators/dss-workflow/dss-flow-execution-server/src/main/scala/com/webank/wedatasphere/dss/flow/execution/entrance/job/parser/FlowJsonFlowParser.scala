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

package com.webank.wedatasphere.dss.flow.execution.entrance.job.parser
import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob
import com.webank.wedatasphere.dss.flow.execution.entrance.utils.FlowExecutionUtils
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow
import com.webank.wedatasphere.dss.workflow.core.WorkflowFactory
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.governance.common.entity.job.JobRequest
import org.springframework.core.annotation.Order

import scala.collection.convert.wrapAsScala._


@Order(2)
class FlowJsonFlowParser extends FlowEntranceJobParser with Logging {

  override def parse(flowEntranceJob: FlowEntranceJob): Unit = {
    info(s"Start to parse flow of Job(${flowEntranceJob.getId}).")
    val codeLanguageLabel = FlowExecutionUtils.getRunTypeLabel(flowEntranceJob.getJobRequest().getLabels)
    if("flowjson" == codeLanguageLabel.getCodeType.toLowerCase) {
      val workflow = WorkflowFactory.INSTANCE.getJsonToFlowParser.parse(createDSSFlow(flowEntranceJob.getJobRequest()))
      flowEntranceJob.setFlow(workflow)
      info(s"Finished to parse flow of Job(${flowEntranceJob.getId}).")
    }
  }

  private def createDSSFlow(jobRequest: JobRequest): DSSFlow = {
    val dssFlow = new DSSFlow
    dssFlow.setFlowJson(jobRequest.getExecutionCode)
    dssFlow.setCreator(jobRequest.getExecuteUser)
    val name = if(jobRequest.getSource != null) jobRequest.getSource.values().mkString("-")
      else jobRequest.getSubmitUser + "-Flow"
    dssFlow.setName(name)
    dssFlow
  }

}
