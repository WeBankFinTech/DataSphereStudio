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

package com.webank.wedatasphere.dss.appjoint.sendemail.email.generate

import com.webank.wedatasphere.dss.appjoint.sendemail.email.AbstractEmail
import com.webank.wedatasphere.dss.appjoint.sendemail.Email
import com.webank.wedatasphere.dss.appjoint.execution.core.{AppJointNode, NodeContext}
import com.webank.wedatasphere.dss.appjoint.sendemail.email.AbstractEmail
import com.webank.wedatasphere.dss.appjoint.sendemail.{Email, EmailGenerator}
import com.webank.wedatasphere.linkis.common.utils.Logging

/**
  * Created by shanhuang on 2019/10/12.
  */
trait AbstractEmailGenerator extends EmailGenerator with Logging{

  protected def createEmail(): AbstractEmail

  override def generateEmail(node: AppJointNode, nodeContext: NodeContext): Email = {
    val email = createEmail()
    generateEmailInfo(node, nodeContext, email)
    generateEmailContent(node, nodeContext, email)
    email
  }

  protected def generateEmailInfo(node: AppJointNode, nodeContext: NodeContext, email: AbstractEmail): Unit = {
    import scala.collection.JavaConversions._
    nodeContext.getRuntimeMap foreach {
      case (k, v) => logger.info(s"K is $k, V is $v")
    }
    val subject = if (nodeContext.getRuntimeMap.get("subject") != null) nodeContext.getRuntimeMap.get("subject").toString else "This is an email"
    email.setSubject(subject)
    val bcc = if (nodeContext.getRuntimeMap.get("bcc") != null) nodeContext.getRuntimeMap.get("bcc").toString else ""
    email.setBcc(bcc)
    val cc = if (nodeContext.getRuntimeMap.get("cc") != null) nodeContext.getRuntimeMap.get("cc").toString else ""
    email.setCc(cc)
    val from = if (nodeContext.getRuntimeMap.get("from") != null) nodeContext.getRuntimeMap.get("from").toString else
      nodeContext.getRuntimeMap.get("user").toString
    email.setFrom(from)
    val to = if (nodeContext.getRuntimeMap.get("to") != null) nodeContext.getRuntimeMap.get("to").toString else ""
    email.setTo(to)
  }

  protected def generateEmailContent(node: AppJointNode, nodeContext: NodeContext, email: AbstractEmail): Unit

}
