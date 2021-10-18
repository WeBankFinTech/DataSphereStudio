package com.webank.wedatasphere.dss.data.governance

import com.webank.wedatasphere.linkis.httpclient.request.Action
import com.webank.wedatasphere.linkis.httpclient.response.Result
import java.io.Closeable

trait RemoteClient extends Closeable{
  protected def execute(action: Action): Result

  override def close(): Unit
}
