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

package com.webank.wedatasphere.dss.orchestrator.core.service

import java.io.{ByteArrayInputStream, InputStream}
import java.util
import java.util.UUID

import com.webank.wedatasphere.dss.common.exception.DSSErrorException
import com.webank.wedatasphere.dss.common.utils.IoUtils
import com.webank.wedatasphere.linkis.bml.client.{BmlClient, BmlClientFactory}
import com.webank.wedatasphere.linkis.bml.protocol.{BmlDownloadResponse, BmlUpdateResponse, BmlUploadResponse}
import com.webank.wedatasphere.linkis.common.utils.{JavaLog, Utils}
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._

/**
  * Created by v_wbjftang on 2019/5/29.
  */
@Component
class BMLService extends JavaLog{

  def upload(userName: String, content: String, fileName: String, projectName:String): util.Map[String, Object] = {
    val inputStream = new ByteArrayInputStream(content.getBytes("utf-8"))
    val client: BmlClient = createBMLClient(userName)
    val resource: BmlUploadResponse = client.uploadShareResource(userName, projectName, fileName, inputStream)
    if (!resource.isSuccess) throw new DSSErrorException(911113, "上传失败")
    val map = new util.HashMap[String, Object]
    map += "resourceId" -> resource.resourceId
    map += "version" -> resource.version
  }

  def upload(userName: String, inputStream: InputStream, fileName: String, projectName:String): util.Map[String, Object] = {
    val client: BmlClient = createBMLClient(userName)
    val resource: BmlUploadResponse = client.uploadShareResource(userName, projectName, fileName, inputStream)
    if (!resource.isSuccess) throw new DSSErrorException(911113, "上传失败")
    val map = new util.HashMap[String, Object]
    map += "resourceId" -> resource.resourceId
    map += "version" -> resource.version
  }

  def update(userName: String, resourceId: String, inputStream: InputStream): util.Map[String, Object] = {
    val client: BmlClient = createBMLClient(userName)
    val resource: BmlUpdateResponse = client.updateShareResource(userName, resourceId, "", inputStream)
    if (!resource.isSuccess) throw new DSSErrorException(911114, "更新失败")
    val map = new util.HashMap[String, Object]
    map += "resourceId" -> resource.resourceId
    map += "version" -> resource.version
  }

  def update(userName: String, resourceId: String, content: String): util.Map[String, Object] = {
    val inputStream = new ByteArrayInputStream(content.getBytes("utf-8"))
    val client: BmlClient = createBMLClient(userName)
    val resource: BmlUpdateResponse = client.updateShareResource(userName, resourceId, UUID.randomUUID().toString+".json", inputStream)
    if (!resource.isSuccess) throw new DSSErrorException(911114, "更新失败")
    val map = new util.HashMap[String, Object]
    map += "resourceId" -> resource.resourceId
    map += "version" -> resource.version
  }

  def query(userName: String, resourceId: String, version: String): util.Map[String, Object] = {
    val client: BmlClient = createBMLClient(userName)
    var resource: BmlDownloadResponse = null
    if (version == null) {
      resource = client.downloadShareResource(userName, resourceId)
    } else {
      resource = client.downloadShareResource(userName, resourceId, version)
    }
    if (!resource.isSuccess) throw new DSSErrorException(911115, "下载失败")
    val map = new util.HashMap[String, Object]
    map += "path" -> resource.fullFilePath
    map += "string" -> inputstremToString(resource.inputStream)
  }

  def download(userName: String, resourceId: String, version: String): util.Map[String, Object] = {
    val client: BmlClient = createBMLClient(userName)
    var resource: BmlDownloadResponse = null
    if (version == null) {
      resource = client.downloadShareResource(userName, resourceId)
    } else {
      resource = client.downloadShareResource(userName, resourceId, version)
    }
    if (!resource.isSuccess) throw new DSSErrorException(911115, "下载失败")
    val map = new util.HashMap[String, Object]
    map += "path" -> resource.fullFilePath
    map += "is" -> resource.inputStream
  }

  def downloadToLocalPath(userName: String, resourceId: String, version: String, path: String): String = {
    val result = download(userName,resourceId,version)
    val is = result.get("is").asInstanceOf[InputStream]
    val os = IoUtils.generateExportOutputStream(path)
    Utils.tryFinally(IOUtils.copy(is,os)){
      IOUtils.closeQuietly(os)
      IOUtils.closeQuietly(is)
    }
    path
  }

  def downloadAndGetFlowJson(userName: String, resourceId: String, version: String, path: String): String = {
    downloadToLocalPath(userName,resourceId,version,path)
    //因为下载到指定目录后返回的resource的Stream为null,只能从文件重新读取。
    val is = IoUtils.generateInputInputStream(path)
    Utils.tryFinally(inputstremToString(is))(IOUtils.closeQuietly(is))
  }

  def readLocalResourceFile(userName: String,readPath: String): InputStream ={
    IoUtils.generateInputInputStream(readPath)
  }

  def readLocalFlowJsonFile(userName: String,readPath: String): String ={
    var inputStream:InputStream = null
    var flowJson:String = null
    Utils.tryFinally{
      inputStream = IoUtils.generateInputInputStream(readPath)
      flowJson = inputstremToString(inputStream)
    }{
      IOUtils.closeQuietly(inputStream)
    }
    flowJson
  }

  private def inputstremToString(inputStream: InputStream): String = {
    scala.io.Source.fromInputStream(inputStream).mkString
  }

  private def createBMLClient(userName: String): BmlClient = {
    if (userName == null)
      BmlClientFactory.createBmlClient()
    else
      BmlClientFactory.createBmlClient(userName)
  }

  def createBmlProject(username:String, projectName:String, editUsers:util.List[String],
                       accessUsers:util.List[String] ): Unit ={
    val client = createBMLClient(username)
    val response =  client.createBmlProject(username, projectName, accessUsers, editUsers)
    if (response.isSuccess){
      logger.info(s"for user $username create bml project $projectName success")
    }else{
      logger.error(s"for user $username create bml project $projectName failed")
    }
  }

  def attachResourceAndProject(username:String, projectName:String, resourceId:String):Unit = {
    val client = createBMLClient(username)
    val response = client.attachResourceAndProject(projectName, resourceId)
    if (response.isSuccess){
      logger.info(s"attach $username and $projectName success")
    }else{
      logger.error(s"attach $username and $projectName failed")
    }
  }


  def updateProjectPriv(projectName:String, username:String, editUsers:util.List[String],
                        accessUsers:util.List[String]): Unit ={
    val client = createBMLClient(username)
    val response = client.updateProjectPriv(username, projectName, editUsers, accessUsers)
    if (response.isSuccess){
      logger.info(s"attach $username and $projectName success")
    }else{
      logger.error(s"attach $username and $projectName failed")
    }
  }



}
