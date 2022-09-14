package com.webank.wedatasphere.warehouse.client.action

import org.apache.linkis.httpclient.request.GetAction
import com.webank.wedatasphere.warehouse.client.DwAction

class ListDwModifierAction extends GetAction with DwAction {
  private var user: String = _
//  private var isAvailable: Boolean = _
//  private var theme: String = _
//  private var layer: String = _

//  def setIsAvailable(isAvailable: Boolean): Unit = this.isAvailable = isAvailable
//
//  def getIsAvailable: Boolean = this.isAvailable
//
//  def setTheme(theme: String): Unit = this.theme = theme
//
//  def getTheme: String = this.theme
//
//  def setLayer(layer: String): Unit = this.layer = layer
//
//  def getLayer: String = this.layer

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("data-warehouse", "modifiers", "all")
}

object ListDwModifierAction {
  def builder(): Builder = new Builder

  class Builder private[ListDwModifierAction]() {
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

    def build(): ListDwModifierAction = {
      val action = new ListDwModifierAction
//      action.setUser(user)
//      action.setParameter("isAvailable", isAvailable)
//      action.setParameter("typeName", name)
//      action.setParameter("layer", layer)
//      action.setParameter("theme", theme)
      if (null != user) {
        action.setUser(user)
      }
      action.setParameter("isAvailable", isAvailable)
      if (null != name) {
        action.setParameter("typeName", name)
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