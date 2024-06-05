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

package com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.parser

import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.{CsvAttachment, MultiContentEmail, PngAttachment}
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.HtmlItem
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.domain.HtmlEmailContent
import org.apache.linkis.server.JSONUtils

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.sys.error

object HtmlEmailContentParser extends AbstractEmailContentParser[HtmlEmailContent] {
  override protected def parseEmailContent(emailContent: HtmlEmailContent,
                                           multiContentEmail: MultiContentEmail): Unit = {
    getFirstLineRecord(emailContent).foreach(htmlStr =>
      emailContent.getFileType match {
        case "html" =>
          val htmlItems: Array[HtmlItem] = JSONUtils.gson.fromJson(htmlStr, classOf[Array[HtmlItem]])
          htmlItems.foreach {
            case htmlItem: HtmlItem =>
              if (htmlItem.getFileType.equals("attachment") && htmlItem.getContentType.equals("csv")) {
                val csvName = htmlItem.getFileName
                val csvContent = htmlItem.getContent
                val csvContentBytes = csvContent.getBytes
                val csvBase64BaseContent = new String(Base64.getEncoder.encode(csvContentBytes), StandardCharsets.UTF_8)
                multiContentEmail.addAttachment(new CsvAttachment(csvName, csvBase64BaseContent))
              } else if (htmlItem.getContentType.equals("image") && htmlItem.getFileType.equals("inline")) {
                multiContentEmail.addAttachment(new PngAttachment(htmlItem.getContentId, htmlItem.getContent))
              } else if (htmlItem.getContentType.equals("html")) {
                emailContent.setContent(htmlItem.getContent)
              } else {
                error("unknow content type: " + emailContent.getFileType)
              }
          }
        case _ =>
      }
    )
  }
}
