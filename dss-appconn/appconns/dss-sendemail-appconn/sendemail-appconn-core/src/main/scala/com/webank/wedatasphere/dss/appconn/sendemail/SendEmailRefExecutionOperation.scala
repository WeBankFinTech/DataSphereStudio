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

package com.webank.wedatasphere.dss.appconn.sendemail

import java.util

import com.webank.wedatasphere.dss.appconn.sendemail.conf.SendEmailAppConnInstanceConfiguration
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef.ExecutionResponseRefBuilder
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.{ExecutionResponseRef, RefExecutionRequestRef}
import com.webank.wedatasphere.dss.standard.app.development.operation.{AbstractDevelopmentOperation, RefExecutionOperation}
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef
import org.apache.linkis.common.utils.Utils

import scala.collection.JavaConversions._

class SendEmailRefExecutionOperation
  extends AbstractDevelopmentOperation[RefExecutionRequestRef.RefExecutionRequestRefImpl, ResponseRef]
    with RefExecutionOperation[RefExecutionRequestRef.RefExecutionRequestRefImpl] {

  private val sendEmailAppConnHooks = SendEmailAppConnInstanceConfiguration.getSendEmailRefExecutionHooks
  private val emailContentParsers = SendEmailAppConnInstanceConfiguration.getEmailContentParsers
  private val emailContentGenerators = SendEmailAppConnInstanceConfiguration.getEmailContentGenerators
  private val emailGenerator = SendEmailAppConnInstanceConfiguration.getEmailGenerator
  private val emailSender = SendEmailAppConnInstanceConfiguration.getEmailSender


  override def init(): Unit = {
    super.init()
    val properties = new util.HashMap[String, String]
    service.getAppInstance.getConfig.foreach {
      case (key: String, value: Object) if value != null =>
        properties.put(key, value.toString)
      case _ =>
    }
    emailSender.init(properties)
  }

  override def execute(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl): ExecutionResponseRef = {
    val email = Utils.tryCatch {
      sendEmailAppConnHooks.foreach(_.preGenerate(requestRef))
      val email = emailGenerator.generateEmail(requestRef)
      emailContentParsers.foreach(_.parse(email))
      emailContentGenerators.foreach{
        g => Utils.tryQuietly(g.generate(email))
      }
      sendEmailAppConnHooks.foreach(_.preSend(requestRef, email))
      email
    }{ t =>
      return putErrorMsg(t.getMessage, t)
    }
    Utils.tryCatch {
      emailSender.send(email)
      new ExecutionResponseRefBuilder().success()
    }(putErrorMsg("发送邮件失败！", _))
  }

  protected def putErrorMsg(errorMsg: String, t: Throwable): ExecutionResponseRef = {
    logger.error(s"failed to send email, $errorMsg ", t)
    new ExecutionResponseRefBuilder().setException(t).setErrorMsg(errorMsg).error()
  }

}
