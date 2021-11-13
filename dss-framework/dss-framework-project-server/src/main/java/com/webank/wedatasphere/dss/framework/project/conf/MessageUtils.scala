/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.dss.framework.project.conf

import org.apache.linkis.server.Message
import javax.ws.rs.core.Response


object MessageUtils {
  implicit def messageToResponse(message: Message): Response =
    Response.status(messageToHttpStatus(message)).entity(message).build()
  implicit def responseToMessage(response: Response): Message = response.readEntity(classOf[Message])
  def messageToHttpStatus(message: Message): Int = message.getStatus match {
    case -1 => 401
    case 0 => 200
    case 1 => 400
    case 2 => 412
    case 3 => 403
    case 4 => 206
  }
}

