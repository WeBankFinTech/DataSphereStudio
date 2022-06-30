package com.webank.wedatasphere.dss.flow.execution.entrance.strategy

import com.webank.wedatasphere.dss.flow.execution.entrance.enums.ExecuteStrategyEnum
import com.webank.wedatasphere.dss.flow.execution.entrance.strategy.impl.{ExecuteNodeSkipStrategy, ReExecuteNodeSkipStrategy, SelectedExecuteNodeSkipStrategy}

object StrategyFactory {

  def getNodeSkipStrategy(executeStrategy: String): NodeSkipStrategy = {
    val skipStrategy = ExecuteStrategyEnum.getEnum(executeStrategy) match {
      case ExecuteStrategyEnum.IS_RE_EXECUTE => new ReExecuteNodeSkipStrategy
      case ExecuteStrategyEnum.IS_SELECTED_EXECUTE => new SelectedExecuteNodeSkipStrategy
      case ExecuteStrategyEnum.IS_EXECUTE => new ExecuteNodeSkipStrategy
      case _ => new ExecuteNodeSkipStrategy
    }
    skipStrategy
  }
}
