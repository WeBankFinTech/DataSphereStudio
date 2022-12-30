package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant
import com.webank.wedatasphere.dss.data.governance.exception.DataAssetsClientBuilderException
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.request.POSTAction

class UpdateModelTypeAction extends POSTAction  with DataAssetsAction{


  private var name:String= _

  private var `type`:String= _

  private var user:String = _

  private var orgName:String = _

  override def getRequestPayload: String = DWSHttpClient.jacksonJson.writeValueAsString(getRequestPayloads)

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = user

  override def suffixURLs: Array[String] = Array("data-assets", "asset", "model","type","modify")
}

object UpdateModelTypeAction{
  def builder(): Builder = new Builder

  class Builder private[UpdateModelTypeAction]() {
    private var user: String = _
    private var name:String= _
    private var `type`:ClassificationConstant= _
    private var orgName:String = _
    def setUser(user: String): Builder ={
      this.user = user
      this
    }

    def setName(name:String):Builder = {
      this.name = name
      this
    }

    def setOrgName(orgName:String):Builder = {
      this.orgName = orgName
      this
    }

    def setType(`type`:ClassificationConstant):Builder = {
      this.`type` = `type`;
      this;
    }

    def build(): UpdateModelTypeAction = {
      val action = new UpdateModelTypeAction
      if(`type` == null) throw new DataAssetsClientBuilderException("type is needed!")
      if (name == null) throw new DataAssetsClientBuilderException("name is needed!")
      if (orgName == null) throw new DataAssetsClientBuilderException("orgName is needed!")
      action.name = name
      action.`type` = `type`.getTypeCode
      action.orgName = orgName
      action.setUser(user)
      action.addRequestPayload("name",action.name)
      action.addRequestPayload("type",action.`type`)
      action.addRequestPayload("orgName",action.orgName)
      action
    }
  }

}
