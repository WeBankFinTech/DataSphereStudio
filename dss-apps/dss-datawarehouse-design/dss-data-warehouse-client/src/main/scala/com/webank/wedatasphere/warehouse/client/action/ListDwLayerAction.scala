package com.webank.wedatasphere.warehouse.client.action

import org.apache.linkis.httpclient.request.GetAction
import com.webank.wedatasphere.warehouse.client.DwAction

class ListDwLayerAction() extends GetAction with DwAction {
  private var user: String = _
//  private var isAvailable: Boolean = true
//  def setIsAvailable(isAvailable: Boolean): Unit = this.isAvailable = isAvailable
//  def getIsAvailable: Boolean = this.isAvailable

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("data-warehouse", "layers", "all")
}

object ListDwLayerAction {
  def builder(): Builder = new Builder

  class Builder private[ListDwLayerAction]() {
    private var user: String = _
    private var db: String = _
    private var isAvailable: Boolean = true

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def setIsAvailable(isAvailable: Boolean): Builder = {
      this.isAvailable = isAvailable
      this
    }

    def setDb(db: String): Builder = {
      this.db = db
      this
    }

    def build(): ListDwLayerAction = {
      val action = new ListDwLayerAction
      if (null != user) {
        action.setUser(user)
      }
      if (null != db) {
        action.setParameter("db", db);
      }
      action.setParameter("isAvailable", isAvailable)
      action
    }
  }

}