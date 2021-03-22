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

package com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.parser

import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.MultiContentEmail
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.domain.FileEmailContent
import com.webank.wedatasphere.linkis.common.utils.Utils
import com.webank.wedatasphere.linkis.storage.LineRecord
import org.apache.commons.io.IOUtils

/**
  * Created by enjoyyin on 2019/10/13.
  */

object FileEmailContentParser extends AbstractEmailContentParser[FileEmailContent] {
  override protected def parseEmailContent(emailContent: FileEmailContent,
                                           multiContentEmail: MultiContentEmail): Unit = {
    val reader = getResultSetReader(emailContent)
    val content = new StringBuilder
    Utils.tryFinally{
      while(reader.hasNext) {
        reader.getRecord match {
          case lineRecord: LineRecord =>
            content.append(lineRecord.getLine).append("<br/>")
        }
      }
    }(IOUtils.closeQuietly(reader))
    emailContent.setContent(content.toString())
  }
}