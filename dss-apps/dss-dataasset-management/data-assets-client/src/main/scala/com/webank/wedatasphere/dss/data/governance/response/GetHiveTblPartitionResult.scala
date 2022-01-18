package com.webank.wedatasphere.dss.data.governance.response

import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import java.util
import scala.beans.BeanProperty
@DWSHttpMessageResult("/api/rest_j/v\\d+/data-assets/asset/hiveTbl/(\\S+)/partition")
class GetHiveTblPartitionResult extends DWSResult{
  @BeanProperty var result:util.List[java.util.Map[String, Any]] = _
}
