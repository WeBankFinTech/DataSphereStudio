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

package com.webank.wedatasphere.dss.flow.execution.entrance.utils

import java.util

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode
import com.webank.wedatasphere.dss.common.entity.Resource
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.WorkflowContext
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource

import scala.collection.JavaConversions._

/**
  * Created by peacewong on 2019/11/6.
  */
object FlowExecutionUtils {

  def isSkippedNode(node:SchedulerNode):Boolean = {
    FlowExecutionEntranceConfiguration.SKIP_NODES.getValue.split(",").exists(_.equalsIgnoreCase(node.getNodeType))
  }


  def isSignalNode(jobType: String) : Boolean = {
    FlowExecutionEntranceConfiguration.SIGNAL_NODES.getValue.split(",").exists(_.equalsIgnoreCase(jobType))
  }

  def isAppJointJob(engineType: String): Boolean = LinkisJobExecutionConfiguration.APPJOINT.equalsIgnoreCase(engineType)


  def getSharedInfo(jobProps: util.Map[String, String], nodeId: String): AnyRef = WorkflowContext.getAppJointContext.getValue(getSharedKey(jobProps, nodeId))

  def getSharedNodesAndJobId(jobProps: util.Map[String, String], nodes: Array[String]): util.Map[String, AnyRef] = {
    val map: util.Map[String, AnyRef] = new util.HashMap[String, AnyRef]
    for (nodeId <- nodes) {
      map.put(nodeId, getSharedInfo(jobProps, nodeId))
    }
    map
  }

  def getSharedKey(jobProps: util.Map[String, String], nodeId: String): String = {
    val projectId: String = jobProps.get(FlowExecutionEntranceConfiguration.PROJECT_NAME)
    val flowId: String = jobProps.get(FlowExecutionEntranceConfiguration.FLOW_NAME)
    val flowExecId: String = jobProps.get(FlowExecutionEntranceConfiguration.FLOW_EXEC_ID)
    projectId + "." + flowId + "." + flowExecId + "." + nodeId
  }


  def setSharedInfo(jobProps: util.Map[String, String], jobId: String): Unit = {
    val nodeId: String = jobProps.get(FlowExecutionEntranceConfiguration.JOB_ID)
    val shareNum: Int = jobProps.get(FlowExecutionEntranceConfiguration.SHARED_NODE_TOKEN).toInt
    WorkflowContext.getAppJointContext.setValue(getSharedKey(jobProps, nodeId), jobId, shareNum)
  }

  def resourcesAdaptation(resources: util.List[Resource]): util.ArrayList[BMLResource] = {
    val bmlResources = new util.ArrayList[BMLResource]()
    for (resource <- resources){
      bmlResources.add(resourceConvertToBMLResource(resource))
    }
    bmlResources
  }

  def resourceConvertToBMLResource(resource: Resource):BMLResource = {
    val bmlResource = new BMLResource()
    bmlResource.setFileName(resource.getFileName)
    bmlResource.setResourceId(resource.getResourceId)
    bmlResource.setVersion(resource.getVersion)
    bmlResource
  }

  def isReadNode(nodeType: String): Boolean = {
    FlowExecutionEntranceConfiguration.EMAIL_TYPE.equalsIgnoreCase(nodeType)
  }

}
