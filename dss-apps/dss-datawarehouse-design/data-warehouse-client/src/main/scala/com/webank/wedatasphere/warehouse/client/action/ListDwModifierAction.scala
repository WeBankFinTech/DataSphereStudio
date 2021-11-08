package com.webank.wedatasphere.warehouse.client.action

import com.webank.wedatasphere.linkis.httpclient.request.GetAction
import com.webank.wedatasphere.warehouse.client.DwAction

class ListDwModifierAction extends GetAction with DwAction {
  private var user: String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("data-warehouse", "modifiers", "all")
}
