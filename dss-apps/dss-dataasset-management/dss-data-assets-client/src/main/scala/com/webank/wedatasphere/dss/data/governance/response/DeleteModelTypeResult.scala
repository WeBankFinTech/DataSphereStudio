package com.webank.wedatasphere.dss.data.governance.response

import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/data-assets/asset/model/type/delete")
class DeleteModelTypeResult extends DWSResult{
  @BeanProperty var result:Object = _
}