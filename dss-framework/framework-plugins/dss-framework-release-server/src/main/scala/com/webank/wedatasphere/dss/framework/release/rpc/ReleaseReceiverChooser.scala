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

import com.webank.wedatasphere.dss.common.protocol.framework.{ReleaseOrchestratorRequest, ReleaseOrchestratorStatusRequest}
import com.webank.wedatasphere.dss.framework.release.service.ReleaseService
import com.webank.wedatasphere.linkis.rpc.{RPCMessageEvent, Receiver, ReceiverChooser}
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author allenlliu
 * @date 2021/3/15 21:37
 */
@Component
class ReleaseReceiverChooser extends ReceiverChooser {


  @Autowired
  var releaseService: ReleaseService = _

  private var receiver: Option[ReleaseReceiver] = _

  @PostConstruct
  def init(): Unit = receiver = Some(new ReleaseReceiver(releaseService))

  override def chooseReceiver(event: RPCMessageEvent): Option[Receiver] = event.message match {
    case _: ReleaseOrchestratorRequest => receiver
    case _: ReleaseOrchestratorStatusRequest => receiver
    case _ => None
  }
}
