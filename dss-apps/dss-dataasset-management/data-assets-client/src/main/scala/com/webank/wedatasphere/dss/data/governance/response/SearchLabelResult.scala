package com.webank.wedatasphere.dss.data.governance.response

import com.webank.wedatasphere.dss.data.governance.entity.{HiveSimpleInfo, SearchLabelInfo}
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import java.util
import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/data-assets/asset/labels/search")
class SearchLabelResult extends DWSResult{
  @BeanProperty var result:util.List[java.util.Map[String, Any]] = _

  def getLabelList: util.List[SearchLabelInfo] ={
    import scala.collection.JavaConverters._
    result.asScala.map(x=>{
      val str = DWSHttpClient.jacksonJson.writeValueAsString(x)
      DWSHttpClient.jacksonJson.readValue(str, classOf[SearchLabelInfo])
    }).asJava
  }
}
