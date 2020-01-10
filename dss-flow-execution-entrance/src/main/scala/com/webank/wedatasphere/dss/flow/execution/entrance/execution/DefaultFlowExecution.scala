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

package com.webank.wedatasphere.dss.flow.execution.entrance.execution

import java.util.concurrent.{Executors, LinkedBlockingQueue, TimeUnit}

import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob
import com.webank.wedatasphere.dss.flow.execution.entrance.node.{NodeExecutionState, NodeRunner}
import com.webank.wedatasphere.dss.flow.execution.entrance.utils.FlowExecutionUtils
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import org.springframework.stereotype.Service

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer


/**
 * Created by johnnwang on 2019/11/5.
 */
@Service
class DefaultFlowExecution extends FlowExecution with Logging {


  private val nodeRunnerQueue: LinkedBlockingQueue[NodeRunner] = new LinkedBlockingQueue[NodeRunner]()

  private val scheduledThreadPool = Executors.newScheduledThreadPool(FlowExecutionEntranceConfiguration.FLOW_EXECUTION_SCHEDULER_POOL_SIZE.getValue)

  private var pollerCount = 0

  override def runJob(flowEntranceJob: FlowEntranceJob): Unit = {

    info(s"${flowEntranceJob.getId} Start to run executable node")
    val scheduledNodes = flowEntranceJob.getFlowContext.getScheduledNodes

    if( ! scheduledNodes.isEmpty) {
      // get executableNodes
      flowEntranceJob.getFlowContext synchronized{
        val runners = new ArrayBuffer[NodeRunner]()
        runners.addAll(scheduledNodes.values())
        val runningNodes = new ArrayBuffer[NodeRunner]()
        runners.foreach{ runner =>
          if ( ! FlowExecutionUtils.isSkippedNode(runner.getNode)){
            info(s"scheduled node ${runner.getNode.getName} to running")
            runner.fromScheduledTunToState(NodeExecutionState.Running)
            // submit node runner
            runningNodes.add(runner)
          } else {
            info(s"This node ${runner.getNode.getDWSNode.getName} Skipped in execution")
            runner.fromScheduledTunToState(NodeExecutionState.Skipped)
          }
        }
        info(s"${flowEntranceJob.getId} Submit nodes(${runningNodes.size}) to running")
        runningNodes.foreach{ node =>
          node.run()
          nodeRunnerQueue.put(node)
          if (pollerCount < FlowExecutionEntranceConfiguration.NODE_STATUS_POLLER_THREAD_SIZE.getValue){
            scheduledThreadPool.scheduleAtFixedRate(new NodeExecutionStatusPoller(nodeRunnerQueue), 1,
              FlowExecutionEntranceConfiguration.NODE_STATUS_POLLER_SCHEDULER_TIME.getValue ,TimeUnit.SECONDS)
            pollerCount = pollerCount + 1
          }
        }
      }
    } else {
      info(s"${flowEntranceJob.getId} no executable nodes")
      flowEntranceJob.tryCompleted
    }
    info(s"${flowEntranceJob.getId} finished to run node")
  }
}
