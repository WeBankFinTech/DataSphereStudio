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
import com.webank.wedatasphere.linkis.common.utils.Logging

import scala.collection.JavaConversions._
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * Created by johnnwang on 2019/11/6.
 */

@Order(2)
@Component
class FlowJobNodeParser extends FlowEntranceJobParser with Logging{

  override def parse(flowEntranceJob: FlowEntranceJob): Unit = {
    info(s"${flowEntranceJob.getId} Start to parse node of flow")
    val project = flowEntranceJob.getDwsProject
    val flow = flowEntranceJob.getFlow
    val flowContext = flowEntranceJob.getFlowContext
    if(null == flow) throw new FlowExecutionErrorException(90101, "This fow of job is empty ")
    val nodes = flow.getSchedulerNodes
    for (node <- nodes) {

      val nodeName = node.getName
      val propsMap = new util.HashMap[String, String]()
      val proxyUser = if (node.getDWSNode.getUserProxy == null) flowEntranceJob.getUser else node.getDWSNode.getUserProxy
      propsMap.put(PROJECT_NAME, project.getName)
      propsMap.put(FLOW_NAME, flow.getName)
      propsMap.put(JOB_ID, nodeName)
      propsMap.put(FLOW_SUBMIT_USER, flowEntranceJob.getUser)
      info(s"${flowEntranceJob.getId} nodeName:${nodeName} node:${node.getNodeType}")
      propsMap.put(LinkisJobExecutionConfiguration.LINKIS_TYPE, node.getNodeType)

      propsMap.put(PROXY_USER, proxyUser)
      propsMap.put(COMMAND, LinkisJobExecutionUtils.gson.toJson(node.getDWSNode.getJobContent))

      var params = node.getDWSNode.getParams
      if (params == null) {
        params = new util.HashMap[String,AnyRef]()
        node.getDWSNode.setParams(params)
      }
      val flowVar = new util.HashMap[String, AnyRef]()
      val properties = flow.getFlowProperties
      if(properties != null) {
        for(proper <- properties){
          flowVar.putAll(proper)
        }
      }

      propsMap.put(FlowExecutionEntranceConfiguration.FLOW_EXEC_ID, flowEntranceJob.getId)

      params.put(PROPS_MAP, propsMap)
      params.put(FLOW_VAR_MAP, flowVar)
      params.put(PROJECT_RESOURCES, project.getProjectResources)
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
