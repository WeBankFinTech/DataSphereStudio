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

package com.webank.wedatasphere.dss.appjoint.visualis.execution

import java.io.{ByteArrayOutputStream, InputStream}
import java.util
import java.util.Base64

import com.webank.wedatasphere.dss.appjoint.execution.NodeExecution
import com.webank.wedatasphere.dss.appjoint.execution.common.{CompletedNodeExecutionResponse, NodeExecutionResponse}
import com.webank.wedatasphere.dss.appjoint.execution.core.{AppJointNode, CommonAppJointNode, NodeContext}
import com.webank.wedatasphere.dss.appjoint.service.session.Session
import com.webank.wedatasphere.dss.appjoint.visualis.execution.VisualisNodeExecutionConfiguration._
import com.webank.wedatasphere.linkis.common.exception.ErrorException
import com.webank.wedatasphere.linkis.common.log.LogUtils
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.storage.{LineMetaData, LineRecord}
import org.apache.commons.io.IOUtils

import scala.collection.JavaConversions.mapAsScalaMap
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import dispatch._
import org.json4s.{DefaultFormats, Formats}

/**
  * Created by enjoyyin on 2019/10/12.
  */
class VisualisNodeExecution extends NodeExecution with Logging {

  private val DISPLAY = "display"
  private val DASHBOARD = "dashboard"

  var basicUrl:String = _

  protected implicit val executors: ExecutionContext = Utils.newCachedExecutionContext(VISUALIS_THREAD_MAX.getValue, getName + "-NodeExecution-Thread", true)
  protected implicit val formats: Formats = DefaultFormats

  private implicit def svc(url: String): Req =
    dispatch.url(url)



  override def getBaseUrl: String = this.basicUrl

  override def setBaseUrl(basicUrl: String): Unit = this.basicUrl = basicUrl

  def getName:String = "visualis"

  override def canExecute(node: AppJointNode, nodeContext: NodeContext, session: Session): Boolean = node.getNodeType.toLowerCase.contains(getName.toLowerCase)

  override def execute(node: AppJointNode, nodeContext: NodeContext, session: Session): NodeExecutionResponse = node match {
    case commonAppJointNode: CommonAppJointNode =>
      val appJointResponse = new CompletedNodeExecutionResponse()
      val idMap = commonAppJointNode.getJobContent
      val id = idMap.values().iterator().next().toString
      val url = if(commonAppJointNode.getNodeType.toLowerCase.contains(DISPLAY)) getDisplayPreviewUrl(nodeContext.getGatewayUrl, id)
      else if(commonAppJointNode.getNodeType.toLowerCase.contains(DASHBOARD)) getDashboardPreviewUrl(nodeContext.getGatewayUrl, id)
      else {
        appJointResponse.setIsSucceed(false)
        appJointResponse.setErrorMsg("不支持的appJoint类型：" + node.getNodeType)
        return appJointResponse
      }
      var response = ""
      val headers = nodeContext.getTokenHeader(nodeContext.getUser)
      nodeContext.appendLog(LogUtils.generateInfo(s"Ready to download preview picture from $url."))
      Utils.tryCatch(download(url, null, headers.toMap,
        input => Utils.tryFinally{
          val os = new ByteArrayOutputStream()
          IOUtils.copy(input, os)
          response = new String(Base64.getEncoder.encode(os.toByteArray))
          //response = IOUtils.toString(input, ServerConfiguration.BDP_SERVER_ENCODING.getValue)
        }(IOUtils.closeQuietly(input)))){ t =>
        val errException = new ErrorException(70063, "failed to do visualis request")
        errException.initCause(t)
        appJointResponse.setException(errException)
        appJointResponse.setIsSucceed(false)
        appJointResponse.setErrorMsg(s"用户${nodeContext.getUser}请求Visualis失败！URL为: " + url)
        return appJointResponse
      }
      nodeContext.appendLog(LogUtils.generateInfo("Preview picture downloaded, now ready to write results."))
      val imagesBytes = response
      val resultSetWriter = nodeContext.createPictureResultSetWriter()
      Utils.tryFinally{
        resultSetWriter.addMetaData(new LineMetaData())
        resultSetWriter.addRecord(new LineRecord(imagesBytes))
      }(IOUtils.closeQuietly(resultSetWriter))
      appJointResponse.setIsSucceed(true)
      appJointResponse
  }

  def download(url: String, queryParams: Map[String, String], headerParams: Map[String, String],
               write: InputStream => Unit,
               paths: String*): Unit = {
    var req = url.GET
    if(headerParams != null && headerParams.nonEmpty) req = req <:< headerParams
    if(queryParams != null) queryParams.foreach{ case (k, v) => req = req.addQueryParameter(k, v)}
    if(paths != null) paths.filter(_ != null).foreach(p => req = req / p)
    val response = Http(req OK as.Response(_.getResponseBodyAsStream)).map(write)
    Await.result(response,  Duration.Inf)
  }

  private def getRealId(displayId:String):Int = {
    Utils.tryCatch{
      val f = java.lang.Float.parseFloat(displayId)
      f.asInstanceOf[Int]
    }{
      _ => 0
    }
  }

  def getPreviewUrl(gatewayUrl: String, uri: String, displayId: String): String = {
    val realId = getRealId(displayId)
    val realGatewayUrl = "http://" + gatewayUrl
    if(!realGatewayUrl.endsWith("/") && !uri.startsWith("/"))
      realGatewayUrl + "/" + String.format(uri, realId.toString)
    else if(realGatewayUrl.endsWith("/") && uri.startsWith("/"))
      realGatewayUrl.substring(0, realGatewayUrl.length - 1) + String.format(uri, realId.toString)
    else realGatewayUrl + String.format(uri, realId.toString)
  }

  def getDisplayPreviewUrl(gatewayUrl: String, displayId: String): String = getPreviewUrl(gatewayUrl, DISPLAY_PREVIEW_URL_FORMAT.getValue, displayId)

  def getDashboardPreviewUrl(gatewayUrl: String, dashboardId: String): String = getPreviewUrl(gatewayUrl, DASHBOARD_PREVIEW_URL_FORMAT.getValue, dashboardId)

  override def init(map: util.Map[String, AnyRef]): Unit = {}
}
