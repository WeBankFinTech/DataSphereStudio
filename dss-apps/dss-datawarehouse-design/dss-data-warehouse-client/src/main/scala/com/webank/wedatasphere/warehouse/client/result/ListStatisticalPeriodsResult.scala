package com.webank.wedatasphere.warehouse.client.result

import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult
import com.webank.wedatasphere.warehouse.domain.DwStatisticalPeriodVO

import java.util
import scala.beans.BeanProperty

@DWSHttpMessageResult(value = "/api/rest_j/v\\d+/data-warehouse/statistical_periods/all")
class ListStatisticalPeriodsResult extends DWSResult {
  @BeanProperty var list: java.util.List[java.util.Map[String, Any]] = _

  def getAll: util.List[DwStatisticalPeriodVO] = {
    import scala.collection.JavaConverters._
    list.asScala.map(x=>{
      val str = DWSHttpClient.jacksonJson.writeValueAsString(x)
      DWSHttpClient.jacksonJson.readValue(str, classOf[DwStatisticalPeriodVO])
    }).asJava
  }
}
