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

package com.webank.wedatasphere.dss.flow.execution.entrance

import java.util

import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode
import com.webank.wedatasphere.dss.flow.execution.entrance.node.NodeRunner
import com.webank.wedatasphere.linkis.scheduler.queue.SchedulerEventState.SchedulerEventState

/**
  * Created by johnnwang on 2019/11/5.
  */
trait FlowContext {

  def getRunningNodes: util.Map[String, NodeRunner]

  def getSucceedNodes: util.Map[String, NodeRunner]

  def getPendingNodes: util.Map[String, NodeRunner]

  def getFailedNodes: util.Map[String, NodeRunner]

  def getSkippedNodes: util.Map[String, NodeRunner]

  def getScheduledNodes: util.Map[String, NodeRunner]

  def getFlowStatus: SchedulerEventState

  def isNodeCompleted(nodeName: String): Boolean
}

object FlowContext {

  def isNodeRunning(nodeName: String, flowContext: FlowContext):Boolean = {
    flowContext.getRunningNodes.containsKey(nodeName) || flowContext.getScheduledNodes.containsKey(nodeName)
  }

  def changedNodeState(fromMap: util.Map[String, NodeRunner],
                       toMap: util.Map[String, NodeRunner], node: SchedulerNode,info:String): Unit = {
    val nodeName = node.getDSSNode.getName
    if (fromMap.containsKey(nodeName)) {
      val runner = fromMap.get(nodeName)
      runner.setNodeExecutedInfo(info)
      toMap.put(nodeName, runner)
      fromMap.remove(nodeName)
    }
  }

  def convertView(nodeRunnerMap: java.util.Map[String, NodeRunner]): java.util.List[java.util.Map[String, Any]] = {
    val nodes = new util.ArrayList[java.util.Map[String, Any]]()
    val iterator = nodeRunnerMap.entrySet().iterator()
    while (iterator.hasNext) {
      val nodeRunner = iterator.next().getValue
      if (nodeRunner != null && nodeRunner.getNode != null) {
        val nodeView = new java.util.HashMap[String, Any]()
        val linkisJob = nodeRunner.getLinkisJob
        val node = nodeRunner.getNode
        if (linkisJob != null && linkisJob.getJobExecuteResult != null) {
          nodeView.put("execID", linkisJob.getJobExecuteResult.getExecID)
          nodeView.put("taskID", linkisJob.getJobExecuteResult.getTaskID())
        }
        nodeView.put("nodeID", node.getId)
        nodeView.put("info", nodeRunner.getNodeExecutedInfo())
        nodeView.put("startTime", nodeRunner.getStartTime())
        nodeView.put("nowTime", nodeRunner.getNowTime())
        nodes.add(nodeView)
      }
    }
    nodes
  }
}