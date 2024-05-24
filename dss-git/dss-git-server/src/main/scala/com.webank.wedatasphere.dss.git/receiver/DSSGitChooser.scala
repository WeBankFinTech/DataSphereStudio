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

package com.webank.wedatasphere.dss.git.receiver

import com.webank.wedatasphere.dss.git.common.protocol.request.{GitArchiveProjectRequest, GitBaseRequest, GitCheckProjectRequest, GitCommitRequest, GitConnectRequest, GitCreateProjectRequest, GitCurrentCommitRequest, GitDeleteRequest, GitDiffRequest, GitFileContentRequest, GitHistoryRequest, GitRemoveRequest, GitRenameRequest, GitRevertRequest, GitSearchRequest, GitUserInfoRequest, GitUserUpdateRequest}
import com.webank.wedatasphere.dss.git.service.{DSSGitProjectManagerService, DSSGitWorkflowManagerService, DSSWorkspaceGitService}

import javax.annotation.PostConstruct
import org.apache.linkis.rpc.{RPCMessageEvent, Receiver, ReceiverChooser}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class DSSGitChooser extends ReceiverChooser {

  @Autowired
  var gitProjectManagerService: DSSGitProjectManagerService = _

  @Autowired
  var gitWorkflowManagerService: DSSGitWorkflowManagerService = _

  @Autowired
  var workspaceGitService: DSSWorkspaceGitService = _

  var receiver: Option[DSSGitReceiver] = _

  @PostConstruct
  def init(): Unit = receiver = Some(new DSSGitReceiver(gitProjectManagerService, gitWorkflowManagerService, workspaceGitService))

  override def chooseReceiver(event: RPCMessageEvent): Option[Receiver] = event.message match {
    case _: GitCreateProjectRequest => receiver
    case _: GitArchiveProjectRequest => receiver
    case _: GitCheckProjectRequest => receiver
    case _: GitCommitRequest => receiver
    case _: GitDiffRequest => receiver
    case _: GitSearchRequest => receiver
    case _: GitFileContentRequest => receiver
    case _: GitDeleteRequest => receiver
    case _: GitHistoryRequest => receiver
    case _: GitUserUpdateRequest => receiver
    case _: GitUserInfoRequest => receiver
    case _: GitCurrentCommitRequest => receiver
    case _: GitRevertRequest => receiver
    case _: GitRemoveRequest => receiver
    case _: GitRenameRequest => receiver
    case _: GitConnectRequest => receiver
    case _ => None
  }
}