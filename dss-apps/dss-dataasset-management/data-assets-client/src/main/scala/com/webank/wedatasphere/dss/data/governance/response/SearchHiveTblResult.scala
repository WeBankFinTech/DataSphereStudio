package com.webank.wedatasphere.dss.data.governance.response

import com.webank.wedatasphere.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import com.webank.wedatasphere.linkis.httpclient.dws.response.DWSResult

import java.util
import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/data-assets/asset/hiveTbl/search")
class SearchHiveTblResult extends DWSResult{
  @BeanProperty var result:util.List[java.util.Map[String, Any]] = _

}
