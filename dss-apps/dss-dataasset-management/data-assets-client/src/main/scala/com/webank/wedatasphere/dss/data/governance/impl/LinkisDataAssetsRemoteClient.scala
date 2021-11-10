package com.webank.wedatasphere.dss.data.governance.impl

import com.webank.wedatasphere.dss.data.governance.request.{CreateModelTypeAction, GetHiveTblBasicAction, GetHiveTblCreateAction, GetHiveTblPartitionAction, SearchHiveDbAction, SearchHiveTblAction}
import com.webank.wedatasphere.dss.data.governance.response.{CreateModelTypeResult, GetHiveTblBasicResult, GetHiveTblCreateResult, GetHiveTblPartitionResult, SearchHiveDbResult, SearchHiveTblResult}
import com.webank.wedatasphere.dss.data.governance.{AbstractRemoteClient, DataAssetsRemoteClient}
import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig

class LinkisDataAssetsRemoteClient(clientConfig: DWSClientConfig) extends AbstractRemoteClient with DataAssetsRemoteClient {
  override protected val dwsHttpClient: DWSHttpClient = new DWSHttpClient(clientConfig, "DataAssets-Client")

  override def searchHiveTbl(action: SearchHiveTblAction): SearchHiveTblResult = execute(action).asInstanceOf[SearchHiveTblResult]

  override def getHiveTblPartition(action: GetHiveTblPartitionAction): GetHiveTblPartitionResult = execute(action).asInstanceOf[GetHiveTblPartitionResult]

  override def getHiveTblBasic(action: GetHiveTblBasicAction): GetHiveTblBasicResult = execute(action).asInstanceOf[GetHiveTblBasicResult]

  override def getHiveTblCreate(action: GetHiveTblCreateAction): GetHiveTblCreateResult = execute(action).asInstanceOf[GetHiveTblCreateResult]

  override def searchHiveDb(action: SearchHiveDbAction): SearchHiveDbResult = execute(action).asInstanceOf[SearchHiveDbResult]

  override def createModelType(action: CreateModelTypeAction): CreateModelTypeResult = execute(action).asInstanceOf[CreateModelTypeResult]
}
