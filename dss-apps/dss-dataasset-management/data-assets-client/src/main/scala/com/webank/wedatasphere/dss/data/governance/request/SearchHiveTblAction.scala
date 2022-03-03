package com.webank.wedatasphere.dss.data.governance.request

import com.webank.wedatasphere.dss.data.governance.entity.QueryType
import org.apache.linkis.httpclient.request.GetAction

class SearchHiveTblAction extends GetAction with DataAssetsAction{
  override def suffixURLs: Array[String] = Array("data-assets", "asset", "hiveTbl","search")

  private var user:String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

}
object SearchHiveTblAction{
  def builder(): Builder = new Builder

  class Builder private[SearchHiveTblAction]() {
    private var classification:String=_
    private var query:String=_
    private var owner:String=_
    private var limit:Int=10
    private var offset:Int=0
    private var precise: QueryType = QueryType.FUZZY
    private var user: String = _

    def setUser(user: String): Builder ={
      this.user = user
      this
    }

    def setClassification(classification:String): Builder ={
      this.classification = classification
      this
    }

    def setQuery(query:String): Builder ={
      this.query = query
      this
    }

    def setOwner(owner:String):Builder={
      this.owner=owner
      this
    }

    def setPrecise(precise : QueryType):Builder={
      this.precise = precise
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

    def build(): SearchHiveTblAction = {
      val action = new SearchHiveTblAction

      if(classification != null) action.setParameter("classification",classification)
      if(query!=null) action.setParameter("query",query)
      if(owner == null) action.setParameter("owner","")
      else action.setParameter("owner",owner)

      action.setParameter("limit",limit)
      action.setParameter("offset",offset)
      action.setParameter("precise",precise.getCode)

      action.setUser(user)
      action
    }
  }
}
