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

package com.webank.wedatasphere.dss.datapipe.entrance.background

import java.util

import com.google.gson.{JsonObject, JsonParser}
import com.webank.wedatasphere.linkis.server._
import com.webank.wedatasphere.linkis.server.socket.controller.ServerEvent


class LoadBackGroundService extends AbstractBackGroundService {

  override val serviceType: String = "load"

  override def operation(serverEvent: ServerEvent): ServerEvent = {
    val params = serverEvent.getData.map { case (k, v) => k -> v.asInstanceOf[Any] }
    val executionCode = BackGroundServiceUtils.gson.toJson(params.get("executionCode").get)
    // TODO: Head may be removed
    var newExecutionCode = ""
    val jsonParser = new JsonParser()
    val jsonCode = jsonParser.parse(executionCode.asInstanceOf[String]).asInstanceOf[JsonObject]
    val source = "val source = \"\"\"" + jsonCode.get("source").toString + "\"\"\"\n"
    val destination = jsonCode.get("destination").toString
    var newDestination = "val destination = \"\"\""
    val storePath = BackGroundServiceUtils.storeExecutionCode(destination,serverEvent.getUser)
    if(storePath == null) newDestination +=destination +"\"\"\"\n" else newDestination +=storePath + "\"\"\"\n"
    newExecutionCode += source
    newExecutionCode += newDestination
    if(storePath == null){
      newExecutionCode += "com.webank.wedatasphere.linkis.engineplugin.spark.imexport.LoadData.loadDataToTable(spark,source,destination)"
    }else{
      newExecutionCode += "com.webank.wedatasphere.linkis.engineplugin.spark.imexport.LoadData.loadDataToTableByFile(spark,destination,source)"
    }

    params.put("executionCode", newExecutionCode)
    print(newExecutionCode)
    val map = new util.HashMap[String, Object]()
    params.foreach(f => map.put(f._1, f._2.asInstanceOf[Object]))
    serverEvent.setData(map)
    serverEvent
  }

  def splitDestination(destination:String):util.ArrayList[String] = {
    val length = 6000
    val list = new util.ArrayList[String]()
    var size = destination.length /length
    if(destination.length % length != 0) size += 1
    for(i <- 0 to size-1){
      list.add(subString(destination,i * length,(i +1) * length))
    }
    list
  }

  private def subString(str:String,begin:Int,end:Int):String = {
    if(begin > str.length) return null
    if(end > str.length) return str.substring(begin,str.length)
    str.substring(begin,end)
  }
}
