package com.webank.wedatasphere.dss.common.utils

import org.apache.linkis.server.Message

import javax.ws.rs.core.Response

object MessageUtils {
  implicit def messageToResponse(message: Message): Response =
    Response.status(messageToHttpStatus(message)).entity(message).build()

  //implicit def responseToMessage(response: Response): Message = response.readEntity(classOf[Message])

  def messageToHttpStatus(message: Message): Int = message.getStatus match {
    case -1 => 401
    case 0 => 200
    case 1 => 400
    case 2 => 412
    case 3 => 403
    case 4 => 206
  }
}
