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

package com.webank.wedatasphere.dss.appconn.sendemail.email.generate

import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.AbstractEmail
import com.webank.wedatasphere.dss.appconn.sendemail.email.{Email, EmailGenerator}
import com.webank.wedatasphere.dss.standard.app.development.listener.core.ExecutionRequestRefContext
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef
import org.apache.linkis.common.utils.{Logging, VariableUtils}

trait AbstractEmailGenerator extends EmailGenerator with Logging{

  protected def createEmail(): AbstractEmail

  override def generateEmail(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl): Email = {
    val email = createEmail()
    generateEmailInfo(requestRef, email)
    generateEmailContent(requestRef, email)
    email
  }

  protected def getRuntimeMap(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl): java.util.Map[String, AnyRef] =
    requestRef.getExecutionRequestRefContext.getRuntimeMap

  protected def getExecutionRequestRefContext(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl): ExecutionRequestRefContext =
    requestRef.getExecutionRequestRefContext

  protected def generateEmailInfo(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl, email: AbstractEmail): Unit = {
    import scala.collection.JavaConversions._
    val runtimeMap = getRuntimeMap(requestRef)
    runtimeMap foreach {
      case (k, v) => logger.info(s"K is $k, V is $v")
    }
    val subject = if (runtimeMap.get("subject") != null) {
      VariableUtils.replace(runtimeMap.get("subject").toString)
    } else{
      "This is an email"
    }
    email.setSubject(subject)
    val bcc = if (runtimeMap.get("bcc") != null) runtimeMap.get("bcc").toString else ""
    email.setBcc(bcc)
    val cc = if (runtimeMap.get("cc") != null) runtimeMap.get("cc").toString else ""
    email.setCc(cc)
    val from = if (runtimeMap.get("from") != null) runtimeMap.get("from").toString else
      if(runtimeMap.get("wds.dss.workflow.submit.user") != null){
      runtimeMap.get("wds.dss.workflow.submit.user").toString
    } else runtimeMap.get("user").toString
    email.setFrom(from)
    val to = if (runtimeMap.get("to") != null) runtimeMap.get("to").toString else ""
    email.setTo(to)
  }

  protected def generateEmailContent(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl, email: AbstractEmail): Unit

}
