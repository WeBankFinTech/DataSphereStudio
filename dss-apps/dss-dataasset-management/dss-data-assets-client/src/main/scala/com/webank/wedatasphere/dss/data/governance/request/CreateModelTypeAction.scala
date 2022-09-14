package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant
import com.webank.wedatasphere.dss.data.governance.exception.DataAssetsClientBuilderException
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.request.POSTAction

class CreateModelTypeAction extends POSTAction with DataAssetsAction {

  private var name:String= _

  private var `type`:String= _

  private var user:String = _

  override def getRequestPayload: String = DWSHttpClient.jacksonJson.writeValueAsString(getRequestPayloads)

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = user

  override def suffixURLs: Array[String] = Array("data-assets", "asset", "model","type")
}

object CreateModelTypeAction{
  def builder(): Builder = new Builder

  class Builder private[CreateModelTypeAction]() {
    private var user: String = _
    private var name:String= _
    private var `type`:ClassificationConstant= _

    def setUser(user: String): Builder ={
      this.user = user
      this
    }

    def setName(name:String):Builder = {
      this.name = name
      this
    }

    def setType(`type`:ClassificationConstant):Builder = {
      this.`type` = `type`;
      this;
    }

    def build(): CreateModelTypeAction = {
      val action = new CreateModelTypeAction
      if(`type` == null) throw new DataAssetsClientBuilderException("type is needed!")
      if (name == null) throw new DataAssetsClientBuilderException("name is needed!")
      action.name = name
      action.`type` = `type`.getTypeCode
      action.setUser(user)
      action.addRequestPayload("name",action.name)
      action.addRequestPayload("type",action.`type`)
      action

    }
  }
}
