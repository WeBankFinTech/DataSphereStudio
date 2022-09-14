package com.webank.wedatasphere.dss.framework.workspace.client

import org.apache.linkis.httpclient.request.Action
import org.apache.linkis.httpclient.response.Result
import java.io.Closeable

trait RemoteClient extends Closeable{
  protected def execute(action: Action): Result

  override def close(): Unit
}
