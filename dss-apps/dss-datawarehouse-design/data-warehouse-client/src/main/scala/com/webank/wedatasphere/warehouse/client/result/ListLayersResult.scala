package com.webank.wedatasphere.warehouse.client.result

import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.linkis.httpclient.dws.response.DWSResult
import com.webank.wedatasphere.warehouse.domain.DwLayerVO
import com.webank.wedatasphere.linkis.httpclient.dws.annotation.DWSHttpMessageResult

import java.util
import scala.beans.BeanProperty

@DWSHttpMessageResult(value = "/api/rest_j/v\\d+/data-warehouse/layers/all")
class ListLayersResult extends DWSResult {
  @BeanProperty var list: java.util.List[java.util.Map[String, Any]] = _

  def getAll: util.List[DwLayerVO] = {
    import scala.collection.JavaConverters._
    list.asScala.map(x=>{
      val str = DWSHttpClient.jacksonJson.writeValueAsString(x)
      DWSHttpClient.jacksonJson.readValue(str, classOf[DwLayerVO])
    }).asJava
  }

}
