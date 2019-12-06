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

package com.webank.wedatasphere.dss.flow.execution.entrance

import java.util

import com.webank.wedatasphere.dss.flow.execution.entrance.node.NodeRunner
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.scheduler.queue.SchedulerEventState
import com.webank.wedatasphere.linkis.scheduler.queue.SchedulerEventState.SchedulerEventState

/**
 * created by chaogefeng on 2019/11/5 15:59
 * Description:
 */
class FlowContextImpl extends FlowContext with Logging {

  val runningNodes: util.Map[String, NodeRunner] = new util.HashMap[String, NodeRunner]

  val succeedNodes: util.Map[String, NodeRunner] = new util.HashMap[String, NodeRunner]

  val pendingNodes: util.Map[String, NodeRunner] = new util.HashMap[String, NodeRunner]

  val failedNodes: util.Map[String, NodeRunner] = new util.HashMap[String, NodeRunner]

  val skippedNodes: util.Map[String, NodeRunner] = new util.HashMap[String, NodeRunner]

  val scheduledNodes: util.Map[String, NodeRunner] = new util.HashMap[String, NodeRunner]

  val flowStatus:SchedulerEventState = SchedulerEventState.Inited

  override def getRunningNodes:  util.Map[String, NodeRunner] = this.runningNodes

  override def getSucceedNodes:  util.Map[String, NodeRunner] = this.succeedNodes

  override def getPendingNodes:  util.Map[String, NodeRunner] =this.pendingNodes

  override def getFailedNodes:  util.Map[String, NodeRunner] = this.failedNodes

  override def getSkippedNodes:  util.Map[String, NodeRunner] = this.skippedNodes

  override def getScheduledNodes:  util.Map[String, NodeRunner] = this.scheduledNodes

  override def getFlowStatus: SchedulerEventState = this.flowStatus

  override def isNodeCompleted(nodeName: String): Boolean = {
    getSkippedNodes.containsKey(nodeName) || getSucceedNodes.containsKey(nodeName) || getFailedNodes.containsKey(nodeName)
  }

}