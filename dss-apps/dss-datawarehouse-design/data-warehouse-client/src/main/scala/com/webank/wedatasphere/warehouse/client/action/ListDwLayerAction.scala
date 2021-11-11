package com.webank.wedatasphere.warehouse.client.action

import com.webank.wedatasphere.linkis.httpclient.request.GetAction
import com.webank.wedatasphere.warehouse.client.DwAction

class ListDwLayerAction() extends GetAction with DwAction {
  private var user: String = _
  private var isAvailable: Boolean = true

  def setIsAvailable(isAvailable: Boolean): Unit = this.isAvailable = isAvailable

  def getIsAvailable: Boolean = this.isAvailable

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("data-warehouse", "layers", "all")
}
