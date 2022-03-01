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

import com.webank.wedatasphere.dss.common.protocol.project.{ProjectInfoRequest, ProjectRelationRequest}
import com.webank.wedatasphere.dss.framework.project.service.{DSSOrchestratorService, DSSProjectService}
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService
import com.webank.wedatasphere.dss.orchestrator.common.protocol.{RequestProjectImportOrchestrator, RequestProjectUpdateOrcVersion}
import org.apache.linkis.protocol.usercontrol.{RequestUserListFromWorkspace, RequestUserWorkspace}
import org.apache.linkis.rpc.{RPCMessageEvent, Receiver, ReceiverChooser}

import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class ProjectReceiverChooser extends ReceiverChooser {


  @Autowired
  var projectService: DSSProjectService = _
  @Autowired
  var orchestratorService: DSSOrchestratorService = _
  @Autowired
  var dssWorkspaceUserService: DSSWorkspaceUserService = _

  private var receiver: Option[ProjectReceiver] = _

  @PostConstruct
  def init(): Unit = receiver = Some(new ProjectReceiver(projectService, dssWorkspaceUserService, orchestratorService))

  override def chooseReceiver(event: RPCMessageEvent): Option[Receiver] = event.message match {
    case _: ProjectRelationRequest => receiver
    case _: RequestUserWorkspace => receiver
    case _: RequestUserListFromWorkspace => receiver
    case _: ProjectInfoRequest => receiver
    case _: RequestProjectImportOrchestrator => receiver
    case _: RequestProjectUpdateOrcVersion => receiver
    case _ => None
  }
}




