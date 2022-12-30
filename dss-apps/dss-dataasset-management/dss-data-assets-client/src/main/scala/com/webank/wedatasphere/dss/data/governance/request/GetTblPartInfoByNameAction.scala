package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.exception.DataAssetsClientBuilderException
import org.apache.linkis.httpclient.request.GetAction

class GetTblPartInfoByNameAction extends GetAction with DataAssetsAction {


  private var user: String = _

  override def suffixURLs: Array[String] = Array("data-assets", "asset", "hiveTbl", "partition", "name")

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user
}

object GetTblPartInfoByNameAction {
  def builder(): Builder = new Builder

  class Builder private[GetTblPartInfoByNameAction]() {
    private var user: String = _
    private var dbName: String = _
    private var tableName: String = _

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def setDbName(dbName: String): Builder = {
      this.dbName = dbName
      this
    }

    def setTableName(tableName: String): Builder = {
      this.tableName = tableName
      this
    }


    def build(): GetTblPartInfoByNameAction = {
      val action = new GetTblPartInfoByNameAction
      if (tableName == null || dbName == null)
        throw new DataAssetsClientBuilderException("tableName or dbName can not be null")

      action.setParameter("tableName", tableName)
      action.setParameter("dbName", dbName)
      action.setUser(user)
      action
    }
  }
}
