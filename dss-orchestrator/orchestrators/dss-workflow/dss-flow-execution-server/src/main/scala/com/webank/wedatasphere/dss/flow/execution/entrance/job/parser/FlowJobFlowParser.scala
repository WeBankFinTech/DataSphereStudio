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
import com.webank.wedatasphere.dss.common.protocol.RequestQueryWorkFlow
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob
import com.webank.wedatasphere.dss.flow.execution.entrance.utils.FlowExecutionUtils
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseQueryWorkflow
import com.webank.wedatasphere.dss.workflow.core.WorkflowFactory
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.rpc.Sender
import org.apache.commons.lang3.StringUtils
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


@Order(1)
@Component
class FlowJobFlowParser extends FlowEntranceJobParser with Logging {

  override def parse(flowEntranceJob: FlowEntranceJob): Unit = {
    info(s"Start to parse flow of Job(${flowEntranceJob.getId}).")
    val code = flowEntranceJob.jobToExecuteRequest().code
    val label = flowEntranceJob.getParams.getOrDefault(DSSCommonUtils.DSS_LABELS_KEY, DSSCommonUtils.ENV_LABEL_VALUE_DEV).asInstanceOf[String]
    val codeLanguageLabel = FlowExecutionUtils.getRunTypeLabel(flowEntranceJob.getJobRequest().getLabels)
    if("json" == codeLanguageLabel.getCodeType // 兼容老版本
      || "flow" == codeLanguageLabel.getCodeType) {
      val flowExecutionCode =DSSCommonUtils.COMMON_GSON.fromJson(code, classOf[java.util.Map[String, Any]])
      val flowId = flowExecutionCode.get("flowId").asInstanceOf[Number].longValue()
      val workflow = getDSSScheduleFlowById(flowEntranceJob.getUser, flowId, label)
      flowEntranceJob.setFlow(workflow)
      info(s"Finished to parse flow of Job(${flowEntranceJob.getId}).")
    }
  }

  private def getDSSScheduleFlowById(userName: String , flowId:Long, label:String): Workflow = {
    val req = RequestQueryWorkFlow(userName, flowId)
    val sendWorkflowName = FlowExecutionEntranceConfiguration.WORKFLOW_APPLICATION_NAME.getValue + label.toUpperCase
    logger.info("Send query workflow json to "+ sendWorkflowName)
    val response: ResponseQueryWorkflow= Sender.getSender(sendWorkflowName)
                                               .ask(req).asInstanceOf[ResponseQueryWorkflow]
    WorkflowFactory.INSTANCE.getJsonToFlowParser.parse(response.getDssFlow)
  }

}
