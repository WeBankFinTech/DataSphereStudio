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

package com.webank.wedatasphere.dss.orchestrator.server.receiver

import com.webank.wedatasphere.dss.orchestrator.common.protocol._
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext
import com.webank.wedatasphere.dss.orchestrator.server.service.{OrchestratorPluginService, OrchestratorService}
import javax.annotation.PostConstruct
import org.apache.linkis.rpc.{RPCMessageEvent, Receiver, ReceiverChooser}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class DSSOrchestratorChooser extends ReceiverChooser {

  @Autowired
  var orchestratorService: OrchestratorService = _

  @Autowired
  var orchestratorPluginService: OrchestratorPluginService = _

  @Autowired
  var orchestratorContext: DSSOrchestratorContext = _

  var receiver: Option[DSSOrchestratorReceiver] = _

  @PostConstruct
  def init(): Unit = receiver = Some(new DSSOrchestratorReceiver(orchestratorService, orchestratorPluginService, orchestratorContext))

  override def chooseReceiver(event: RPCMessageEvent): Option[Receiver] = event.message match {
    case _: RequestExportOrchestrator => receiver
    case _: RequestImportOrchestrator => receiver
    case _: RequestQueryOrchestrator => receiver
    case _: RequestFrameworkConvertOrchestration => receiver
    case _: RequestFrameworkConvertOrchestrationStatus => receiver
    case _: RequestAddVersionAfterPublish => receiver
    case _: RequestOrchestratorVersion => receiver
    case _: RequestOrchestratorInfos => receiver
    case _: RequestQueryByIdOrchestrator => receiver
    case _: RequestQuertByAppIdOrchestrator => receiver
    case _ => None
  }
}