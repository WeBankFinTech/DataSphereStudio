package com.webank.wedatasphere.dss.framework.workspace.client.request

import com.webank.wedatasphere.dss.framework.workspace.client.exception.WorkSpaceClientBuilderException
import org.apache.linkis.httpclient.request.GetAction

class GetWorkspaceRolesAction extends GetAction with WorkSpaceAction{

  private var user:String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("dss", "framework","workspace", "getWorkspaceRoles")
}
object GetWorkspaceRolesAction{
  def builder(): Builder = new Builder

  class Builder private[GetWorkspaceRolesAction]() {
    private var workspaceId:String = _
    private var user: String = _

    def setUser(user: String): Builder ={
      this.user = user
      this
    }
    def setWorkspaceId(workspaceId:String): Builder = {
      this.workspaceId = workspaceId
      this
    }

    def build(): GetWorkspaceRolesAction = {
      val action = new GetWorkspaceRolesAction
      if(workspaceId == null) throw new WorkSpaceClientBuilderException("workspaceId is needed!")
      action.setParameter("workspaceId",workspaceId)
      action.setUser(user)
      action
    }
  }
}


