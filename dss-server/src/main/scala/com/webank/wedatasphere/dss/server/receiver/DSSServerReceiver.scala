/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.server.receiver

import com.webank.wedatasphere.dss.application.dao.ApplicationMapper
import com.webank.wedatasphere.dss.common.exception.DSSErrorException
import com.webank.wedatasphere.dss.common.protocol.{RequestDSSApplication, RequestDSSProject}
import com.webank.wedatasphere.dss.server.service.DSSProjectService
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.duration.Duration


@Component
class DSSServerReceiver extends Receiver{

  @Autowired
  var dwsProjectService:DSSProjectService = _

  @Autowired
  var applicationMapper:ApplicationMapper = _

  override def receive(message: Any, sender: Sender): Unit = {}

  override def receiveAndReply(message: Any, sender: Sender): Any = message match {
    case f:RequestDSSProject => dwsProjectService.getExecutionDSSProject(f)
    case RequestDSSApplication(name) => applicationMapper.getApplication(name)
    case _ =>throw new DSSErrorException(90000,"")
  }

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = {}
}
