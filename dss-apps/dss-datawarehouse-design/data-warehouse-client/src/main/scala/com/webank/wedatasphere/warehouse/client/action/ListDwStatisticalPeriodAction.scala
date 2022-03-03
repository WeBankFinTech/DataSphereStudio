package com.webank.wedatasphere.warehouse.client.action

import org.apache.linkis.httpclient.request.GetAction
import com.webank.wedatasphere.warehouse.client.DwAction

class ListDwStatisticalPeriodAction extends GetAction with DwAction {
  private var user: String = _
//  private var isAvailable: Boolean = true
//  def setIsAvailable(isAvailable: Boolean): Unit = this.isAvailable = isAvailable
//  def getIsAvailable: Boolean = this.isAvailable

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("data-warehouse", "statistical_periods", "all")
}

object ListDwStatisticalPeriodAction {
  def builder(): Builder = new Builder

  class Builder private[ListDwStatisticalPeriodAction]() {
    private var user: String = _
    private var name: String = ""
    private var layer: String = ""
    private var theme: String = ""
    private var isAvailable: Boolean = true

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def setName(name: String): Builder = {
      this.name = name
      this
    }

    def setLayer(layer: String): Builder = {
      this.layer = layer
      this
    }

    def setTheme(theme: String): Builder = {
      this.theme = theme
      this
    }

    def setIsAvailable(isAvailable: Boolean): Builder = {
      this.isAvailable = isAvailable
      this
    }

    def build(): ListDwStatisticalPeriodAction = {
      val action = new ListDwStatisticalPeriodAction
      if (null != user) {
        action.setUser(user)
      }
      action.setParameter("isAvailable", isAvailable)
      if (null != name) {
        action.setParameter("name", name)
      }
      if (null != layer) {
        action.setParameter("layer", layer)
      }
      if (null != theme) {
        action.setParameter("theme", theme)
      }
      action
    }
  }

}