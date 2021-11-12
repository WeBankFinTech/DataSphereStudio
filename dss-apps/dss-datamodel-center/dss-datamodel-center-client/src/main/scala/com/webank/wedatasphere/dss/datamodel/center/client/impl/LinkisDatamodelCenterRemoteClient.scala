package com.webank.wedatasphere.dss.datamodel.center.client.impl

import com.webank.wedatasphere.dss.datamodel.center.client.request.{CyclesReferenceAction, LayersReferenceAction, ModifiersReferenceAction, ThemesReferenceAction}
import com.webank.wedatasphere.dss.datamodel.center.client.response.{CyclesReferenceResult, LayersReferenceResult, ModifiersReferenceResult, ThemesReferenceResult}
import com.webank.wedatasphere.dss.datamodel.center.client.{AbstractRemoteClient, DatamodelCenterRemoteClient}
import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig

class LinkisDatamodelCenterRemoteClient(clientConfig: DWSClientConfig) extends AbstractRemoteClient with DatamodelCenterRemoteClient {
  override protected val dwsHttpClient: DWSHttpClient = new DWSHttpClient(clientConfig, "Datamodel-center-Client")

  override def themesReference(action: ThemesReferenceAction): ThemesReferenceResult = execute(action).asInstanceOf[ThemesReferenceResult]

  override def layersReference(action: LayersReferenceAction): LayersReferenceResult = execute(action).asInstanceOf[LayersReferenceResult]

  override def modifiersReference(action: ModifiersReferenceAction): ModifiersReferenceResult = execute(action).asInstanceOf[ModifiersReferenceResult]

  override def cyclesReference(action: CyclesReferenceAction): CyclesReferenceResult = execute(action).asInstanceOf[CyclesReferenceResult]
}
