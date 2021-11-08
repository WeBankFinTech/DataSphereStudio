package com.webank.wedatasphere.warehouse.client

import com.webank.wedatasphere.linkis.httpclient.request.Action
import com.webank.wedatasphere.linkis.httpclient.response.Result

trait RemoteClient extends java.io.Closeable {
  protected def execute(action: Action): Result

  override def close(): scala.Unit
}
