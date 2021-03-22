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

package com.webank.wedatasphere.dss.appconn.sendemail.email.domain

import com.webank.wedatasphere.dss.appconn.sendemail.email.Email

import scala.collection.mutable.ArrayBuffer

/**
  * Created by enjoyyin on 2019/10/12.
  */
class AbstractEmail extends Email {

  private var content: String = _
  private var attachments = ArrayBuffer[Attachment]()
  private var subject: String = _
  private var from: String = _
  private var to: String = _
  private var cc: String = _
  private var bcc: String = _

  override def getContent: String = content
  override def setContent(content: String): Unit = this.content = content

  override def getAttachments: Array[Attachment] = attachments.toArray
  override def setAttachments(attachments: Array[Attachment]): Unit =
    this.attachments ++= attachments
  def addAttachment(attachment: Attachment): Unit = this.attachments += attachment

  override def getSubject: String = subject
  override def setSubject(subject: String): Unit = this.subject = subject

  override def getFrom: String = from
  override def setFrom(from: String): Unit = this.from = from

  override def getTo: String = to
  override def setTo(to: String): Unit = this.to = to

  override def getCc: String = cc
  override def setCc(cc: String): Unit = this.cc = cc

  override def getBcc: String = bcc
  override def setBcc(bcc: String): Unit = this.bcc = bcc
}
