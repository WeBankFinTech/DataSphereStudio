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

import com.webank.wedatasphere.dss.git.common.protocol.request.{GitArchiveProjectRequest, GitCheckProjectRequest, GitCommitRequest, GitCreateProjectRequest, GitCurrentCommitRequest, GitDeleteRequest, GitDiffRequest, GitFileContentRequest, GitHistoryRequest, GitRevertRequest, GitSearchRequest, GitUserInfoRequest, GitUserUpdateRequest}
import com.webank.wedatasphere.dss.git.service.{DSSGitProjectManagerService, DSSGitWorkflowManagerService, DSSWorkspaceGitService}
import org.apache.linkis.rpc.{Receiver, Sender}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.stereotype.Component

import java.util
import scala.concurrent.duration.Duration

class DSSGitReceiver(gitProjectManagerService: DSSGitProjectManagerService, gitWorkflowManagerService: DSSGitWorkflowManagerService,
                     workspaceGitService: DSSWorkspaceGitService) extends Receiver {

  override def receive(message: Any, sender: Sender): Unit = {}

  override def receiveAndReply(message: Any, sender: Sender): Any  = message match {
    case gitCreateProjectRequest: GitCreateProjectRequest =>
      gitProjectManagerService.create(gitCreateProjectRequest)
    case gitArchiveProjectRequest: GitArchiveProjectRequest =>
      gitProjectManagerService.archive(gitArchiveProjectRequest)
    case gitCheckProjectRequest: GitCheckProjectRequest =>
      gitProjectManagerService.checkProject(gitCheckProjectRequest)
    case gitDiffRequest: GitDiffRequest =>
      gitWorkflowManagerService.diff(gitDiffRequest)
    case gitCommitRequest: GitCommitRequest =>
      gitWorkflowManagerService.commit(gitCommitRequest)
    case gitSearchRequest: GitSearchRequest =>
      gitWorkflowManagerService.search(gitSearchRequest)
    case gitDeleteRequest: GitDeleteRequest =>
      gitWorkflowManagerService.delete(gitDeleteRequest)
    case gitFileContentRequest: GitFileContentRequest =>
      gitWorkflowManagerService.getFileContent(gitFileContentRequest)
    case gitHistoryRequest: GitHistoryRequest =>
      gitWorkflowManagerService.getHistory(gitHistoryRequest)
    case gitUserUpdateRequest: GitUserUpdateRequest =>
      workspaceGitService.associateGit(gitUserUpdateRequest)
    case gitUserInfoRequest: GitUserInfoRequest =>
      workspaceGitService.selectGitUserInfo(gitUserInfoRequest)
    case gitCurrentCommitRequest: GitCurrentCommitRequest =>
      gitWorkflowManagerService.getCurrentCommit(gitCurrentCommitRequest)
    case _ => None
  }

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = {}
}
