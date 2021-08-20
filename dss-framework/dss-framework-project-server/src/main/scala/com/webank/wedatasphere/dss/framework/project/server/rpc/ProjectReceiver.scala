/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.project.server.rpc

import java.util

import com.webank.wedatasphere.dss.common.entity.project.DSSProject
import com.webank.wedatasphere.dss.common.protocol.project.{ProjectInfoRequest, ProjectRelationRequest, ProjectRelationResponse}
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestProjectImportOrchestrator
import com.webank.wedatasphere.linkis.protocol.usercontrol.{RequestUserListFromWorkspace, RequestUserWorkspace, ResponseUserWorkspace, ResponseWorkspaceUserList}
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._
import scala.concurrent.duration.Duration


@Component
class ProjectReceiver(projectService: DSSProjectService) extends Receiver {

  @Autowired
  var dssWorkspaceUserService:DSSWorkspaceUserService = _


  override def receive(message: Any, sender: Sender): Unit = {

  }

  override def receiveAndReply(message: Any, sender: Sender): Any = {
    message match {
      case projectRelationRequest: ProjectRelationRequest =>
        val dssProjectId = projectRelationRequest.getDssProjectId
        val dssLabels = projectRelationRequest.getDssLabels
        val appConnName = projectRelationRequest.getAppconnName
        val appConnProjectId = projectService.getAppConnProjectId(dssProjectId, appConnName, dssLabels)
        new ProjectRelationResponse(dssProjectId, appConnName, dssLabels, appConnProjectId)
      case requestUserWorkspace:RequestUserWorkspace =>
        val userWorkspaceIds: util.List[Integer] = dssWorkspaceUserService.getUserWorkspaceIds(requestUserWorkspace.getUserName)
        new ResponseUserWorkspace(userWorkspaceIds)

      case requestUserListFromWorkspace: RequestUserListFromWorkspace=>
        val userList = requestUserListFromWorkspace.getUserWorkspaceIds.flatMap(id => dssWorkspaceUserService.getAllWorkspaceUsers(id)).distinct
        new ResponseWorkspaceUserList(userList)

      case projectInfoRequest: ProjectInfoRequest =>
        val dssProjectDO: DSSProjectDO = projectService.getProjectById(projectInfoRequest.getProjectId)
        val DSSProject = new DSSProject()
        BeanUtils.copyProperties(dssProjectDO, DSSProject)
        DSSProject

      case projectImportOrchestrator: RequestProjectImportOrchestrator =>
        projectService.importOrchestrator(projectImportOrchestrator)
    }
  }


  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = {
    null
  }
}
