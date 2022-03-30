package com.webank.wedatasphere.dss.data.governance.impl

import com.webank.wedatasphere.dss.data.governance.request._
import com.webank.wedatasphere.dss.data.governance.response._
import com.webank.wedatasphere.dss.data.governance.{AbstractRemoteClient, DataAssetsRemoteClient}
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.dws.config.DWSClientConfig

class LinkisDataAssetsRemoteClient(clientConfig: DWSClientConfig) extends AbstractRemoteClient with DataAssetsRemoteClient {
  override protected val dwsHttpClient: DWSHttpClient = new DWSHttpClient(clientConfig, "DataAssets-Client")

  override def searchHiveTbl(action: SearchHiveTblAction): SearchHiveTblResult = execute(action).asInstanceOf[SearchHiveTblResult]

  override def getHiveTblPartition(action: GetHiveTblPartitionAction): GetHiveTblPartitionResult = execute(action).asInstanceOf[GetHiveTblPartitionResult]

  override def getHiveTblBasic(action: GetHiveTblBasicAction): GetHiveTblBasicResult = execute(action).asInstanceOf[GetHiveTblBasicResult]

  override def getHiveTblCreate(action: GetHiveTblCreateAction): GetHiveTblCreateResult = execute(action).asInstanceOf[GetHiveTblCreateResult]

  override def searchHiveDb(action: SearchHiveDbAction): SearchHiveDbResult = execute(action).asInstanceOf[SearchHiveDbResult]

  override def createModelType(action: CreateModelTypeAction): CreateModelTypeResult = execute(action).asInstanceOf[CreateModelTypeResult]

  override def bindModelType(action: BindModelTypeAction): BindModelTypeResult = execute(action).asInstanceOf[BindModelTypeResult]

  override def updateModelType(action: UpdateModelTypeAction): UpdateModelTypeResult = execute(action).asInstanceOf[UpdateModelTypeResult]

  override def unBindModelType(action: UnBindModelTypeAction): UnBindModelTypeResult = execute(action).asInstanceOf[UnBindModelTypeResult]

  override def deleteModelType(action: DeleteModelTypeAction): DeleteModelTypeResult = execute(action).asInstanceOf[DeleteModelTypeResult]

  override def searchHiveTblSize(action: HiveTblSizeAction): HiveTblSizeResult = execute(action).asInstanceOf[HiveTblSizeResult]

  override def searchHiveTblStats(action: HiveTblStatsAction): HiveTblStatsResult = execute(action).asInstanceOf[HiveTblStatsResult]

  override def createLabel(action: CreateLabelAction): CreateLabelResult = execute(action).asInstanceOf[CreateLabelResult]

  override def updateLabel(action: UpdateLabelAction): UpdateLabelResult = execute(action).asInstanceOf[UpdateLabelResult]

  override def deleteLabel(action: DeleteLabelAction): DeleteLabelResult = execute(action).asInstanceOf[DeleteLabelResult]

  override def bindLabel(action: BindLabelAction): BindLabelResult = execute(action).asInstanceOf[BindLabelResult]

  override def unBindLabel(action: UnBindLabelAction): UnBindLabelResult = execute(action).asInstanceOf[UnBindLabelResult]

  override def searchLabel(action: SearchLabelAction): SearchLabelResult = execute(action).asInstanceOf[SearchLabelResult]

  override def getHiveTblPartInfoByNameResult(action: GetTblPartInfoByNameAction): GetHiveTblPartInfoByNameResult = execute(action).asInstanceOf[GetHiveTblPartInfoByNameResult]

}
