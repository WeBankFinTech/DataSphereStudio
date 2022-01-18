package com.webank.wedatasphere.warehouse.client

import org.apache.linkis.httpclient.request.Action
import org.apache.linkis.httpclient.response.Result

trait RemoteClient extends java.io.Closeable {
  protected def execute(action: Action): Result

  override def close(): scala.Unit
}
