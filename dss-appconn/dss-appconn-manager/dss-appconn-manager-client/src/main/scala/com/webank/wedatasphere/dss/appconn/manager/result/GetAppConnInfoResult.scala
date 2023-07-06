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

package com.webank.wedatasphere.dss.appconn.manager.result

import java.util
import com.webank.wedatasphere.dss.appconn.manager.entity.{AppConnInfo, AppConnInfoImpl}
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import scala.collection.convert.wrapAsScala._
import scala.collection.convert.wrapAsJava._

@DWSHttpMessageResult("/api/rest_j/v\\d+/dss/framework/project/appconn/[a-zA-Z0-9]+/get")
class GetAppConnInfoResult extends DWSResult {

  private var appConnInfo: util.List[AppConnInfo] = _

  def setAppConnInfo(appConnInfo: util.List[util.Map[String, Object]]): Unit = {
    this.appConnInfo = appConnInfo.map{ map =>
      val json = DSSCommonUtils.COMMON_GSON.toJson(map)
      DSSCommonUtils.COMMON_GSON.fromJson(json, classOf[AppConnInfoImpl]).asInstanceOf[AppConnInfo]
    }
  }

  def getAppConnInfo: util.List[AppConnInfo] = appConnInfo

}
