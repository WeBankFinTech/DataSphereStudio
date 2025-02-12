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

import java.util
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration._
import com.webank.wedatasphere.dss.flow.execution.entrance.exception.FlowExecutionErrorException
import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob
import com.webank.wedatasphere.dss.flow.execution.entrance.node.DefaultNodeRunner
import com.webank.wedatasphere.dss.flow.execution.entrance.utils.FlowExecutionUtils
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowWithContextImpl
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.common.variable.{CustomDateType, CustomHourType}
import org.apache.linkis.common.variable.DateTypeUtils.{getCurHour, getToday, getYesterday}
import org.apache.linkis.entrance.execute.EntranceJob
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._


@Order(10)
@Component
class FlowJobNodeParser extends FlowEntranceJobParser with Logging{

  override def parse(flowEntranceJob: FlowEntranceJob): Unit = {
    info(s"${flowEntranceJob.getId} Start to parse node of flow.")
    val flow = flowEntranceJob.getFlow
    val flowContext = flowEntranceJob.getFlowContext
    if(null == flow) throw new FlowExecutionErrorException(90101, "This fow of job is empty ")
    val nodes = flow.getWorkflowNodes
    for (node <- nodes) {

      val nodeName = node.getName
      val propsMap = new util.HashMap[String, String]()
      val proxyUser = if (node.getDSSNode.getUserProxy == null) flowEntranceJob.getUser else node.getDSSNode.getUserProxy
      propsMap.put(FLOW_NAME, flow.getName)
      propsMap.put(JOB_ID, nodeName)
      val  projectName =flowEntranceJob.asInstanceOf[EntranceJob].getJobRequest.getSource.get("projectName")
      propsMap.put(PROJECT_NAME,projectName)
      propsMap.put(FLOW_SUBMIT_USER, flowEntranceJob.getUser)
      info(s"Job(${flowEntranceJob.getId}) with nodeName: $nodeName and node: ${node.getNodeType}.")
      propsMap.put(LinkisJobExecutionConfiguration.LINKIS_TYPE, node.getNodeType)

      propsMap.put(PROXY_USER, proxyUser)
      propsMap.put(COMMAND, LinkisJobExecutionUtils.gson1.toJson(node.getDSSNode.getJobContent))

      var params = node.getDSSNode.getParams
      if (params == null) {
        params = new util.HashMap[String,AnyRef]()
        node.getDSSNode.setParams(params)
      }
      val flowVar = new util.HashMap[String, AnyRef]()
      val properties = flow.getFlowProperties
      if(properties != null) {
        for(proper <- properties){
          flowVar.putAll(proper)
        }
      }

      propsMap.put(FlowExecutionEntranceConfiguration.FLOW_EXEC_ID, flowEntranceJob.getId)

      flow match {
        case workflow: WorkflowWithContextImpl =>
          propsMap.put(CONTEXT_ID, workflow.getContextID)
        case _ =>
      }
      var run_date: CustomDateType = null
      if (!flowVar.containsKey("run_date")) {
        //如果用户没有在工作流里定义run_date，则取工作流开始运行的时间，自动加到参数里
        run_date = new CustomDateType(getYesterday(false), false)
        flowVar.put("run_date", run_date.toString())
      } else {
        //如果用户自定义了run_date，则以用户自定义为准
        Utils.tryCatch {
          val run_date_str = flowVar.get("run_date").asInstanceOf[String]
          run_date = new CustomDateType(run_date_str, false)
        }(t => {
          logger.error("parse run_date  failed, we will get System run_date ", t)
          run_date = new CustomDateType(getYesterday(false), false)
        })
      }
      val run_today = new CustomDateType(getToday(std = false, run_date + 1), false)
      val cur_hour=getCurHour(std = false, run_today.toString)
      val run_today_h = new CustomHourType(cur_hour, false)
      flowVar.put("run_today_h", run_today_h.toString())
      flowVar.put("run_today_hour", run_today_h.toString())
      val run_today_h_std = new CustomHourType(cur_hour, true)
      flowVar.put("run_today_h_std", run_today_h_std.toString())
      flowVar.put("run_today_hour_std", run_today_h_std.toString())
      params.put(PROPS_MAP, propsMap)
      params.put(FLOW_VAR_MAP, flowVar)
      val flowNameAndResources = new util.HashMap[String, util.List[BMLResource]]()
      flowNameAndResources.put("flow." + flow.getName + LinkisJobExecutionConfiguration.RESOURCES_NAME, FlowExecutionUtils.resourcesAdaptation(flow.getFlowResources))
      params.put(FLOW_RESOURCES, flowNameAndResources)

      val pendingNodeMap = flowContext.getPendingNodes

      val nodeRunner = pendingNodeMap.getOrDefault(nodeName, new DefaultNodeRunner)
      nodeRunner.setNodeRunnerListener(flowEntranceJob)
      nodeRunner.setNode(node)
      pendingNodeMap.put(nodeName, nodeRunner)
    }
    info(s"${flowEntranceJob.getId} finished to parse node of flow")
  }

}
