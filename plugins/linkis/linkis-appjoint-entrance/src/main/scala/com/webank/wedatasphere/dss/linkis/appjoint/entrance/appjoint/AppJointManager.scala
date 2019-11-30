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

package com.webank.wedatasphere.dss.linkis.appjoint.entrance.appjoint

import java.util
import java.util.concurrent.TimeUnit

import com.google.common.cache.CacheBuilder
import com.webank.wedatasphere.dss.appjoint.AppJoint
import com.webank.wedatasphere.dss.appjoint.execution.scheduler.ListenerEventBusNodeExecutionScheduler
import com.webank.wedatasphere.dss.appjoint.loader.AppJointLoader
import com.webank.wedatasphere.dss.common.protocol.RequestDSSApplication
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.conf.AppJointEntranceConfiguration
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.rpc.Sender

/**
  * Created by enjoyyin on 2019/11/13.
  */
object AppJointManager extends Logging{

  private val scheduler = new ListenerEventBusNodeExecutionScheduler

  scheduler.start()


  private val sender = Sender.getSender(AppJointEntranceConfiguration.DSS_SERVER_SPRING_APPLICATION.getValue)

  private val urlStr:String = "url"

  def getScheduler:ListenerEventBusNodeExecutionScheduler = scheduler


  /**
    * 通过appJointName获取一个可以使用的AppJoint
    * @param appJointName appjoint的名字,例如 sendemail,qualitis
    * @return
    */


  def getAppJoint(appJointName: String): AppJoint = {
    val request = RequestDSSApplication(appJointName)
    val result = Utils.tryCatch{
      sender.ask(request)
    }{
      t:Throwable => logger.warn(s"can not get application $appJointName from dss-Server", t)
        null
    }
    var params:java.util.Map[String, Object] = new util.HashMap[String, Object]()
    val baseUrl:String = if (result != null){
      result match {
        case map:java.util.Map[String, Object] => params = map
          if (null != map.get(urlStr)) map.get(urlStr).toString else ""
        case _ => ""
      }
    }else ""
    AppJointLoader.getAppJointLoader.getAppJoint("", appJointName, params)
  }

}
