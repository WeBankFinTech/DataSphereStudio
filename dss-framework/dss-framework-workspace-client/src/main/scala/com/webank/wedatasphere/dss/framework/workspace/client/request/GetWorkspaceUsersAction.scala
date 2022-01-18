package com.webank.wedatasphere.dss.framework.workspace.client.request

import com.webank.wedatasphere.dss.framework.workspace.client.exception.WorkSpaceClientBuilderException
import org.apache.linkis.httpclient.request.GetAction

class GetWorkspaceUsersAction extends GetAction with WorkSpaceAction{
  private var user:String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("dss", "framework","workspace", "getWorkspaceUsers")
}
object GetWorkspaceUsersAction{
  def builder(): Builder = new Builder

  class Builder private[GetWorkspaceUsersAction]() {
    private var workspaceId:String = _
    private var pageNow:Integer=_
    private var pageSize:Integer=_
    private var department:String=_
    private var username:String=_
    private var roleName:String=_
    private var user: String = _

    def setUser(user: String): Builder ={
      this.user = user
      this
    }
    def setWorkspaceId(workspaceId:String): Builder = {
      this.workspaceId = workspaceId
      this
    }

    def setPageNow(pageNow:Integer): Builder ={
      this.pageNow = pageNow
      this
    }

    def setPageSize(pageSize:Integer):Builder={
      this.pageSize = pageSize
      this
    }

    def setDepartment(department:String): Builder ={
      this.department = department
      this
    }

    def setRoleName(roleName:String):Builder={
      this.roleName = roleName
      this
    }

    def setUsername(username:String):Builder={
      this.username = username
      this
    }

    def build(): GetWorkspaceUsersAction = {
      val action = new GetWorkspaceUsersAction
      if(workspaceId == null) throw new WorkSpaceClientBuilderException("workspaceId is needed!")
      if(user == null) throw new WorkSpaceClientBuilderException("user is needed!")
      if(pageNow == null) pageNow = 1
      if(pageSize == null) pageSize = 10
      action.setUser(user)
      action.setParameter("workspaceId",workspaceId)
      action.setParameter("pageNow",pageNow)
      action.setParameter("pageSize",pageSize)
      if(department != null) action.setParameter("department",department)
      if(roleName != null) action.setParameter("roleName",roleName)
      if(username != null) action.setParameter("username",username)

      action
    }
  }
}
