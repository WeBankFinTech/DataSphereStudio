package com.webank.wedatasphere.dss.datamodel.center.client.request

import com.webank.wedatasphere.dss.datamodel.center.client.exception.DatamodelCenterClientBuilderException
import com.webank.wedatasphere.linkis.httpclient.request.GetAction

class LayersReferenceAction  extends GetAction with DatamodelCenterAction{

  private var name:String=_

  private var user:String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("datamodel", "layers", "reference",name)
}
object LayersReferenceAction{
  def builder(): Builder = new Builder

  class Builder private[LayersReferenceAction]() {
    private var name:String = _
    private var user: String = _

    def setUser(user: String): Builder ={
      this.user = user
      this
    }
    def setName(name:String): Builder = {
      this.name = name
      this
    }

    def build(): LayersReferenceAction = {
      val action = new LayersReferenceAction
      if(name == null) throw new DatamodelCenterClientBuilderException("name is needed!")
      action.name = name
      action.setUser(user)
      action
    }
  }
}

