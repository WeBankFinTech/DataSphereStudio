package com.webank.wedatasphere.dss.datamodel.center.client

import com.webank.wedatasphere.dss.datamodel.center.client.request._
import com.webank.wedatasphere.dss.datamodel.center.client.response._

trait DatamodelCenterRemoteClient extends RemoteClient {
  def themesReference(action: ThemesReferenceAction) : ThemesReferenceResult
  def layersReference(action: LayersReferenceAction) : LayersReferenceResult
  def modifiersReference(action: ModifiersReferenceAction):ModifiersReferenceResult
  def cyclesReference(action:CyclesReferenceAction) : CyclesReferenceResult
}
