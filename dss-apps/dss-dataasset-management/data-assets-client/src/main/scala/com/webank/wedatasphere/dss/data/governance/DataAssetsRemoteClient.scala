package com.webank.wedatasphere.dss.data.governance

import com.webank.wedatasphere.dss.data.governance.request._
import com.webank.wedatasphere.dss.data.governance.response._


trait DataAssetsRemoteClient extends RemoteClient {
  def searchHiveTbl(action: SearchHiveTblAction): SearchHiveTblResult

  def searchHiveDb(action: SearchHiveDbAction): SearchHiveDbResult

  def getHiveTblPartition(action: GetHiveTblPartitionAction): GetHiveTblPartitionResult

  def getHiveTblBasic(action: GetHiveTblBasicAction): GetHiveTblBasicResult

  def getHiveTblCreate(action: GetHiveTblCreateAction): GetHiveTblCreateResult

  def createModelType(action: CreateModelTypeAction): CreateModelTypeResult

  def bindModelType(action: BindModelTypeAction): BindModelTypeResult

  def updateModelType(action: UpdateModelTypeAction): UpdateModelTypeResult

  def unBindModelType(action: UnBindModelTypeAction): UnBindModelTypeResult

  def createLabel(action: CreateLabelAction): CreateLabelResult

  def updateLabel(action: UpdateLabelAction): UpdateLabelResult

  def deleteLabel(action: DeleteLabelAction): DeleteLabelResult

  def bindLabel(action: BindLabelAction): BindLabelResult

  def unBindLabel(action: UnBindLabelAction): UnBindLabelResult

  def searchLabel(action: SearchLabelAction): SearchLabelResult

  def deleteModelType(action: DeleteModelTypeAction): DeleteModelTypeResult

  def searchHiveTblSize(action: HiveTblSizeAction): HiveTblSizeResult

  def searchHiveTblStats(action: HiveTblStatsAction): HiveTblStatsResult

  def getHiveTblPartInfoByNameResult(action: GetTblPartInfoByNameAction): GetHiveTblPartInfoByNameResult

}
