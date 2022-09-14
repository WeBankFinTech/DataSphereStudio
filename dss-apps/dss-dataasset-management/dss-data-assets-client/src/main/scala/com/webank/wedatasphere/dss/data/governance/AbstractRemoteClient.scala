package com.webank.wedatasphere.dss.data.governance

import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.request.Action
import org.apache.linkis.httpclient.response.Result

abstract class AbstractRemoteClient extends RemoteClient {
  protected val dwsHttpClient:DWSHttpClient

  override def execute(action: Action): Result = action match {
    case action: Action => dwsHttpClient.execute(action)
  }

  override def close(): Unit = dwsHttpClient.close()
}
