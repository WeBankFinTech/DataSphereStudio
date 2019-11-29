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

package com.webank.wedatasphere.dss.flow.execution.entrance.node

import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob
import com.webank.wedatasphere.dss.flow.execution.entrance.FlowContext
import com.webank.wedatasphere.dss.flow.execution.entrance.listener.NodeRunnerListener
import com.webank.wedatasphere.dss.flow.execution.entrance.node.NodeExecutionState.NodeExecutionState
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import java.util.concurrent.Future

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode

/**
  * Created by peacewong on 2019/11/5.
  */
abstract class NodeRunner extends Runnable with Logging{

  private[flow] var future: Future[_] = _

  private var flowContext:FlowContext = _



  def getFlowContext:FlowContext = this.flowContext

  def setFlowContext(flowContext: FlowContext): Unit = {
     this.flowContext = flowContext
  }

  def getNode:SchedulerNode

  def setNode(node: SchedulerNode):Unit

  def getLinkisJob: LinkisJob

  def cancel(): Unit

  def pause(): Unit

  def resume(): Boolean

  def isCanceled: Boolean

  def getStatus: NodeExecutionState

  def isLinkisJobCompleted: Boolean

  def setStatus(nodeExecutionState: NodeExecutionState): Unit

  def setNodeRunnerListener(nodeRunnerListener: NodeRunnerListener): Unit

  def getNodeRunnerListener: NodeRunnerListener

  def getNodeExecutedInfo(): String

  def setNodeExecutedInfo(info: String ):Unit

  def getStartTime(): Long

  def setStartTime(startTime: Long): Unit

  protected def transitionState(toState: NodeExecutionState): Unit = Utils.tryAndWarn{
    if (getStatus == toState) return
    info(s"from state $getStatus to $toState")
    this.getNodeRunnerListener.onStatusChanged(getStatus, toState, this.getNode)
    this.setStatus(toState)
  }


  def tunToScheduled(): Boolean = if (! NodeExecutionState.isInited(this.getStatus)) false else this synchronized {
    if (! NodeExecutionState.isInited(this.getStatus)) false else {
      transitionState(NodeExecutionState.Scheduled)
      true
    }
  }

  def fromScheduledTunToState(state: NodeExecutionState): Boolean = if (! NodeExecutionState.isScheduled(this.getStatus) ) false else this synchronized {
    if (! NodeExecutionState.isScheduled(this.getStatus)) false else {
      transitionState(state)
      true
    }
  }


}
