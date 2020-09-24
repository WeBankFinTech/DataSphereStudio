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

package com.webank.wedatasphere.dss.appjoint.sendemail.email

import java.io.File

import com.webank.wedatasphere.dss.appjoint.sendemail.Attachment
import com.webank.wedatasphere.dss.appjoint.sendemail.Attachment

/**
  * Created by shanhuang on 2019/10/12.
  */
class PngAttachment(name: String, b64: String, file: File) extends Attachment {

  override def getName: String = name

  override def getBase64Str: String = b64

  override def getFile: File = file

  override def getMediaType = "image/png"

}