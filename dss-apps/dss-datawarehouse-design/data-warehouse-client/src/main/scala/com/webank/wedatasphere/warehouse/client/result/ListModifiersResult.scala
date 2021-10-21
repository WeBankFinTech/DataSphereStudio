package com.webank.wedatasphere.warehouse.client.result

import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import com.webank.wedatasphere.linkis.httpclient.dws.response.DWSResult
import com.webank.wedatasphere.warehouse.domain.DwModifierVO

import java.util
import scala.beans.BeanProperty

@DWSHttpMessageResult(value = "/api/rest_j/v\\d+/data-warehouse/modifiers/all")
class ListModifiersResult extends DWSResult {
  @BeanProperty var list: java.util.List[java.util.Map[String, Any]] = _

  def getAll: util.List[DwModifierVO] = {
    import scala.collection.JavaConverters._
    list.asScala.map(x=>{
      val str = DWSHttpClient.jacksonJson.writeValueAsString(x)
      DWSHttpClient.jacksonJson.readValue(str, classOf[DwModifierVO])
    }).asJava
  }
}