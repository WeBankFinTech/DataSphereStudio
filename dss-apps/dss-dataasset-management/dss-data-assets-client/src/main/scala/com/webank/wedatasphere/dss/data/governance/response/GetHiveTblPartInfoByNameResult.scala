package com.webank.wedatasphere.dss.data.governance.response

import com.webank.wedatasphere.dss.data.governance.entity.{HiveSimpleInfo, PartInfo}
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import java.util
import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/data-assets/asset/hiveTbl/partition/name")
class GetHiveTblPartInfoByNameResult extends DWSResult{
  @BeanProperty var result:util.List[java.util.Map[String, Any]] = _

  def getInfo: util.List[PartInfo] ={
    import scala.collection.JavaConverters._
    result.asScala.map(x=>{
      val str = DWSHttpClient.jacksonJson.writeValueAsString(x)
      DWSHttpClient.jacksonJson.readValue(str, classOf[PartInfo])
    }).asJava
  }
}
