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

package com.webank.wedatasphere.dss.common.utils

import com.webank.wedatasphere.linkis.common.conf.{BDPConfiguration, CommonVars, DSSConfiguration}

import scala.collection.JavaConversions._

/**
 * @author allenlliu
 * @date 2021/3/9 17:18
 */
object DSSMainHelper {

//  val DSS_DEFAULT_PROPERTY_FILE_NAME = CommonVars("wds.dss.default.property.file", "dss.properties")
//  val DSS_SERVER_DEFAULT_PROPERTY_FILE_NAME = CommonVars("wds.dss.server.default.property.file", "%s.properties")

  def formatPropertyFiles(serviceName: String): Unit = {
    sys.props.put("wds.linkis.configuration", "dss.properties")
    sys.props.put("wds.linkis.server.conf", serviceName+".properties")
  }

  //TODO wait for linkis re-written
  @Deprecated
  def addExtraPropertyFiles(filePaths: String *): Unit = {
    sys.props.put("wds.linkis.server.confs", filePaths.mkString(","))
  }

  def getExtraSpringOptions: Array[String] = {
    "--spring.profiles.active=dss" +: DSSConfiguration.getAllProperties.filter { case (k, v) => k != null && k.startsWith("spring.")}
      .map{ case (k, v) => {
        val realKey=k.substring(7)
        s"--$realKey=$v"
         }
      }.toArray
  }

}
