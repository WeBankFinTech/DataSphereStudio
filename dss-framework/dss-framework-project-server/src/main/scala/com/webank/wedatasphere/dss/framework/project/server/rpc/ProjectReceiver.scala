/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.project.server.rpc

import com.webank.wedatasphere.dss.common.protocol.project.{ProjectRelationRequest, ProjectRelationResponse}
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.duration.Duration

/**
 * created by cooperyang on 2021/1/18
 * Description:用于rpc请求的接受类
 */
@Component
class ProjectReceiver(projectService: DSSProjectService) extends Receiver {



  override def receive(message: Any, sender: Sender): Unit = {

  }

  override def receiveAndReply(message: Any, sender: Sender): Any = {
    message match {
      case projectRelationRequest: ProjectRelationRequest =>
        val dssProjectId = projectRelationRequest.getDssProjectId;
        val dssLabels = projectRelationRequest.getDssLabels
        val appConnName = projectRelationRequest.getAppconnName
        val appConnProjectId = projectService.getAppConnProjectId(dssProjectId, appConnName, dssLabels)
        new ProjectRelationResponse(dssProjectId, appConnName, dssLabels, appConnProjectId)
    }
  }


  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = {
    null
  }
}
