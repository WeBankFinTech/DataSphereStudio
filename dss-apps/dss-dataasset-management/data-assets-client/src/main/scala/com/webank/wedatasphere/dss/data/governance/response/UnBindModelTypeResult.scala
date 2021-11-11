package com.webank.wedatasphere.dss.data.governance.response

import com.webank.wedatasphere.linkis.httpclient.dws.annotation.DWSHttpMessageResult

import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/data-assets/asset/model/unbind")
class UnBindModelTypeResult {
  @BeanProperty var result: Object = _
}
