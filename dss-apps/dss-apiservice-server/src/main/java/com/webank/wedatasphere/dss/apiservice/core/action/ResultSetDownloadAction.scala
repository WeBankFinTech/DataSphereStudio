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

package com.webank.wedatasphere.dss.apiservice.core.action

import com.webank.wedatasphere.linkis.httpclient.request.{DownloadAction, GetAction}
import com.webank.wedatasphere.linkis.ujes.client.request.UJESJobAction

import scala.tools.nsc.interpreter.InputStream

/**
 * @author allenlliu
 * @version 2.0.0
 */

class ResultSetDownloadAction extends GetAction with  DownloadAction with UJESJobAction  {

  private var inputStream: InputStream = _

  override def write(inputStream: InputStream): Unit = this.inputStream = inputStream

  def getInputStream: InputStream = inputStream

  override def suffixURLs: Array[String] = Array("filesystem", "resultsetToExcel")
}
