package com.webank.wedatasphere.dss.framework.workspace.client.impl

import com.webank.wedatasphere.dss.framework.workspace.client.request.{GetWorkspaceRolesAction, GetWorkspaceUsersAction}
import com.webank.wedatasphere.dss.framework.workspace.client.response.{GetWorkspaceRolesResult, GetWorkspaceUsersResult}
import com.webank.wedatasphere.dss.framework.workspace.client.{AbstractRemoteClient, WorkSpaceRemoteClient}
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.dws.config.DWSClientConfig

class LinkisWorkSpaceRemoteClient(clientConfig: DWSClientConfig) extends AbstractRemoteClient with WorkSpaceRemoteClient {
  override protected val dwsHttpClient: DWSHttpClient = new DWSHttpClient(clientConfig, "WorkSpace-Client")

  override def getWorkspaceUsers(action: GetWorkspaceUsersAction): GetWorkspaceUsersResult = execute(action).asInstanceOf[GetWorkspaceUsersResult]

  override def getWorkspaceRoles(action: GetWorkspaceRolesAction): GetWorkspaceRolesResult = execute(action).asInstanceOf[GetWorkspaceRolesResult]

}
