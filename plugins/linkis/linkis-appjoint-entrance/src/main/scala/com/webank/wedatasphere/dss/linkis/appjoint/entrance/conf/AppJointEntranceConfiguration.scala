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

package com.webank.wedatasphere.dss.linkis.appjoint.entrance.conf

import com.webank.wedatasphere.linkis.common.conf.CommonVars

/**
  * created by cooperyang on 2019/9/26
  * Description:
  */
object AppJointEntranceConfiguration {
  val APP_JOINT_CLASS = CommonVars("wds.linkis.external.appjoint",
    "com.webank.wedatasphere.dss.linkis.appjoint.example.ExampleAppJoint")

  val APPLICATION_SPRING_APPLICATION = CommonVars("wds.linkis.application.spring.name", "cloud-application")

  val CALLBACK_TIME_OUT:CommonVars[Long] = CommonVars[Long]("wds.linkis.callback.timeout", 5 * 60 * 1000L)


  val PUBLIC_SERVICE_SPRING_APPLICATION = CommonVars("wds.linkis.publicservice.spring.name", "cloud-publicserivce")

  val GATEWAY_SPRING_APPLICATION = CommonVars("wds.linkis.gateway.spring.name", "dataworkcloud-gateway")

  val DSS_SERVER_SPRING_APPLICATION = CommonVars("wds.linkis.dssserver.spring.name", "dss-server")
}
