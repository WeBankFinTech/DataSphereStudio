package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.exception.DataAssetsClientBuilderException
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.request.POSTAction

class UpdateLabelAction extends POSTAction with DataAssetsAction {

  private var user: String = _

  override def getRequestPayload: String = DWSHttpClient.jacksonJson.writeValueAsString(getRequestPayloads)

  override def suffixURLs: Array[String] = Array("data-assets", "asset", "labels","modify")

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

}
object UpdateLabelAction {
  def builder(): Builder = new Builder

  class Builder private[UpdateLabelAction]() {
    private var user: String = _
    private var name: String = _
    private var orgName: String = _

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def setName(name: String): Builder = {
      this.name = name
      this
    }

    def setOrgName(orgName:  String) : Builder = {
      this.orgName = orgName
      this
    }

    def build(): UpdateLabelAction = {
      val action = new UpdateLabelAction
      if (name == null) throw new DataAssetsClientBuilderException("name is needed!")
      if (orgName == null) throw new DataAssetsClientBuilderException("orgName is needed!")
      action.setUser(user)
      action.addRequestPayload("name", this.name)
      action.addRequestPayload("orgName", this.orgName)
      action

    }
  }
}



