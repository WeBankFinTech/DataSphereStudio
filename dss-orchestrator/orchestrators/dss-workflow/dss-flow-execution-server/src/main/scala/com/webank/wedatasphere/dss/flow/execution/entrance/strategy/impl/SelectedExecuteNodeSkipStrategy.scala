package com.webank.wedatasphere.dss.flow.execution.entrance.strategy.impl


import java.util

import com.webank.wedatasphere.dss.flow.execution.entrance.strategy.NodeSkipStrategy
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode

/**
 * Description 选中执行时，跳过节点策略
 *
 * @Author elishazhang
 * @Date 2021/11/24
 */

class SelectedExecuteNodeSkipStrategy extends NodeSkipStrategy {
  override def isSkippedNode(node: WorkflowNode, paramsMap: util.Map[String, Any], isReversedChoose: Boolean): Boolean = {
    super.isSkippedNode(node, paramsMap, isReversedChoose = true)
  }

}
