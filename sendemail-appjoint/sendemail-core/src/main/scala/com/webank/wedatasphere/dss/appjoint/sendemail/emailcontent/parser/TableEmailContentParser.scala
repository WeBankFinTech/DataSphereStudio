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

package com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent.parser

import com.webank.wedatasphere.dss.appjoint.sendemail.email.MultiContentEmail
import com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent.TableEmailContent
import com.webank.wedatasphere.linkis.common.utils.Utils
import com.webank.wedatasphere.linkis.storage.resultset.table.{TableMetaData, TableRecord}
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.springframework.stereotype.Component

/**
  * Created by shanhuang on 2019/10/12.
  */

object TableEmailContentParser extends AbstractEmailContentParser[TableEmailContent] {
  override protected def parseEmailContent(emailContent: TableEmailContent,
                                           multiContentEmail: MultiContentEmail): Unit = {
    val reader = getResultSetReader(emailContent)
    val content = new StringBuilder
    reader.getMetaData match {
      case tableMetaData: TableMetaData =>
        writeTableTH(tableMetaData, content)
    }
    Utils.tryFinally {
      while(reader.hasNext) reader.getRecord match {
        case tableRecord: TableRecord =>
          writeTableTR(tableRecord, content)
      }
    }(IOUtils.closeQuietly(reader))
    emailContent.setContent(content.toString())
  }

  protected def writeTableTH(tableMetaData: TableMetaData, content: StringBuilder): Unit = {
    content.append("<th>")
    tableMetaData.columns.foreach{ c =>
      content.append("<td>").append(c.columnName)
      if(StringUtils.isNotBlank(c.comment)) {
        val comment = if(c.comment.length < 10) c.comment else c.comment.substring(0, 9) + "..."
        content.append("(").append(comment).append(")")
      }
      content.append("</td>")
    }
    content.append("</th>")
  }

  protected def writeTableTR(tableRecord: TableRecord, content: StringBuilder): Unit = {
    content.append("<tr>")
    tableRecord.row.foreach(v => content.append("<td>").append(v).append("</td>"))
    content.append("</tr>")
  }

}
