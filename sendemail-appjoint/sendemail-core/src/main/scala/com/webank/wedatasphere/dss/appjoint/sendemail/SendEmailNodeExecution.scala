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

package com.webank.wedatasphere.dss.appjoint.sendemail

import java.util

import com.webank.wedatasphere.dss.appjoint.execution.NodeExecution
import com.webank.wedatasphere.dss.appjoint.execution.common.{CompletedNodeExecutionResponse, NodeExecutionResponse}
import com.webank.wedatasphere.dss.appjoint.execution.core.{AppJointNode, NodeContext}
import com.webank.wedatasphere.dss.appjoint.sendemail.conf.SendEmailAppJointSpringConfiguration
import com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent.parser.{FileEmailContentParser, HtmlEmailContentParser, PictureEmailContentParser, TableEmailContentParser}
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl
import com.webank.wedatasphere.dss.appjoint.service.session.Session
import com.webank.wedatasphere.linkis.common.exception.ErrorException
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}

/**
  * Created by shanhuang on 2019/10/12.
  */
class SendEmailNodeExecution extends AppJointUrlImpl with NodeExecution with Logging{

  private val sendEmailAppJointHooks = Array.empty[SendEmailNodeExecutionHook]
  private val emailContentParsers =
    Array[EmailContentParser](FileEmailContentParser, HtmlEmailContentParser, PictureEmailContentParser, TableEmailContentParser)
  private val emailContentGenerators = Array[EmailContentGenerator](SendEmailAppJointSpringConfiguration.createEmailContentGenerator())

  private val emailGenerator = SendEmailAppJointSpringConfiguration.createEmailGenerator()

  private val emailSender = SendEmailAppJointSpringConfiguration.createEmailSender()

  override def canExecute(node: AppJointNode, nodeContext: NodeContext, session: Session): Boolean = node.getNodeType.toLowerCase.contains("sendemail")

  override def execute(node: AppJointNode, nodeContext: NodeContext, seesion:Session): NodeExecutionResponse = {
    val response = new CompletedNodeExecutionResponse
    val email = Utils.tryCatch {
      sendEmailAppJointHooks.foreach(_.preGenerate(node, nodeContext))
      val email = emailGenerator.generateEmail(node, nodeContext)
      emailContentParsers.foreach{
        p => Utils.tryQuietly(p.parse(email))
      }
      emailContentGenerators.foreach{
        g => Utils.tryQuietly(g.generate(email))
      }
      sendEmailAppJointHooks.foreach(_.preSend(node, nodeContext, email))
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
                            response: CompletedNodeExecutionResponse): Unit = t match {
    case t: Throwable =>
      response.setErrorMsg(errorMsg)
      val exception = new ErrorException(80079, "failed to sendEmail")
      exception.initCause(t)
      logger.error(s"failed to send email, $errorMsg ", t)
      response.setException(exception)
      response.setIsSucceed(false)
  }


  override def init(params: util.Map[String, AnyRef]): Unit = {}
}
