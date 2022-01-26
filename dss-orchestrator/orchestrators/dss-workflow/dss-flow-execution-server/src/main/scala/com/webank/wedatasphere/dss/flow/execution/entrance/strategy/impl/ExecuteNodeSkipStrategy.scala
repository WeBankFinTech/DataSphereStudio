package com.webank.wedatasphere.dss.flow.execution.entrance.strategy.impl

import java.util

import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.flow.execution.entrance.strategy.NodeSkipStrategy
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode

/**
 * Description 执行时，跳过节点策略
 *
 * @Author elishazhang
 * @Date 2021/11/24
 */

class ExecuteNodeSkipStrategy extends NodeSkipStrategy {
  override def isSkippedNode(node: WorkflowNode, paramsMap: util.Map[String, Any], isReversedChoose: Boolean): Boolean = {
    FlowExecutionEntranceConfiguration.SKIP_NODES.getValue.split(",").exists(_.equalsIgnoreCase(node.getNodeType))
  }
}
