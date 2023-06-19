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

package com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.generator

import com.webank.wedatasphere.dss.appconn.sendemail.email.Email
import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.MultiContentEmail
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.domain.{ArrayEmailContent, StringEmailContent}
import org.apache.linkis.common.utils.Logging


class MultiEmailContentGenerator extends AbstractEmailContentGenerator with Logging {

  override def generate(email: Email): Unit = email match {
    case multiContentEmail: MultiContentEmail =>
      formatSubjectOfOldVersion(email)
      formatSubject(multiContentEmail)
      if (multiContentEmail.getEmailType.equals("html")) {
        setHtmlContent(multiContentEmail)
      } else {
        formatContent(multiContentEmail)
      }
  }

  protected def formatContent(email: MultiContentEmail): Unit = {
    val sb: StringBuilder = new StringBuilder("<html><body>")
    sb.append("<table cellspacing=0 cellpadding=0>")
    email.getEmailContents.foreach {
      case emailContent: ArrayEmailContent =>
        if (emailContent.getContent != null) {
          emailContent.getContent.foreach(content => sb.append("<tr><td>").append(content).append("</td></tr>"))
        }
      case emailContent: StringEmailContent =>
        sb.append("<tr><td>").append(emailContent.getContent).append("</td></tr>")
    }
    sb.append("</table>")
    sb.append("</body></html>")
    email.setContent(sb.toString)
  }

  protected def setHtmlContent(email: MultiContentEmail): Unit = {
    email.getEmailContents.foreach {
      case emailContent: StringEmailContent =>
        if (emailContent.getContent != null) {
          email.setContent(emailContent.getContent)
        }
    }
  }

}