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

package com.webank.wedatasphere.dss.appjoint.sendemail

import java.util

import com.webank.wedatasphere.dss.appjoint.AppJoint
import com.webank.wedatasphere.dss.appjoint.execution.NodeExecution
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl

/**
  * Created by shanhuang on 2019/10/12.
  */
class SendEmailAppJoint extends AppJointUrlImpl with AppJoint {

  private var params = _
  private var nodeExecution = _

  override def getAppJointName = "SendEmail"

  override def init(baseUrl: String, params: util.Map[String, AnyRef]): Unit = {
    setBaseUrl(baseUrl)
    this.params = params
  }

  override def getNodeExecution: NodeExecution = {
    if(nodeExecution == null) synchronized {
      if(nodeExecution == null) {
        nodeExecution = new SendEmailNodeExecution
        nodeExecution.setBaseUrl(getBaseUrl)
        nodeExecution.init(params)
      }
    }
    nodeExecution
  }
}
