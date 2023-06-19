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

package com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.domain

import org.apache.linkis.common.io.FsPath

import scala.beans.BeanProperty

package object emailcontent {

}

class PictureEmailContent extends ArrayEmailContent with FsPathStoreEmailContent {

  def this(filePath: FsPath) = {
    this()
    setFsPath(filePath)
  }

  def this(filePath: FsPath, fileType: String) = {
    this()
    setFsPath(filePath)
    setFileType(fileType)
  }

}

class HtmlEmailContent extends StringEmailContent with FsPathStoreEmailContent {

  def this(filePath: FsPath, fileType: String) = {
    this()
    setFsPath(filePath)
    setFileType(fileType)
  }

}

class TableEmailContent extends StringEmailContent with FsPathStoreEmailContent {

  def this(filePath: FsPath) = {
    this()
    setFsPath(filePath)
  }

}

class FileEmailContent extends StringEmailContent with FsPathStoreEmailContent {

  def this(filePath: FsPath) = {
    this()
    setFsPath(filePath)
  }

}

class TextEmailContent extends StringEmailContent {

  def this(text: String) = {
    this()
    setContent(text)
  }

}

class UrlEmailContent extends StringEmailContent {

  @BeanProperty var url: String = _

  def this(url: String) = {
    this()
    this.url = url
  }

}