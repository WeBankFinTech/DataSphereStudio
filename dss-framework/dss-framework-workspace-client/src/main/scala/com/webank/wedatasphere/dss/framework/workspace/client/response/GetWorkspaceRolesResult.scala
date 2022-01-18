package com.webank.wedatasphere.dss.framework.workspace.client.response

import com.webank.wedatasphere.dss.framework.workspace.client.entity.DSSWorkspaceRole
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import java.util
import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/dss/framework/workspace/getWorkspaceRoles")
class GetWorkspaceRolesResult extends DWSResult{
  @BeanProperty var workspaceRoles:util.List[java.util.Map[String, Any]] = _

  def getWorkspaceRoleList:util.List[DSSWorkspaceRole]={
    import scala.collection.JavaConverters._
    workspaceRoles.asScala.map(x=>{
      val str = DWSHttpClient.jacksonJson.writeValueAsString(x)
      DWSHttpClient.jacksonJson.readValue(str, classOf[DSSWorkspaceRole])
    }).asJava
  }
}
