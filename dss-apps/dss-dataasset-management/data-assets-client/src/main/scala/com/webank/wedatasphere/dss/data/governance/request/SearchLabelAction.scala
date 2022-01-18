package com.webank.wedatasphere.dss.data.governance.request

import org.apache.linkis.httpclient.request.GetAction

class SearchLabelAction extends GetAction with DataAssetsAction{
  override def suffixURLs: Array[String] = Array("data-assets", "asset", "labels","search")

  private var user:String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

}
object SearchLabelAction{
  def builder(): Builder = new Builder

  class Builder private[SearchLabelAction]() {

    private var query:String=_

    private var limit:Int=10
    private var offset:Int=0
    private var user: String = _

    def setUser(user: String): Builder ={
      this.user = user
      this
    }


    def setQuery(query:String): Builder ={
      this.query = query
      this
    }



    def setLimit(limit:Int):Builder={
      this.limit=limit
      this
    }

    def setOffset(offset:Int):Builder={
      this.offset=offset
      this
    }

    def build(): SearchLabelAction = {
      val action = new SearchLabelAction
      if(query!=null) action.setParameter("query",query)
      action.setParameter("limit",limit)
      action.setParameter("offset",offset)
      action.setUser(user)
      action
    }
  }
}

