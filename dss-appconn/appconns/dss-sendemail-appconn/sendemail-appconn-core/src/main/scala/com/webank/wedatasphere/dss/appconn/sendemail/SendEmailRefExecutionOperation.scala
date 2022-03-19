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

import java.util.Properties

import com.webank.wedatasphere.dss.appconn.sendemail.conf.SendEmailAppConnInstanceConfiguration
import com.webank.wedatasphere.dss.appconn.sendemail.email.sender.SpringJavaEmailSender
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef.ExecutionResponseRefBuilder
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.{ExecutionResponseRef, RefExecutionRequestRef}
import com.webank.wedatasphere.dss.standard.app.development.operation.{AbstractDevelopmentOperation, RefExecutionOperation}
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef
import org.apache.linkis.common.utils.Utils

import scala.collection.JavaConversions._

class SendEmailRefExecutionOperation
  extends AbstractDevelopmentOperation[RefExecutionRequestRef.RefExecutionRequestRefImpl, ResponseRef]
    with RefExecutionOperation[RefExecutionRequestRef.RefExecutionRequestRefImpl] {

  val EMAIL_FROM_DEFAULT = "email.from.default"
  val EMAIL_HOST = "email.host"
  val EMAIL_USERNAME = "email.username"
  val EMAIL_PASSWORD = "email.password"
  val EMAIL_PORT = "email.port"
  val EMAIL_PROTOCOL = "email.protocol"

  private val sendEmailAppConnHooks = SendEmailAppConnInstanceConfiguration.getSendEmailRefExecutionHooks
  private val emailContentParsers = SendEmailAppConnInstanceConfiguration.getEmailContentParsers
  private val emailContentGenerators = SendEmailAppConnInstanceConfiguration.getEmailContentGenerators
  private val emailGenerator = SendEmailAppConnInstanceConfiguration.getEmailGenerator
  private val emailSender = SendEmailAppConnInstanceConfiguration.getEmailSender

  override def execute(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl): ExecutionResponseRef = {
    val instanceConfig = this.service.getAppInstance.getConfig
    val properties = new Properties()
    instanceConfig.foreach {
      case (key: String, value: Object) =>
        properties.put(key, value.toString)
    }
    emailSender match {
      case springJavaEmailSender: SpringJavaEmailSender =>
        val javaMailSender = springJavaEmailSender.getJavaMailSender
        javaMailSender.setHost(properties.getProperty(EMAIL_HOST))
        javaMailSender.setPort(Integer.parseInt(properties.getProperty(EMAIL_PORT)))
        javaMailSender.setUsername(properties.getProperty(EMAIL_USERNAME))
        javaMailSender.setPassword(properties.getProperty(EMAIL_PASSWORD))
        javaMailSender.setProtocol(properties.getProperty(EMAIL_PROTOCOL))
      case _ =>
    }
    val email = Utils.tryCatch {
      sendEmailAppConnHooks.foreach(_.preGenerate(requestRef))
      val email = emailGenerator.generateEmail(requestRef)
      emailContentParsers.foreach{
        p => Utils.tryQuietly(p.parse(email))
      }
      emailContentGenerators.foreach{
        g => Utils.tryQuietly(g.generate(email))
      }
      sendEmailAppConnHooks.foreach(_.preSend(requestRef, email))
      email
    }{ t =>
      return putErrorMsg("解析邮件内容失败！", t)
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
