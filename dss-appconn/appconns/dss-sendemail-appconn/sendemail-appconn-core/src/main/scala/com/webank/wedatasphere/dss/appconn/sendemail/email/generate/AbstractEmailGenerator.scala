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

package com.webank.wedatasphere.dss.appconn.sendemail.email.generate

import com.webank.wedatasphere.dss.appconn.sendemail.email.{Email, EmailGenerator}
import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.AbstractEmail
import com.webank.wedatasphere.dss.appconn.sendemail.exception.EmailSendFailedException
import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.development.execution.common.AsyncExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.development.execution.core.ExecutionRequestRefContext
import com.webank.wedatasphere.linkis.common.utils.Logging

/**
  * Created by enjoyyin on 2019/10/13.
  */
trait AbstractEmailGenerator extends EmailGenerator with Logging{

  protected def createEmail(): AbstractEmail

  override def generateEmail(requestRef: ExecutionRequestRef): Email = {
    val email = createEmail()
    generateEmailInfo(requestRef, email)
    generateEmailContent(requestRef, email)
    email
  }

  protected def getRuntimeMap(requestRef: ExecutionRequestRef): java.util.Map[String, AnyRef] =
    requestRef match {
      case r: AsyncExecutionRequestRef => r.getExecutionRequestRefContext.getRuntimeMap
      case _ => requestRef.getParameters
    }

  protected def getExecutionRequestRefContext(requestRef: ExecutionRequestRef): ExecutionRequestRefContext =
    requestRef match {
      case r: AsyncExecutionRequestRef => r.getExecutionRequestRefContext
      case _ => throw new EmailSendFailedException(80002, "ExecutionRequestRefContext is empty!")
    }

  protected def generateEmailInfo(requestRef: ExecutionRequestRef, email: AbstractEmail): Unit = {
    import scala.collection.JavaConversions._
    val runtimeMap = getRuntimeMap(requestRef)
    runtimeMap foreach {
      case (k, v) => logger.info(s"K is $k, V is $v")
    }
    val subject = if (runtimeMap.get("subject") != null) runtimeMap.get("subject").toString else "This is an email"
    email.setSubject(subject)
    val bcc = if (runtimeMap.get("bcc") != null) runtimeMap.get("bcc").toString else ""
    email.setBcc(bcc)
    val cc = if (runtimeMap.get("cc") != null) runtimeMap.get("cc").toString else ""
    email.setCc(cc)
    val from = if (runtimeMap.get("from") != null) runtimeMap.get("from").toString else
      if(runtimeMap.get("wds.linkis.schedulis.submit.user") != null){
      runtimeMap.get("wds.linkis.schedulis.submit.user").toString
    } else runtimeMap.get("user").toString
    email.setFrom(from)
    val to = if (runtimeMap.get("to") != null) runtimeMap.get("to").toString else ""
    email.setTo(to)
  }

  protected def generateEmailContent(requestRef: ExecutionRequestRef, email: AbstractEmail): Unit

}
