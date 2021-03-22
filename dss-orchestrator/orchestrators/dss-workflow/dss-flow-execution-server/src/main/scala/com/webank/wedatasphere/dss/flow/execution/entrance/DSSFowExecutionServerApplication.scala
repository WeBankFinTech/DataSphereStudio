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

package com.webank.wedatasphere.dss.flow.execution.entrance

import com.webank.wedatasphere.dss.common.utils.DSSMainHelper
import com.webank.wedatasphere.linkis.DataWorkCloudApplication
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}


object DSSFowExecutionServerApplication extends Logging {

  val userName: String = System.getProperty("user.name")
  val hostName: String = Utils.getComputerName

  def main(args: Array[String]): Unit = {
    val serviceName = System.getProperty("serviceName")//ProjectConf.SERVICE_NAME.getValue
    DSSMainHelper.formatPropertyFiles(serviceName)
    val allArgs = args ++ DSSMainHelper.getExtraSpringOptions
    System.setProperty("hostName", hostName)
    System.setProperty("userName", userName)
    info(s"Ready to start $serviceName with args: ${allArgs.toList}.")
    println(s"Test Ready to start $serviceName with args: ${allArgs.toList}.")
    DataWorkCloudApplication.main(allArgs)
  }
}