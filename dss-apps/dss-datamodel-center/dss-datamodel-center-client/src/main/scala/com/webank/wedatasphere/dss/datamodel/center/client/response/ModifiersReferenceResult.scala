package com.webank.wedatasphere.dss.datamodel.center.client.response

import com.webank.wedatasphere.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import com.webank.wedatasphere.linkis.httpclient.dws.response.DWSResult

import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/datamodel/modifiers/reference/(\\S+)")
class ModifiersReferenceResult extends DWSResult{
  @BeanProperty var result:Int = _
}
