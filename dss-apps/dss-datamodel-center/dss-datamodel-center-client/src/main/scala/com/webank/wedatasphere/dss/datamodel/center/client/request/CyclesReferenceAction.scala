package com.webank.wedatasphere.dss.datamodel.center.client.request

import com.webank.wedatasphere.dss.datamodel.center.client.exception.DatamodelCenterClientBuilderException
import org.apache.linkis.httpclient.request.GetAction

class CyclesReferenceAction  extends GetAction with DatamodelCenterAction{

  private var name:String=_

  private var user:String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("datamodel", "cycles", "reference",name)
}
object CyclesReferenceAction{
  def builder(): Builder = new Builder

  class Builder private[CyclesReferenceAction]() {
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

    def build(): CyclesReferenceAction = {
      val action = new CyclesReferenceAction
      if(name == null) throw new DatamodelCenterClientBuilderException("name is needed!")
      action.name = name
      action.setUser(user)
      action
    }
  }
}

