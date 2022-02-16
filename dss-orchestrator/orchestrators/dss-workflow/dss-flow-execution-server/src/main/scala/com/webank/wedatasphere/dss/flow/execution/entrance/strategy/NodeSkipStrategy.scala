package com.webank.wedatasphere.dss.flow.execution.entrance.strategy

import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode

trait NodeSkipStrategy {
  /**
   * 判断节点是否需要跳过
   *
   * @param node             节点信息
   * @param paramsMap        执行入参
   * @param isReversedChoose 是否是反选
   * @return true/false
   *
   *
   */
  def isSkippedNode(node: WorkflowNode, paramsMap: java.util.Map[String, Any], isReversedChoose: Boolean): Boolean = {
    if (FlowExecutionEntranceConfiguration.SKIP_NODES.getValue.split(",").exists(_.equalsIgnoreCase(node.getNodeType))) {
      return true
    }
    val nodeIdObj = paramsMap.get("nodeID")
    if (nodeIdObj != null) {
      if (isReversedChoose) {
        return !nodeIdObj.toString.split(",").exists(_.equalsIgnoreCase(node.getId))
      } else {
        return nodeIdObj.toString.split(",").exists(_.equalsIgnoreCase(node.getId))
      }
    }
    false
  }
}