package com.webank.wedatasphere.dss.framework.workspace.client

import com.webank.wedatasphere.dss.framework.workspace.client.request.{GetWorkspaceRolesAction, GetWorkspaceUsersAction}
import com.webank.wedatasphere.dss.framework.workspace.client.response.{GetWorkspaceRolesResult, GetWorkspaceUsersResult}

trait WorkSpaceRemoteClient extends RemoteClient {
  def getWorkspaceUsers(action: GetWorkspaceUsersAction): GetWorkspaceUsersResult
  def getWorkspaceRoles(action: GetWorkspaceRolesAction): GetWorkspaceRolesResult
}
