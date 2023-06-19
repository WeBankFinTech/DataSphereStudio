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

package com.webank.wedatasphere.dss.common.service

import com.webank.wedatasphere.dss.common.entity.BmlResource
import com.webank.wedatasphere.dss.common.exception.DSSErrorException
import com.webank.wedatasphere.dss.common.utils.IoUtils
import org.apache.commons.io.IOUtils
import org.apache.linkis.bml.client.{BmlClient, BmlClientFactory}
import org.apache.linkis.bml.protocol.{BmlDownloadResponse, BmlUpdateResponse, BmlUploadResponse}
import org.apache.linkis.common.utils.{JavaLog, Utils}

import java.io.{ByteArrayInputStream, InputStream}
import java.util
import java.util.UUID
import scala.collection.JavaConversions._

class BMLService extends JavaLog {

  @volatile var bmlClient: BmlClient = _

  /**
   * 单例，获取bml client
   * @param userName
   * @return
   */
  def getBmlClient(userName: String): BmlClient = {
    if (bmlClient == null) synchronized {
      if (bmlClient == null) {
        bmlClient = BmlClientFactory.createBmlClient(userName)
      }
    }
    bmlClient
  }

  /**
   * 上传一段文本
   * @param content 要上传的文本内容
   * @param fileName 指定生成的文件名
   * @param projectName 指定项目名
   * @return 返回BmlResource
   */
  def upload(userName: String, content: String, fileName: String, projectName: String): util.Map[String, Object] = {
    val inputStream = new ByteArrayInputStream(content.getBytes("utf-8"))
    val client: BmlClient = getBmlClient(userName)
    val resource: BmlUploadResponse = client.uploadShareResource(userName, projectName, fileName, inputStream)
    if (!resource.isSuccess) throw new DSSErrorException(911113, "上传失败")
    val map = new util.HashMap[String, Object]
    map += "resourceId" -> resource.resourceId
    map += "version" -> resource.version
  }

  /**
   * 上传二进制流
   *
   * @param inputStream 要上传的二进制流
   * @param fileName    指定生成的文件名
   * @param projectName 指定项目名
   * @return 上传后的结果BmlResource
   */
  def upload(userName: String, inputStream: InputStream, fileName: String, projectName: String): BmlResource = {
    val client: BmlClient = getBmlClient(userName)
    val resource: BmlUploadResponse = client.uploadShareResource(userName, projectName, fileName, inputStream)
    if (!resource.isSuccess) throw new DSSErrorException(911113, "上传失败")
    new BmlResource(resource.resourceId, resource.version)
  }

  /**
   * 将特定资源更新为指定二进制流
   * @param resourceId 要更新的资源
   * @param inputStream 二进制流
   * @return 更新后的结果BmlResource
   */
  def update(userName: String, resourceId: String, inputStream: InputStream): util.Map[String, Object] = {
    val client: BmlClient = getBmlClient(userName)
    val resource: BmlUpdateResponse = client.updateShareResource(userName, resourceId, "", inputStream)
    if (!resource.isSuccess) throw new DSSErrorException(911114, "更新失败")
    val map = new util.HashMap[String, Object]
    map += "resourceId" -> resource.resourceId
    map += "version" -> resource.version
  }

  /**
   * 将特定资源更新为指定文本
   *
   * @param resourceId  要更新的资源
   * @param inputStream 文本内容
   * @return 更新后的结果BmlResource
   */
  def update(userName: String, resourceId: String, content: String): util.Map[String, Object] = {
    val inputStream = new ByteArrayInputStream(content.getBytes("utf-8"))
    val client: BmlClient = getBmlClient(userName)
    val resource: BmlUpdateResponse = client.updateShareResource(userName, resourceId, UUID.randomUUID().toString + ".json", inputStream)
    if (!resource.isSuccess) throw new DSSErrorException(911114, "更新失败")
    val map = new util.HashMap[String, Object]
    map += "resourceId" -> resource.resourceId
    map += "version" -> resource.version
  }

  /**
   * 读取一个resource并转为文本返回。
   */
  def query(userName: String, resourceId: String, version: String): util.Map[String, Object] = {
    val client: BmlClient = getBmlClient(userName)
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

  /**
   * 下载一个资源
   * @return 资源的路径和输入流
   */
  def download(userName: String, resourceId: String, version: String): util.Map[String, Object] = {
    val client: BmlClient = getBmlClient(userName)
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

  /**
   * 把资源下载到指定的本地文件，返
   * @param path 本地文件全路径名
   */
  def downloadToLocalPath(userName: String, resourceId: String, version: String, path: String): String = {
    val result = download(userName, resourceId, version)
    val is = result.get("is").asInstanceOf[InputStream]
    val os = IoUtils.generateExportOutputStream(path)
    Utils.tryFinally(IOUtils.copy(is, os)) {
      IOUtils.closeQuietly(os)
      IOUtils.closeQuietly(is)
    }
    path
  }

  def downloadAndGetText(userName: String, resourceId: String, version: String, path: String): String = {
    downloadToLocalPath(userName, resourceId, version, path)
    //因为下载到指定目录后返回的resource的Stream为null,只能从文件重新读取。
    val is = IoUtils.generateInputInputStream(path)
    Utils.tryFinally(inputstremToString(is))(IOUtils.closeQuietly(is))
  }

  /**
   * 读取本地文件，返回二进制输入流
   *
   * @param userName 用户名
   * @param readPath 本地文件全路径名
   * @return 二进制输入流
   */
  def readLocalResourceFile(userName: String, readPath: String): InputStream = {
    IoUtils.generateInputInputStream(readPath)
  }

  /**
   * 读取本地指定文件名的一段文本
   * @param readPath 本地文件全路径名
   * @return 文件里的文本内容
   */
  def readLocalTextFile(userName: String, readPath: String): String = {
    var inputStream: InputStream = null
    var text: String = null
    Utils.tryFinally {
      inputStream = IoUtils.generateInputInputStream(readPath)
      text = inputstremToString(inputStream)
    } {
      IOUtils.closeQuietly(inputStream)
    }
    text
  }


  def readTextFromBML(userName: String, resourceId: String, version: String): String = {

    val result = download(userName, resourceId, version)
    val is = result.get("is").asInstanceOf[InputStream]

    var inputStream: InputStream = null
    var text: String = null
    Utils.tryFinally {
      inputStream = is
      text = inputstremToString(inputStream)
    } {
      IOUtils.closeQuietly(inputStream)
    }
    text
  }


  private def inputstremToString(inputStream: InputStream): String = {
    scala.io.Source.fromInputStream(inputStream).mkString
  }


  /**
   * 创建BML工程
   * @param username 用户名
   * @param projectName 工程名
   * @param editUsers 编辑者
   * @param accessUsers 访问者
   */
  def createBmlProject(username:String, projectName:String, editUsers:util.List[String],
                       accessUsers:util.List[String] ): Unit ={
    val client = getBmlClient(username)
    val response = client.createBmlProject(username, projectName, accessUsers, editUsers)
    if (response.isSuccess) {
      logger.info(s"for user $username create bml project $projectName success")
    } else {
      logger.error(s"for user $username create bml project $projectName failed")
    }
  }

  def attachResourceAndProject(username: String, projectName: String, resourceId: String): Unit = {
    val client = getBmlClient(username)
    val response = client.attachResourceAndProject(projectName, resourceId)
    if (response.isSuccess) {
      logger.info(s"attach $username and $projectName success")
    } else {
      logger.error(s"attach $username and $projectName failed")
    }
  }

  /**
   * 修改BML工程中的编辑者、访问者等权限
   * @param projectName 工程名
   * @param username 操作用户名
   * @param editUsers 新的编辑者名单
   * @param accessUsers 新的访问者名单
   */
  def updateProjectPriv(projectName: String, username: String, editUsers: util.List[String],
                        accessUsers: util.List[String]): Unit = {
    val client = getBmlClient(username)
    val response = client.updateProjectPriv(username, projectName, editUsers, accessUsers)
    if (response.isSuccess) {
      logger.info(s"attach $username and $projectName success")
    } else {
      logger.error(s"attach $username and $projectName failed")
    }
  }

  def deleteBmlResource(username: String, resourceId: String): Unit = {
    val client = getBmlClient(username)
    val response = client.deleteResource(username, resourceId)
    if (response.isSuccess) {
      logger.info(s"delete $username bml  resource file success, resourceId: $resourceId")
    } else {
      logger.error(s"delete $username bml resource file failed, resourceId: $resourceId")
    }
  }

}

object BMLService {
  @volatile var bmlService: BMLService = _

  def getInstance(): BMLService = {
    if (bmlService == null) synchronized {
      if (bmlService == null) {
        bmlService = new BMLService()
      }
    }
    bmlService
  }

}