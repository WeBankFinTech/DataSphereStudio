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

package com.webank.wedatasphere.dss.git.server.receiver

import com.webank.wedatasphere.dss.git.common.protocol.request.{GitArchiveProjectRequest, GitBaseRequest, GitCheckProjectRequest, GitCommitRequest, GitCreateProjectRequest, GitDeleteRequest, GitDiffRequest, GitFileContentRequest, GitRemoveRequest, GitRevertRequest, GitSearchRequest}
import com.webank.wedatasphere.dss.git.service.{DSSGitProjectManagerService, DSSGitWorkflowManagerService}

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

  var receiver: Option[DSSGitReceiver] = _

  @PostConstruct
  def init(): Unit = receiver = Some(new DSSGitReceiver(gitProjectManagerService, gitWorkflowManagerService))

  override def chooseReceiver(event: RPCMessageEvent): Option[Receiver] = event.message match {
    case _: GitCreateProjectRequest => receiver
    case _: GitArchiveProjectRequest => receiver
    case _: GitCheckProjectRequest => receiver
    case _: GitCommitRequest => receiver
    case _: GitDiffRequest => receiver
    case _: GitSearchRequest => receiver
    case _: GitFileContentRequest => receiver
    case _: GitDeleteRequest => receiver
    case _ => None
  }
}