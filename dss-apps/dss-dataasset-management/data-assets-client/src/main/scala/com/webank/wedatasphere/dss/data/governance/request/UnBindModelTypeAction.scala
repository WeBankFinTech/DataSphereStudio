package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant
import com.webank.wedatasphere.dss.data.governance.exception.DataAssetsClientBuilderException
import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.linkis.httpclient.request.POSTAction

class UnBindModelTypeAction extends POSTAction with DataAssetsAction {

  private var user: String = _

  private var modelName: String = _

  private var modelType: String = _

  private var tableName: String = _

  private var guid: String = _

  override def getRequestPayload: String = DWSHttpClient.jacksonJson.writeValueAsString(getRequestPayloads)

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("data-assets", "asset", "model", "unbind")
}

object UnBindModelTypeAction {

  def builder() = new Builder

  class Builder private[UnBindModelTypeAction] {
    private var user: String = _

    private var modelName: String = _

    private var modelType: ClassificationConstant = _

    private var tableName: String = _

    private var guid: String = _

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def setModelName(modelName: String): Builder = {
      this.modelName = modelName;
      this
    }

    def setModelType(modelType: ClassificationConstant): Builder = {
      this.modelType = modelType;
      this
    }

    def setTableName(tableName: String): Builder = {
      this.tableName = tableName;
      this
    }

    def setGuid(guid: String): Builder = {
      this.guid = guid;
      this
    }

    def build(): UnBindModelTypeAction = {
      var action = new UnBindModelTypeAction;
      if (tableName == null && guid == null) throw new DataAssetsClientBuilderException("tableName or guid is needed!")
      if (modelType == null || modelName == null) throw new DataAssetsClientBuilderException("modelType and modelName is needed!")
      action.setUser(user)
      action.guid = guid
      action.modelName = modelName
      action.modelType = modelType.getTypeCode
      action.tableName = tableName
      action.addRequestPayload("guid", action.guid)
      action.addRequestPayload("modelName", action.modelName)
      action.addRequestPayload("modelType", action.modelType)
      action.addRequestPayload("tableName", action.tableName)
      action
    }
  }
}


