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

package com.webank.wedatasphere.dss.scriptis.entrance.background

import java.util

import com.google.gson.internal.LinkedTreeMap
import com.google.gson.{JsonObject, JsonParser}
import org.apache.linkis.common.utils.Logging
import org.apache.linkis.server._
import org.apache.linkis.server.socket.controller.ServerEvent


class ExportBackGroundService extends AbstractBackGroundService with Logging{
  override val serviceType: String = "export"

  override def operation(serverEvent: ServerEvent): ServerEvent = {
    val params = serverEvent.getData.map { case (k, v) => k -> v.asInstanceOf[Any] }
    //val executionCode = params.get("executionCode").get
    val ec = params.get("executionCode").get.asInstanceOf[LinkedTreeMap[String,LinkedTreeMap[String,String]]]
    if(ec.get("destination")!=null && ec.get("destination").get("fieldDelimiter") != null){
      info(s"---${ec.get("destination").get("fieldDelimiter")}---")
      ec.get("destination").get("fieldDelimiter") match {
        case "\\t" =>ec.get("destination").put("fieldDelimiter","\t")
        case _ =>info("---other fieldDelimiter---")
      }
    }
    val executionCode = BackGroundServiceUtils.gson.toJson(params.get("executionCode").get)
    // TODO: Head may be removed
    var newExecutionCode = ""
    val jsonParser = new JsonParser()
    val jsonCode = jsonParser.parse(executionCode.asInstanceOf[String]).asInstanceOf[JsonObject]
    val destination = "val destination = \"\"\"" + jsonCode.get("destination").toString + "\"\"\"\n"
    val dataInfo = jsonCode.get("dataInfo").toString
    var newDataInfo = "val dataInfo = \"\"\""
    val storePath = BackGroundServiceUtils.storeExecutionCode(dataInfo,serverEvent.getUser)
    if(storePath == null) newDataInfo += dataInfo + "\"\"\"\n" else newDataInfo += storePath + "\"\"\"\n"
    newExecutionCode += destination
    newExecutionCode += newDataInfo
    if(storePath == null)
      newExecutionCode += "org.apache.linkis.engineplugin.spark.imexport.ExportData.exportData(spark,dataInfo,destination)"
    else
      newExecutionCode += "org.apache.linkis.engineplugin.spark.imexport.ExportData.exportDataByFile(spark,dataInfo,destination)"
    params.put("executionCode", newExecutionCode)
    print(newExecutionCode)
    val map = new util.HashMap[String, Object]()
    params.foreach(f => map.put(f._1, f._2.asInstanceOf[Object]))
    serverEvent.setData(map)
    serverEvent
  }

   def splitDataInfo(dataInfo:String):util.ArrayList[String] = {
    val length = 6000
    val list = new util.ArrayList[String]()
    var size = dataInfo.length /length
    if(dataInfo.length % length != 0) size += 1
    for(i <- 0 to size-1){
      list.add(subString(dataInfo,i * length,(i +1) * length))
    }
    list
  }

  private def subString(str:String,begin:Int,end:Int):String = {
    if(begin > str.length) return null
    if(end > str.length) return str.substring(begin,str.length)
    str.substring(begin,end)
  }


}

object A{
  def main(args: Array[String]): Unit = {
    val builder: StringBuilder = new StringBuilder
    for(i <- 1 to 200){
      if(i == 1) builder.append(2) else builder.append(1)

    }
    var newDataInfo = "val dataInfo = \"\"\""
    val service: ExportBackGroundService = new ExportBackGroundService()
    val splitDataInfos: util.ArrayList[String] =service.splitDataInfo(builder.toString())
    val splitString = "\"\"\"" + "+" + "\"\"\""
    val compaction = splitDataInfos.foldLeft("")((l,r) =>  l + splitString + r )
    newDataInfo += compaction.substring(splitString.length,compaction.length) + "\"\"\"\n"
    print(newDataInfo)
  }
}
