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

import com.webank.wedatasphere.dss.appjoint.sendemail._
import com.webank.wedatasphere.dss.appjoint.sendemail.email.{AbstractEmail, MultiContentEmail}
import com.webank.wedatasphere.linkis.common.io.FsPath
import com.webank.wedatasphere.dss.appjoint.execution.core.{AppJointNode, NodeContext}
import com.webank.wedatasphere.dss.appjoint.sendemail.EmailContent
import com.webank.wedatasphere.dss.appjoint.sendemail.email.{AbstractEmail, MultiContentEmail}
import com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent._
import com.webank.wedatasphere.linkis.storage.resultset.ResultSetFactory

/**
  * Created by shanhuang on 2019/10/12.
  */
class MultiContentEmailGenerator extends AbstractEmailGenerator {

  override protected def createEmail() = new MultiContentEmail

  override protected def generateEmailContent(node: AppJointNode, nodeContext: NodeContext, email: AbstractEmail): Unit = email match {
    case multiContentEmail: MultiContentEmail =>
      def addContentEmail(getEmailContent: String => EmailContent[_]): Unit = nodeContext.getRuntimeMap.get("content") match {
        case content: Array[String] => content.foreach(c => multiContentEmail.addEmailContent(getEmailContent(c)))
        case content: String => multiContentEmail.addEmailContent(getEmailContent(content))
      }
      nodeContext.getRuntimeMap.get("category") match {
        case "node" =>
          val resultSetFactory = ResultSetFactory.getInstance
          nodeContext.getJobIdsOfShareNode.foreach { jobId =>
            nodeContext.getResultSetPathsByJobId(jobId).foreach { fsPath =>
              val resultSet = resultSetFactory.getResultSetByPath(fsPath)
              val emailContent = resultSet.resultSetType() match {
                case ResultSetFactory.PICTURE_TYPE => new PictureEmailContent(fsPath)
                case ResultSetFactory.HTML_TYPE => new HtmlEmailContent(fsPath)
                case ResultSetFactory.TABLE_TYPE => new TableEmailContent(fsPath)
                case ResultSetFactory.TEXT_TYPE => new FileEmailContent(fsPath)
              }
              multiContentEmail.addEmailContent(emailContent)
            }
          }
        case "file" => addContentEmail(c => new FileEmailContent(new FsPath(c)))
        case "text" => addContentEmail(new TextEmailContent(_))
        case "link" => addContentEmail(new UrlEmailContent(_))
      }
  }

}