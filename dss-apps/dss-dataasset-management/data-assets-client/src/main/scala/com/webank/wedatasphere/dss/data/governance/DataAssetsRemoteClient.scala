package com.webank.wedatasphere.dss.data.governance

import com.webank.wedatasphere.dss.data.governance.request.{GetHiveTblBasicAction, GetHiveTblCreateAction, GetHiveTblPartitionAction, SearchHiveDbAction, SearchHiveTblAction}
import com.webank.wedatasphere.dss.data.governance.response.{GetHiveTblBasicResult, GetHiveTblCreateResult, GetHiveTblPartitionResult, SearchHiveDbResult, SearchHiveTblResult}

trait DataAssetsRemoteClient extends RemoteClient {
  def searchHiveTbl(action:SearchHiveTblAction):SearchHiveTblResult
  def searchHiveDb(action:SearchHiveDbAction):SearchHiveDbResult
  def getHiveTblPartition(action:GetHiveTblPartitionAction):GetHiveTblPartitionResult
  def getHiveTblBasic(action:GetHiveTblBasicAction):GetHiveTblBasicResult
  def getHiveTblCreate(action:GetHiveTblCreateAction):GetHiveTblCreateResult
}
