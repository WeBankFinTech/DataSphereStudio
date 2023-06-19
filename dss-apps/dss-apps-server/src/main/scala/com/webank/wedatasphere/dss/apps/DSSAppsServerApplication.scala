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

package com.webank.wedatasphere.dss.apps

import com.webank.wedatasphere.dss.common.utils.DSSMainHelper
import org.apache.linkis.DataWorkCloudApplication
import org.apache.linkis.common.conf.DSSConfiguration
import org.apache.linkis.common.utils.{Logging, Utils}

import java.io.File

object DSSAppsServerApplication extends Logging {

  val userName: String = System.getProperty("user.name")
  val hostName: String = Utils.getComputerName

  val legacyServerConfigs: Array[String] = Array("dss-apiservice-server.properties", "dss-datapipe-server.properties",
    "dss-guide-server.properties", "dss-scriptis-server.properties")

  def main(args: Array[String]): Unit = {
    val serviceName = System.getProperty("serviceName") //ProjectConf.SERVICE_NAME.getValue
    DSSMainHelper.formatPropertyFiles(serviceName)
    // 若dss-apps-server配置不存在，则加载先前配置
    val serverConfFileURL = getClass.getClassLoader.getResource(serviceName + ".properties")
    if (serverConfFileURL == null || !new File(serverConfFileURL.getPath).exists) {
      logger.info(s"$serviceName.properties is not exists, now try to load legacy configs.")
      DSSConfiguration.addLegacyConfiguration(legacyServerConfigs)
      // server.port使用dss-scriptis-server.properties设置的值
      DSSConfiguration.setSpringApplicationName("dss-apps-server")
    }
    val allArgs = args ++ DSSMainHelper.getExtraSpringOptions
    System.setProperty("hostName", hostName)
    System.setProperty("userName", userName)
    info(s"Ready to start $serviceName with args: ${allArgs.toList}.")
    println(s"Test Ready to start $serviceName with args: ${allArgs.toList}.")

    DataWorkCloudApplication.main(allArgs)
  }
}