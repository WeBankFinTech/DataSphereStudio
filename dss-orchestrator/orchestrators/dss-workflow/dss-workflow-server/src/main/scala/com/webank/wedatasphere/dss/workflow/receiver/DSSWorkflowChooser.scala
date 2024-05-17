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

package com.webank.wedatasphere.dss.workflow.receiver

import com.webank.wedatasphere.dss.common.protocol.{RequestDeleteWorkflow, RequestExportWorkflow, RequestQueryWorkFlow, RequestUpdateWorkflow}
import com.webank.wedatasphere.dss.orchestrator.common.protocol._
import com.webank.wedatasphere.dss.workflow.WorkFlowManager
import com.webank.wedatasphere.dss.workflow.common.protocol.{RequestCopyWorkflow, RequestCreateWorkflow, RequestDeleteBmlSource, RequestImportWorkflow, RequestLockWorkflow, RequestSubFlowContextIds, RequestUnlockWorkflow}
import org.apache.linkis.rpc.{RPCMessageEvent, Receiver, ReceiverChooser}

import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DSSWorkflowChooser extends ReceiverChooser {


  @Autowired
  var workflowManager: WorkFlowManager = _

  var receiver: Option[DSSWorkflowReceiver] = _

  @PostConstruct
  def init(): Unit = receiver = Some(new DSSWorkflowReceiver(workflowManager))

  override def chooseReceiver(event: RPCMessageEvent): Option[Receiver] = event.message match {
    case _: RequestCreateWorkflow => receiver
    case _: RequestUpdateWorkflow => receiver
    case _: RequestDeleteWorkflow => receiver
    case _: RequestExportWorkflow => receiver
    case _: RequestImportWorkflow => receiver
    case _: RequestCopyWorkflow => receiver
    case _: RequestQueryWorkFlow => receiver
    case _: RequestUnlockWorkflow => receiver
    case _: RequestConvertOrchestrations => receiver
    case _: RequestSubFlowContextIds => receiver
    case _: RequestDeleteBmlSource => receiver

    case _ => None
  }
}