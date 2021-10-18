package com.webank.wedatasphere.dss.data.governance.impl

import com.webank.wedatasphere.dss.data.governance.request.{GetHiveTblBasicAction, GetHiveTblPartitionAction, SearchHiveTblAction}
import com.webank.wedatasphere.dss.data.governance.response.{GetHiveTblBasicResult, GetHiveTblPartitionResult, SearchHiveTblResult}
import com.webank.wedatasphere.dss.data.governance.{AbstractRemoteClient, DataAssetsRemoteClient}
import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig

class LinkisDataAssetsRemoteClient(clientConfig: DWSClientConfig) extends AbstractRemoteClient with DataAssetsRemoteClient {
  override protected val dwsHttpClient: DWSHttpClient = new DWSHttpClient(clientConfig, "DataAssets-Client")

  override def searchHiveTbl(action: SearchHiveTblAction): SearchHiveTblResult = execute(action).asInstanceOf[SearchHiveTblResult]

  override def getHiveTblPartition(action: GetHiveTblPartitionAction): GetHiveTblPartitionResult = execute(action).asInstanceOf[GetHiveTblPartitionResult]

  override def getHiveTblBasic(action: GetHiveTblBasicAction): GetHiveTblBasicResult = execute(action).asInstanceOf[GetHiveTblBasicResult]
}
