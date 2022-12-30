package com.webank.wedatasphere.dss.data.governance.response

import com.webank.wedatasphere.dss.data.governance.entity.{CreateModelTypeInfo, HiveTblStatsDTO}
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/data-assets/asset/hiveTbl/stats")
class HiveTblStatsResult extends DWSResult{
  @BeanProperty var result: java.util.Map[String, Any] = _


  def getInfo : HiveTblStatsDTO ={
    val str = DWSHttpClient.jacksonJson.writeValueAsString(result)
    DWSHttpClient.jacksonJson.readValue(str, classOf[HiveTblStatsDTO])
  }
}
