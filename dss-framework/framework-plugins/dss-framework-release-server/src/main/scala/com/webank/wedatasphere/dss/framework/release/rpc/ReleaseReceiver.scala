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

package com.webank.wedatasphere.dss.framework.release.rpc

import com.webank.wedatasphere.dss.common.protocol.framework.{ReleaseOrchestratorRequest, ReleaseOrchestratorResponse, ReleaseOrchestratorStatusRequest, ReleaseOrchestratorStatusResponse}
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.framework.release.service.ReleaseService
import com.webank.wedatasphere.dss.standard.app.sso.Workspace
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.duration.Duration

/**
 * created by cooperyang on 2020/11/17
 * Description:框架发布的RPC的Receiver单例
 * 需要实现
 * 1.接收发布的请求，同时需要
 */

class ReleaseReceiver(releaseService:ReleaseService) extends Receiver with Logging{





  override def receive(message: Any, sender: Sender): Unit = {

  }

  override def receiveAndReply(message: Any, sender: Sender): Any = message match {
    case ReleaseOrchestratorRequest(releaseUser, orchestratorVersionId, orchestratorId, dssLabel, workspaceName, workSpaceStr) =>
      val workspace:Workspace = DSSCommonUtils.COMMON_GSON.fromJson(workSpaceStr, classOf[Workspace])
      val jobId = releaseService.releaseOrchestrator(releaseUser, orchestratorVersionId, orchestratorId, dssLabel, workspace)
      ReleaseOrchestratorResponse(jobId)
    case ReleaseOrchestratorStatusRequest(jobId) => val (status, errorMsg) = releaseService.getStatus(jobId)
      ReleaseOrchestratorStatusResponse(status, jobId, errorMsg)
    case _ =>
  }

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = ???
}
