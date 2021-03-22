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

import com.webank.wedatasphere.dss.appconn.sendemail.cs.EmailCSHelper
import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.{AbstractEmail, MultiContentEmail}
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.domain.PictureEmailContent
import com.webank.wedatasphere.dss.appconn.sendemail.exception.EmailSendFailedException
import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef
import com.webank.wedatasphere.linkis.storage.resultset.ResultSetFactory

/**
  * Created by enjoyyin on 2019/10/13.
  */
class MultiContentEmailGenerator extends AbstractEmailGenerator {

  override protected def createEmail(): AbstractEmail = new MultiContentEmail

  override protected def generateEmailContent(requestRef: ExecutionRequestRef, email: AbstractEmail): Unit = email match {
    case multiContentEmail: MultiContentEmail =>
      val runtimeMap = getRuntimeMap(requestRef)
      val refContext = getExecutionRequestRefContext(requestRef)
      runtimeMap.get("category") match {
        case "node" =>
          val resultSetFactory = ResultSetFactory.getInstance
          EmailCSHelper.getJobIds(refContext).foreach { jobId =>
            refContext.fetchLinkisJobResultSetPaths(jobId).foreach { fsPath =>
              val resultSet = resultSetFactory.getResultSetByPath(fsPath)
              val emailContent = resultSet.resultSetType() match {
                case ResultSetFactory.PICTURE_TYPE => new PictureEmailContent(fsPath)
                case ResultSetFactory.HTML_TYPE => throw new EmailSendFailedException(80003 ,"html result set is not allowed")//new HtmlEmailContent(fsPath)
                case ResultSetFactory.TABLE_TYPE => throw new EmailSendFailedException(80003 ,"table result set is not allowed")//new TableEmailContent(fsPath)
                case ResultSetFactory.TEXT_TYPE => throw new EmailSendFailedException(80003 ,"text result set is not allowed")//new FileEmailContent(fsPath)
              }
              multiContentEmail.addEmailContent(emailContent)
            }
          }
        case "file" => throw new EmailSendFailedException(80003 ,"file content is not allowed") //addContentEmail(c => new FileEmailContent(new FsPath(c)))
        case "text" => throw new EmailSendFailedException(80003 ,"text content is not allowed")//addContentEmail(new TextEmailContent(_))
        case "link" => throw new EmailSendFailedException(80003 ,"link content is not allowed")//addContentEmail(new UrlEmailContent(_))
      }
  }




}