package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.exception.DataAssetsClientBuilderException
import org.apache.linkis.httpclient.request.GetAction

class HiveTblStatsAction extends GetAction with DataAssetsAction {

  private var dbName: String = _

  private var tableName: String = _

  private var guid: String = _

  private var user: String = _

  override def suffixURLs: Array[String] = Array("data-assets", "asset", "hiveTbl", "stats")

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user
}

object HiveTblStatsAction {
  def builder(): Builder = new Builder

  class Builder private[HiveTblStatsAction]() {
    private var guid: String = _
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

    def setGuid(guid: String): Builder = {
      this.guid = guid
      this
    }

    def build(): HiveTblStatsAction = {
      val action = new HiveTblStatsAction
      if (guid == null && (tableName == null || dbName == null))
        throw new DataAssetsClientBuilderException("That tableName or dbName being null and guid being null could not happen in the same time!")
      if (guid != null) {
        action.setParameter("guid", guid)
      }
      if (tableName != null && dbName != null) {
        action.setParameter("tableName", tableName)
        action.setParameter("dbName", dbName)
      }
      action.setUser(user)
      action
    }
  }
}

