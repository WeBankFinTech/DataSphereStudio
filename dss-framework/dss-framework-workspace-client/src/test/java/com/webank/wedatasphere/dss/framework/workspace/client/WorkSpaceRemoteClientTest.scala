package com.webank.wedatasphere.dss.framework.workspace.client

import com.webank.wedatasphere.dss.framework.workspace.client.impl.LinkisWorkSpaceRemoteClient
import com.webank.wedatasphere.dss.framework.workspace.client.request.{GetWorkspaceRolesAction, GetWorkspaceUsersAction}
import org.apache.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder

import java.util.concurrent.TimeUnit

object WorkSpaceRemoteClientTest {
  def main(args: Array[String]): Unit = {
    val clientConfig = DWSClientConfigBuilder.newBuilder()
      .addServerUrl("http://localhost:8088")
      .connectionTimeout(30000)
      .discoveryEnabled(false)
      .discoveryFrequency(1,TimeUnit.MINUTES)
      .loadbalancerEnabled(true)
      .maxConnectionSize(5)
      .retryEnabled(false)
      .readTimeout(30000)
      .setAuthenticationStrategy(new StaticAuthenticationStrategy())
      .setAuthTokenKey("hdfs")
      .setAuthTokenValue("hdfs")
      .setDWSVersion("v1")
      .build()

    val workSpaceRemoteClient = new LinkisWorkSpaceRemoteClient(clientConfig)

    val workspaceRoles = workSpaceRemoteClient.getWorkspaceRoles(GetWorkspaceRolesAction.builder().setUser("hdfs").setWorkspaceId("224").build())
    println(workspaceRoles.getWorkspaceRoleList)


    val workspaceUsersResult = workSpaceRemoteClient.getWorkspaceUsers(GetWorkspaceUsersAction.builder().setUser("hdfs").setWorkspaceId("224").build())
    println(workspaceUsersResult.total)
    println(workspaceUsersResult.getWorkspaceUserList)
    println(workspaceUsersResult.getWorkspaceRoleList)
  }


}
