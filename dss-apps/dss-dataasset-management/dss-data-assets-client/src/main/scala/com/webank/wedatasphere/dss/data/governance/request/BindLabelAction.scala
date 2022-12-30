package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.exception.DataAssetsClientBuilderException
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.request.POSTAction

class BindLabelAction extends POSTAction with DataAssetsAction {

  private var user: String = _

  override def getRequestPayload: String = DWSHttpClient.jacksonJson.writeValueAsString(getRequestPayloads)

  override def suffixURLs: Array[String] = Array("data-assets", "asset", "labels", "bind")

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

}

object BindLabelAction {
  def builder(): Builder = new Builder

  class Builder private[BindLabelAction] {
    private var user: String = _

    private var label: String = _

    private var labelGuid: String = _

    private var tableName: String = _

    private var tableGuid: String = _

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def setTableName(tableName: String): Builder = {
      this.tableName = tableName;
      this
    }

    def setLabel(label : String) :Builder={
      this.label = label
      this
    }

    def setTableGuid(tableGuid : String) : Builder = {
      this.tableGuid = tableGuid
      this
    }

    def setLabelGuid(labelGuid : String) : Builder = {
      this.labelGuid = labelGuid
      this
    }

    def build(): BindLabelAction = {
      var action = new BindLabelAction;
      if ((tableName == null || label == null) && (labelGuid == null || tableGuid == null))
        throw new DataAssetsClientBuilderException("tableName and label ,or tableGuid and labelGuid are needed!")

      action.setUser(user)
      action.addRequestPayload("label", label)
      action.addRequestPayload("tableName", tableName)
      action.addRequestPayload("tableGuid", tableGuid)
      action.addRequestPayload("labelGuid", labelGuid)
      action
    }
  }
}




