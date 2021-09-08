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
import com.webank.wedatasphere.dss.appconn.sendemail.email.EmailSender
import com.webank.wedatasphere.dss.appconn.sendemail.email.sender.SpringJavaEmailSender
import com.webank.wedatasphere.dss.common.utils.ClassUtils
import com.webank.wedatasphere.dss.standard.app.development.listener.common.CompletedExecutionResponseRef
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef
import com.webank.wedatasphere.linkis.common.exception.ErrorException
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import org.springframework.mail.javamail.JavaMailSender

import scala.collection.JavaConversions._

class SendEmailRefExecutionOperation extends RefExecutionOperation with Logging {
  val EMAIL_FROM_DEFAULT = "email.from.default"
  val EMAIL_HOST = "email.host"
  val EMAIL_USERNAME = "email.username"
  val EMAIL_PASSWORD = "email.password"
  val EMAIL_PORT = "email.port"
  val EMAIL_PROTOCOL = "email.protocol"

  private var service:DevelopmentService = _

  private val sendEmailAppConnHooks = SendEmailAppConnInstanceConfiguration.getSendEmailRefExecutionHooks
  private val emailContentParsers = SendEmailAppConnInstanceConfiguration.getEmailContentParsers
  private val emailContentGenerators = SendEmailAppConnInstanceConfiguration.getEmailContentGenerators
  private val emailGenerator = SendEmailAppConnInstanceConfiguration.getEmailGenerator
  private val emailSender = SendEmailAppConnInstanceConfiguration.getEmailSender

  override def execute(requestRef: ExecutionRequestRef): ResponseRef = {
    val instanceConfig = this.service.getAppInstance.getConfig
    val properties = new Properties()
    instanceConfig.foreach {
      case (key: String, value: Object) =>
        properties.put(key, value.toString)
    }
    val springJavaEmailSender =  new SpringJavaEmailSender()
    val javaMailSender = springJavaEmailSender.getJavaMailSender
    javaMailSender.setHost(properties.getProperty(EMAIL_HOST))
    javaMailSender.setPort(Integer.parseInt(properties.getProperty(EMAIL_PORT)))
    javaMailSender.setUsername(properties.getProperty(EMAIL_USERNAME))
    javaMailSender.setPassword(properties.getProperty(EMAIL_PASSWORD))
    javaMailSender.setProtocol(properties.getProperty(EMAIL_PROTOCOL))
    val emailSender = ClassUtils.getInstanceOrDefault(classOf[EmailSender],springJavaEmailSender)
    val response = new CompletedExecutionResponseRef(200)
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
      putErrorMsg("解析邮件内容失败！", t, response)
      return response
    }
    Utils.tryCatch {
     emailSender.send(email)
      response.setIsSucceed(true)
    }(putErrorMsg("发送邮件失败！", _, response))
    response
  }

  protected def putErrorMsg(errorMsg: String, t: Throwable,
                            response: CompletedExecutionResponseRef): Unit = t match {
    case t: Throwable =>
      response.setErrorMsg(errorMsg)
      val exception = new ErrorException(80079, "failed to sendEmail")
      exception.initCause(t)
      logger.error(s"failed to send email, $errorMsg ", t)
      response.setException(exception)
      response.setIsSucceed(false)
  }

  override def setDevelopmentService(service: DevelopmentService): Unit = {
    this.service = service
  }
}
