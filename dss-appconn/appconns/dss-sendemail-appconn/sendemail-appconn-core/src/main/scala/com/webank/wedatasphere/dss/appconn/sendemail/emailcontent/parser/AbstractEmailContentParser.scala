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

import java.lang.reflect.{ParameterizedType, Type}

import com.webank.wedatasphere.dss.appconn.sendemail.email.Email
import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.MultiContentEmail
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.domain.FsPathStoreEmailContent
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.{EmailContent, EmailContentParser}
import org.apache.linkis.common.io.resultset.ResultSetReader
import org.apache.linkis.common.io.{MetaData, Record}
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.storage.LineRecord
import org.apache.linkis.storage.resultset.ResultSetReaderFactory
import org.apache.commons.io.IOUtils

abstract class AbstractEmailContentParser[T] extends EmailContentParser {

  override def parse(email: Email): Unit = email match {
    case multiContentEmail: MultiContentEmail =>
      multiContentEmail.getEmailContents.foreach {
        case t: EmailContent[_] if t.getClass == getEmailContentClass =>
          parseEmailContent(t.asInstanceOf[T], multiContentEmail)
        case _ =>
      }
    case _ =>
  }

  protected def getResultSetReader(fsPathStore: FsPathStoreEmailContent): ResultSetReader[_, _ ] = {
    val reader = ResultSetReaderFactory.getResultSetReader(fsPathStore.getFsPath.getSchemaPath)
    reader.getMetaData
    reader
  }

  protected def getFirstLineRecord(fsPathStore: FsPathStoreEmailContent): Option[String] = {
    val reader = getResultSetReader(fsPathStore)
    if(!reader.hasNext) None
    else Utils.tryFinally(reader.getRecord match {
      case record: LineRecord => Option(record.getLine)
    })(IOUtils.closeQuietly(reader))
  }

  protected def getEmailContentClass: Type = getClass.getGenericSuperclass match {
    case pType: ParameterizedType => pType.getActualTypeArguments.head
  }

  protected def parseEmailContent(emailContent: T,
                                  multiContentEmail: MultiContentEmail): Unit

}
