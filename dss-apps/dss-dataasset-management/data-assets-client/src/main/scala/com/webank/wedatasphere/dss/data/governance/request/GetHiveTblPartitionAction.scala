package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.exception.DataAssetsClientBuilderException
import com.webank.wedatasphere.linkis.httpclient.request.GetAction

class GetHiveTblPartitionAction extends GetAction with DataAssetsAction{

  private var guid:String=_

  private var user:String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("data-assets", "asset", "hiveTbl",guid,"partition")
}
object GetHiveTblPartitionAction{
  def builder(): Builder = new Builder

  class Builder private[GetHiveTblPartitionAction]() {
    private var guid:String = _
    private var user: String = _

    def setUser(user: String): Builder ={
      this.user = user
      this
    }

    def setGuid(guid:String): Builder = {
      this.guid = guid
      this
    }

    def build(): GetHiveTblPartitionAction = {
      val action = new GetHiveTblPartitionAction
      if(guid == null) throw new DataAssetsClientBuilderException("guid is needed!")
      action.guid = guid
      action.setUser(user)
      action
    }
  }
}
