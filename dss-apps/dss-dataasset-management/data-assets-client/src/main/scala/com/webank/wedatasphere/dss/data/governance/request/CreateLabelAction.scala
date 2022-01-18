package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.exception.DataAssetsClientBuilderException
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.request.POSTAction

class CreateLabelAction extends POSTAction with DataAssetsAction {

  private var user: String = _

  override def getRequestPayload: String = DWSHttpClient.jacksonJson.writeValueAsString(getRequestPayloads)

  override def suffixURLs: Array[String] = Array("data-assets", "asset", "labels")

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

}

object CreateLabelAction {
  def builder(): Builder = new Builder

  class Builder private[CreateLabelAction]() {
    private var user: String = _
    private var name: String = _

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def setName(name: String): Builder = {
      this.name = name
      this
    }


    def build(): CreateLabelAction = {
      val action = new CreateLabelAction
      if (name == null) throw new DataAssetsClientBuilderException("name is needed!")
      action.setUser(user)
      action.addRequestPayload("name", this.name)
      action

    }
  }
}


