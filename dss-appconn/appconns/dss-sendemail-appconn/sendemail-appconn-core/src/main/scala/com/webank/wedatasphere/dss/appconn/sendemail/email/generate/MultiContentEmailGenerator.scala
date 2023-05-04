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

import com.google.gson.internal.LinkedTreeMap
import com.webank.wedatasphere.dss.appconn.sendemail.cs.EmailCSHelper
import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.{AbstractEmail, MultiContentEmail}
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.domain.{HtmlEmailContent, PictureEmailContent}
import com.webank.wedatasphere.dss.appconn.sendemail.exception.EmailSendFailedException
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.log.LogUtils
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.server.JSONUtils
import org.apache.linkis.storage.LineMetaData
import org.apache.linkis.storage.resultset.ResultSetFactory

import java.util

class MultiContentEmailGenerator extends AbstractEmailGenerator {

  override protected def createEmail(): AbstractEmail = new MultiContentEmail

  override protected def generateEmailContent(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl, email: AbstractEmail): Unit = email match {
    case multiContentEmail: MultiContentEmail =>
      val runtimeMap = getRuntimeMap(requestRef)
      val refContext = getExecutionRequestRefContext(requestRef)
      runtimeMap.get("category") match {
        case "node" =>
          val resultSetFactory = ResultSetFactory.getInstance
          EmailCSHelper.getJobIds(refContext).foreach { jobId =>
            refContext.fetchLinkisJobResultSetPaths(jobId).foreach { fsPath =>
              var fileType = "";
              val reader = refContext.getResultSetReader(fsPath)
              val meta = reader.getMetaData.cloneMeta()
              Utils.tryFinally(meta match {
                case metadata: LineMetaData =>
                  val data = JSONUtils.gson.fromJson(metadata.getMetaData, classOf[util.Map[String, String]])
                  // 如果是pdf附件存字段pdf到multiContentEmail
                  if (data.get("type") != null && data.get("format") != null) {
                    fileType = data.get("format").toString
                  }
                case _ =>
              })(Utils.tryQuietly(reader.close()))
              val resultSet = resultSetFactory.getResultSetByPath(fsPath)
              val emailContent = resultSet.resultSetType() match {
                case ResultSetFactory.PICTURE_TYPE => new PictureEmailContent(fsPath, fileType)
                case ResultSetFactory.HTML_TYPE =>
                  multiContentEmail.setEmailType("html")
                  new HtmlEmailContent (fsPath, fileType)
                case ResultSetFactory.TABLE_TYPE => throw new EmailSendFailedException(80003 ,"table result set is not allowed")
                case ResultSetFactory.TEXT_TYPE => throw new EmailSendFailedException(80003 ,"text result set is not allowed")
              }
              multiContentEmail.addEmailContent(emailContent)
            }
            if (StringUtils.isBlank(multiContentEmail.getEmailType)) {
              val emailType = refContext.fetchLinkisJob(jobId).getParams.get("labels").asInstanceOf[LinkedTreeMap[_,_]].get("codeType").toString
              multiContentEmail.setEmailType(emailType)
            }
          }
        case "file" => throw new EmailSendFailedException(80003 ,"file content is not allowed")
        case "text" => throw new EmailSendFailedException(80003 ,"text content is not allowed")
        case "link" => throw new EmailSendFailedException(80003 ,"link content is not allowed")
      }
  }
}