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

package com.webank.wedatasphere.dss.flow.execution.entrance.resolver

import java.util

import com.webank.wedatasphere.dss.flow.execution.entrance.{FlowContext}
import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob

import scala.collection.JavaConversions._
import com.webank.wedatasphere.linkis.common.utils.Logging
import org.springframework.stereotype.Component


/**
  * created by chaogefeng on 2019/11/5 10:24
  * Description:
  */
@Component
class FlowDependencyResolverImpl extends FlowDependencyResolver with Logging {
  override def resolvedFlow(flowJob: FlowEntranceJob) = {

    info(s"${flowJob.getId} Start to get executable node")

    val flowContext: FlowContext = flowJob.getFlowContext
    val nodes  = flowContext.getPendingNodes.toMap.values.map(_.getNode)

    def  isAllParentDependencyCompleted(parents:util.List[String]): Boolean = {
      for (parent <- parents){
        if( ! flowContext.isNodeCompleted(parent)) return false
      }
      true
    }
    nodes.foreach{ node =>
      val nodeName = node.getName
      def isCanExecutable: Boolean = {
        (flowContext.getPendingNodes.containsKey(nodeName)
          && !FlowContext.isNodeRunning(nodeName, flowContext)
          && !flowContext.isNodeCompleted(nodeName)
          && isAllParentDependencyCompleted(node.getDependencys))
      }
      if (isCanExecutable) flowContext synchronized {
        if (isCanExecutable) flowContext.getPendingNodes.get(nodeName).tunToScheduled()
      }
    }
    info(s"${flowJob.getId} Finished to get executable node(${flowContext.getScheduledNodes.size()})")

  }


}