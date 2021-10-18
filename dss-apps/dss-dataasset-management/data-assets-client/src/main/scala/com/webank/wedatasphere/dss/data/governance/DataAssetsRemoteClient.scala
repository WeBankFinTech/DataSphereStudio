package com.webank.wedatasphere.dss.data.governance

import com.webank.wedatasphere.dss.data.governance.request.{GetHiveTblBasicAction, GetHiveTblPartitionAction, SearchHiveTblAction}
import com.webank.wedatasphere.dss.data.governance.response.{GetHiveTblBasicResult, GetHiveTblPartitionResult, SearchHiveTblResult}

trait DataAssetsRemoteClient extends RemoteClient {
  def searchHiveTbl(action:SearchHiveTblAction):SearchHiveTblResult
  def getHiveTblPartition(action:GetHiveTblPartitionAction):GetHiveTblPartitionResult
  def getHiveTblBasic(action:GetHiveTblBasicAction):GetHiveTblBasicResult
}
