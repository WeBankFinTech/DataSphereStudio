package com.webank.wedatasphere.dss.datamodel.center.client.request

import com.webank.wedatasphere.dss.datamodel.center.client.exception.DatamodelCenterClientBuilderException
import com.webank.wedatasphere.linkis.httpclient.request.GetAction

class ModifiersReferenceAction extends GetAction with DatamodelCenterAction{

  private var name:String=_

  private var user:String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("datamodel", "modifiers", "reference",name)
}
object ModifiersReferenceAction{
  def builder(): Builder = new Builder

  class Builder private[ModifiersReferenceAction]() {
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

    def build(): ModifiersReferenceAction = {
      val action = new ModifiersReferenceAction
      if(name == null) throw new DatamodelCenterClientBuilderException("name is needed!")
      action.name = name
      action.setUser(user)
      action
    }
  }
}
