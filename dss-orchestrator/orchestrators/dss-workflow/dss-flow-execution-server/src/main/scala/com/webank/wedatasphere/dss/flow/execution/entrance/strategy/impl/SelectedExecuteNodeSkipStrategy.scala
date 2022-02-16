package com.webank.wedatasphere.dss.flow.execution.entrance.strategy.impl


import java.util

import com.webank.wedatasphere.dss.flow.execution.entrance.strategy.NodeSkipStrategy
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode

class SelectedExecuteNodeSkipStrategy extends NodeSkipStrategy {
  override def isSkippedNode(node: WorkflowNode, paramsMap: util.Map[String, Any], isReversedChoose: Boolean): Boolean = {
    super.isSkippedNode(node, paramsMap, isReversedChoose = true)
  }

}
