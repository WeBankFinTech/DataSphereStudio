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

package com.webank.wedatasphere.dss.appconn.sendemail.email.domain

import java.util

import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.EmailContent

import scala.collection.JavaConversions._

class MultiContentEmail extends AbstractEmail {

  private val emailContents = new util.ArrayList[EmailContent[_]]()

  def addEmailContent(emailContent: EmailContent[_]): Unit = emailContents.add(emailContent)

  def getEmailContents: Array[EmailContent[_]] = emailContents.toIterator.toArray

}
