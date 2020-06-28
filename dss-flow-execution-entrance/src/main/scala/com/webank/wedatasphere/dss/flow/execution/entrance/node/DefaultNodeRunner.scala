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

import java.util

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.job.{JobTypeEnum, LinkisJob}
import com.webank.wedatasphere.dss.flow.execution.entrance.listener.NodeRunnerListener
import com.webank.wedatasphere.dss.flow.execution.entrance.log.FlowExecutionLog
import com.webank.wedatasphere.dss.flow.execution.entrance.node.NodeExecutionState.NodeExecutionState
import com.webank.wedatasphere.dss.linkis.node.execution.execution.impl.LinkisNodeExecutionImpl
import com.webank.wedatasphere.dss.linkis.node.execution.listener.LinkisExecutionListener
import com.webank.wedatasphere.linkis.common.exception.ErrorException
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}

/**
 * Created by johnnwang on 2019/11/5.
 */
class DefaultNodeRunner extends NodeRunner with Logging {

  private var node: SchedulerNode = _

  private var linkisJob: LinkisJob = _

  private var canceled: Boolean = false

  private var status: NodeExecutionState = NodeExecutionState.Inited

  private var nodeRunnerListener: NodeRunnerListener = _

  private var executedInfo: String = _

  private var startTime: Long = _

  private var nowTime:Long = _

  private var lastGetStatusTime: Long = 0

  override def getNode: SchedulerNode = this.node

  def setNode(schedulerNode: SchedulerNode): Unit = {
    this.node = schedulerNode
  }

  override def getLinkisJob: LinkisJob = this.linkisJob


  override def isCanceled: Boolean = this.canceled

  override def getStatus: NodeExecutionState = {
    this.status
  }

  override def isLinkisJobCompleted: Boolean = Utils.tryCatch{

    val interval = System.currentTimeMillis() - lastGetStatusTime

    if ( interval < FlowExecutionEntranceConfiguration.NODE_STATUS_INTERVAL.getValue){
      return false
    }

    lastGetStatusTime = System.currentTimeMillis()

    if(NodeExecutionState.isCompleted(getStatus)) return true
    val toState = NodeExecutionState.withName(LinkisNodeExecutionImpl.getLinkisNodeExecution.getState(this.linkisJob))
    if (NodeExecutionState.isCompleted(toState)) {
      val listener = LinkisNodeExecutionImpl.getLinkisNodeExecution.asInstanceOf[LinkisExecutionListener]
      listener.onStatusChanged(getStatus.toString, toState.toString, this.linkisJob)
      this.transitionState(toState)
      info(s"Finished to execute node of ${this.node.getName}")
      true
    } else {
      false
    }
  }{
    case e:ErrorException => logger.warn(s"failed to get ${this.node.getName} state", e)
      false
    case t :Throwable => logger.error(s"failed to get ${this.node.getName} state", t)
      true
  }

  override def setNodeRunnerListener(nodeRunnerListener: NodeRunnerListener): Unit = this.nodeRunnerListener = nodeRunnerListener

  override def getNodeRunnerListener: NodeRunnerListener = this.nodeRunnerListener

  override def run(): Unit = {
    info(s"start to run node of ${node.getName}")
    try {
      val jobProps = node.getDWSNode.getParams.remove(FlowExecutionEntranceConfiguration.PROPS_MAP) match {
        case propsMap: util.Map[String, String] => propsMap
        case _ => new util.HashMap[String, String]()
      }
      this.linkisJob = AppJointJobBuilder.builder().setNode(node).setJobProps(jobProps).build.asInstanceOf[LinkisJob]
      this.linkisJob.setLogObj(new FlowExecutionLog(this))
      //set start time
      this.setStartTime(System.currentTimeMillis())
      if (JobTypeEnum.EmptyJob == this.linkisJob.getJobType) {
        warn("This node is empty type")
        this.transitionState(NodeExecutionState.Succeed)
        return
      }

      LinkisNodeExecutionImpl.getLinkisNodeExecution.runJob(this.linkisJob)
      info(s"start to run node of ${node.getName}")
      /*LinkisNodeExecutionImpl.getLinkisNodeExecution.waitForComplete(this.linkisJob)
      val listener = LinkisNodeExecutionImpl.getLinkisNodeExecution.asInstanceOf[LinkisExecutionListener]
      val toState = LinkisNodeExecutionImpl.getLinkisNodeExecution.getState(this.linkisJob)
      listener.onStatusChanged(getStatus.toString, toState, this.linkisJob)
      this.transitionState(NodeExecutionState.withName(toState))
      info(s"Finished to execute node of ${node.getName}")*/
    } catch {
      case t: Throwable =>
        warn(s"Failed to execute node of ${node.getName}", t)
        this.transitionState(NodeExecutionState.Failed)

    }

  }


  override def cancel(): Unit = if (!this.canceled) this synchronized {
    if (this.canceled) return
    this.canceled = true
    if (this.linkisJob != null && this.linkisJob.getJobExecuteResult != null)
      LinkisNodeExecutionImpl.getLinkisNodeExecution.cancel(this.linkisJob)
    this.transitionState(NodeExecutionState.Cancelled)
    warn(s"This node(${node.getName}) has been canceled")
  }

  override def pause(): Unit = {}

  override def resume(): Boolean = true

  override def setStatus(nodeExecutionState: NodeExecutionState): Unit = this.status = nodeExecutionState

  override def getNodeExecutedInfo(): String = this.executedInfo

  override def setNodeExecutedInfo(info: String): Unit = this.executedInfo = info

  override def getStartTime(): Long = this.startTime

  override def setStartTime(startTime: Long): Unit = this.startTime = startTime

  override def getNowTime(): Long = this.nowTime

  override def setNowTime(nowTime: Long): Unit = this.nowTime = nowTime
}
