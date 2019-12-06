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

package com.webank.wedatasphere.dss.flow.execution.entrance.job

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.{SchedulerFlow, SchedulerNode}
import com.webank.wedatasphere.dss.common.entity.project.DWSProject
import com.webank.wedatasphere.dss.flow.execution.entrance.exception.FlowExecutionErrorException
import com.webank.wedatasphere.dss.flow.execution.entrance.{FlowContext, FlowContextImpl}
import com.webank.wedatasphere.dss.flow.execution.entrance.listener.NodeRunnerListener
import com.webank.wedatasphere.dss.flow.execution.entrance.node.{NodeExecutionState, NodeRunner}
import com.webank.wedatasphere.dss.flow.execution.entrance.node.NodeExecutionState.NodeExecutionState
import com.webank.wedatasphere.linkis.common.log.LogUtils
import com.webank.wedatasphere.linkis.common.utils.Utils
import com.webank.wedatasphere.linkis.entrance.execute.StorePathExecuteRequest
import com.webank.wedatasphere.linkis.entrance.job.EntranceExecutionJob
import com.webank.wedatasphere.linkis.protocol.query.RequestPersistTask
import com.webank.wedatasphere.linkis.scheduler.executer.{ErrorExecuteResponse, ExecuteRequest, SuccessExecuteResponse}
import com.webank.wedatasphere.linkis.scheduler.queue.{Job, SchedulerEventState}
import com.webank.wedatasphere.linkis.scheduler.queue.SchedulerEventState.Running

import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

class FlowEntranceJob extends EntranceExecutionJob with NodeRunnerListener {

  private var flow: SchedulerFlow = _

  private val flowContext: FlowContext = new FlowContextImpl


   @BeanProperty   var dwsProject: DWSProject = _

  def setFlow(flow: SchedulerFlow): Unit = this.flow = flow

  def getFlow: SchedulerFlow = this.flow

  def getFlowContext: FlowContext = this.flowContext

  private val STATUS_CHANGED_LOCK = "STATUS_CHANGED_LOCK".intern()


  override def jobToExecuteRequest(): ExecuteRequest = {
    new ExecuteRequest with StorePathExecuteRequest with FlowExecutionRequest {
      override val code: String = FlowEntranceJob.this.getTask match {
        case requestPersistTask: RequestPersistTask => requestPersistTask.getExecutionCode
        case _ => null
      }
      override val storePath: String = FlowEntranceJob.this.getTask match {
        case requestPersistTask: RequestPersistTask => requestPersistTask.getResultLocation
        case _ => ""
      }
      override val job: Job = FlowEntranceJob.this
    }
  }

  override def run(): Unit = {
    setResultSize(0)
    if (!isScheduled) return
    startTime = System.currentTimeMillis
    Utils.tryAndWarn(transition(Running))
    getExecutor.execute(jobToExecuteRequest())
    }


  override def onStatusChanged(fromState: NodeExecutionState, toState: NodeExecutionState, node: SchedulerNode): Unit = {

    val nodeName = node.getDWSNode.getName
    toState match {
      case NodeExecutionState.Failed =>
        printLog(s"Failed to execute node($nodeName),prepare to kill flow job", "ERROR")
        if(NodeExecutionState.isRunning(fromState))
          FlowContext.changedNodeState(this.getFlowContext.getRunningNodes, this.getFlowContext.getFailedNodes, node, "node execute fail")
        this.kill()
        info(s"Succeed to kill flow job")
      case NodeExecutionState.Cancelled =>
        printLog(s"node($nodeName) has cancelled execution", "WARN")
        if(NodeExecutionState.isRunning(fromState))
          FlowContext.changedNodeState(this.getFlowContext.getRunningNodes, this.getFlowContext.getFailedNodes, node, "node has cancelled")
        if(NodeExecutionState.isScheduled(fromState))
          FlowContext.changedNodeState(this.getFlowContext.getScheduledNodes, this.getFlowContext.getFailedNodes, node, "node has cancelled")
      case NodeExecutionState.Skipped =>
        printLog(s"node($nodeName) has skipped execution from $fromState", "WARN")
        FlowContext.changedNodeState(this.getFlowContext.getScheduledNodes, this.getFlowContext.getSkippedNodes, node, "node has skipped")
        //Trigger the next execution
        this.STATUS_CHANGED_LOCK.synchronized {
          getExecutor.execute(jobToExecuteRequest())
        }
      case NodeExecutionState.Succeed =>
        printLog(s"Succeed to execute node($nodeName)", "INFO")
        if (NodeExecutionState.isRunning(fromState))
          FlowContext.changedNodeState(this.getFlowContext.getRunningNodes, this.getFlowContext.getSucceedNodes, node, "node execute success")
        //Trigger the next execution
        this.STATUS_CHANGED_LOCK.synchronized {
          getExecutor.execute(jobToExecuteRequest())
        }
      case NodeExecutionState.Running =>
        printLog(s"Start to execute node($nodeName) ", "INFO")
        if(NodeExecutionState.isScheduled(fromState))
          FlowContext.changedNodeState(this.getFlowContext.getScheduledNodes, this.getFlowContext.getRunningNodes, node, "node in running")

      case NodeExecutionState.Scheduled =>
        printLog(s"node($nodeName) from inited to scheduled", "INFO")
        if(NodeExecutionState.isInited(fromState))
          FlowContext.changedNodeState(this.getFlowContext.getPendingNodes, this.getFlowContext.getScheduledNodes, node, "node in scheduled")
      case _ =>
    }
    tryCompleted
  }

  def printLog(log:String, level:String): Unit = level match {
    case "INFO" =>
      info(log)
      getLogListener.foreach(_.onLogUpdate(this, LogUtils.generateInfo(log)))
    case "WARN" =>
      warn(log)
      getLogListener.foreach(_.onLogUpdate(this, LogUtils.generateWarn(log)))
    case "ERROR" =>
      error(log)
      getLogListener.foreach(_.onLogUpdate(this, LogUtils.generateERROR(log)))
    case _ =>
  }

  override def kill(): Unit = if (! SchedulerEventState.isCompleted(this.getState)) this synchronized  {
    if(! SchedulerEventState.isCompleted(this.getState)){
      Utils.tryAndWarn(this.killNodes)
      super.kill()
      transitionCompleted(ErrorExecuteResponse(s"execute job(${getId}) failed!", new FlowExecutionErrorException(90101, s"This Flow killed by user") ))
    }
  }

  override def cancel(): Unit = if (! SchedulerEventState.isCompleted(this.getState)) this synchronized  {
      if(! SchedulerEventState.isCompleted(this.getState)){
        Utils.tryAndWarn(this.killNodes)
        super.cancel()
        transitionCompleted(ErrorExecuteResponse(s"cancel job(${getId}) execution!", new FlowExecutionErrorException(90101, s"This Flow killed by user") ))
      }
  }

  def isFlowCompleted: Boolean = this.getFlowContext.getRunningNodes.isEmpty && this.getFlowContext.getPendingNodes.isEmpty && this.getFlowContext.getScheduledNodes.isEmpty


  def tryCompleted: Unit = {
    if (this.isFlowCompleted) {
      info(s"This Flow(${getId}) is Completed")
      /*transition(SchedulerEventState.Succeed)*/
      if(! SchedulerEventState.isCompleted(this.getState))
        transitionCompleted(SuccessExecuteResponse())
    }
  }

  def killNodes: Unit = {
    val runners = new ArrayBuffer[NodeRunner]()
    runners.addAll(this.getFlowContext.getRunningNodes.values())
    for (node <- runners) {
      node.cancel()
    }

  }




}
