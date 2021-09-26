package com.webank.wedatasphere.warehouse.client

import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.linkis.httpclient.request.Action
import com.webank.wedatasphere.linkis.httpclient.response.Result

abstract class AbstractDwRemoteClient extends RemoteClient {
  protected val dwsHttpClient: DWSHttpClient
  override def execute(action: Action) : Result = action match {
    case action: Action => dwsHttpClient.execute(action)
  }
  override def close() : scala.Unit =  dwsHttpClient.close()
}