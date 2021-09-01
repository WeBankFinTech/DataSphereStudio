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

package com.webank.wedatasphere.linkis.manager.engineplugin.appconn.executor

import java.util
import java.util.Map

import com.webank.wedatasphere.dss.appconn.core.AppConn
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager
import com.webank.wedatasphere.dss.common.label.{EnvDSSLabel, LabelKeyConvertor}
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.standard.app.development.listener.common.{AsyncExecutionRequestRef, CompletedExecutionResponseRef}
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.sso.Workspace
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance
import com.webank.wedatasphere.dss.standard.common.entity.ref.{AsyncResponseRef, DefaultRefFactory, ResponseRef}
import com.webank.wedatasphere.linkis.common.utils.{OverloadUtils, Utils}
import com.webank.wedatasphere.linkis.engineconn.computation.executor.execute.{ComputationExecutor, EngineExecutionContext}
import com.webank.wedatasphere.linkis.engineconn.launch.EngineConnServer
import com.webank.wedatasphere.linkis.governance.common.utils.GovernanceConstant
import com.webank.wedatasphere.linkis.manager.common.entity.resource.{CommonNodeResource, LoadResource, NodeResource}
import com.webank.wedatasphere.linkis.manager.engineplugin.appconn.exception.AppConnExecuteFailedException
import com.webank.wedatasphere.linkis.manager.engineplugin.appconn.executor.AppConnEngineConnExecutor._
import com.webank.wedatasphere.linkis.manager.engineplugin.common.conf.EngineConnPluginConf
import com.webank.wedatasphere.linkis.manager.label.entity.Label
import com.webank.wedatasphere.linkis.manager.label.entity.cluster.ClusterLabel
import com.webank.wedatasphere.linkis.manager.label.entity.engine.EngineTypeLabel
import com.webank.wedatasphere.linkis.protocol.UserWithCreator
import com.webank.wedatasphere.linkis.protocol.engine.JobProgressInfo
import com.webank.wedatasphere.linkis.scheduler.executer.{ErrorExecuteResponse, ExecuteResponse, SuccessExecuteResponse}
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper
import org.apache.commons.lang.StringUtils

import scala.beans.BeanProperty

class AppConnEngineConnExecutor(val id: Int) extends ComputationExecutor {

  @BeanProperty
  var userWithCreator: UserWithCreator = _

  private val executorLabels: util.List[Label[_]] = new util.ArrayList[Label[_]](2)

  private val refFactory = new DefaultRefFactory

  override def executeLine(engineExecutorContext: EngineExecutionContext, code: String): ExecuteResponse = {
    info("got code:[" + code + "]")
    info("got params:[" + BDPJettyServerHelper.gson.toJson(engineExecutorContext.getProperties) + "]")
    val source = engineExecutorContext.getProperties.get(GovernanceConstant.TASK_SOURCE_MAP_KEY) match {
      case map: util.Map[String, Object] => map
      case _ => return ErrorExecuteResponse("Cannot find source.", null)
    }
    def getValue(map: util.Map[String, AnyRef], key: String): String = map.get(key) match {
      case string: String => string
      case anyRef: AnyRef => BDPJettyServerHelper.gson.toJson(anyRef)
      case _ => null
    }
    val requestRef = refFactory.newRef(classOf[ExecutionRequestRef], getClass.getClassLoader, "com.webank.wedatasphere") match {
      case ref: AsyncExecutionRequestRef =>
        ref.setProjectName(getValue(source, PROJECT_NAME_STR))
        ref.setOrchestratorName(getValue(source, FLOW_NAME_STR))
        ref.setJobContent(BDPJettyServerHelper.gson.fromJson(code, classOf[util.HashMap[String, AnyRef]]))
        ref.setName(getValue(source, NODE_NAME_STR))
        engineExecutorContext.getLabels.find(_.isInstanceOf[EngineTypeLabel]).foreach { case engineTypeLabel: EngineTypeLabel =>
          ref.setType(engineTypeLabel.getEngineType)
        }
        ref.setExecutionRequestRefContext(new ExecutionRequestRefContextImpl(engineExecutorContext, userWithCreator))
        ref
      case ref: ExecutionRequestRef => ref
    }
    val workspace = BDPJettyServerHelper.gson.fromJson(getValue(engineExecutorContext.getProperties, WORKSPACE_NAME_STR), classOf[Workspace])
    requestRef.setWorkspace(workspace)
    val appConnName = getAppConnName(getValue(engineExecutorContext.getProperties, NODE_TYPE))
    val appConn = AppConnManager.getAppConnManager.getAppConn(appConnName)
    //val labels = getDSSLabels(engineExecutorContext.getLabels)
    var labels = engineExecutorContext.getProperties.get("labels").toString
    getAppInstanceByLabels(labels, appConn) match {
      case Some(appInstance) =>
        val developmentIntegrationStandard = appConn.asInstanceOf[OnlyDevelopmentAppConn].getOrCreateDevelopmentStandard
        val refExecutionService =developmentIntegrationStandard.getRefExecutionService(appInstance)
        val refExecutionOperation  = refExecutionService.getRefExecutionOperation
        Utils.tryCatch(refExecutionOperation.execute(requestRef) match {
          case asyncResponseRef: AsyncResponseRef =>
            asyncResponseRef.waitForCompleted()
            createExecuteResponse(asyncResponseRef.getResponse, appConnName)
          case responseRef: ResponseRef =>
            createExecuteResponse(responseRef, appConnName)
        })(ErrorExecuteResponse("Failed to execute appconn", _))
      case None =>
        throw AppConnExecuteFailedException(510000, "Cannot Find AppInstance by labels " + labels)
    }
  }

  private def getDSSLabels(labels: Array[Label[_]]): String =
    labels.find(_.isInstanceOf[ClusterLabel]).map {case clusterLabel: ClusterLabel => clusterLabel.getClusterName }
      .getOrElse(throw AppConnExecuteFailedException(510000, "Cannot Find AppInstance by labels " + labels.toList))

  private def createExecuteResponse(responseRef: ResponseRef, appConnName: String): ExecuteResponse =
    if(responseRef.isSucceed) SuccessExecuteResponse()
    else {
      val exception = responseRef match {
        case response: CompletedExecutionResponseRef => response.getException
        case _ => null
      }
      error(s"$appConnName execute failed, failed reason is ${responseRef.getErrorMsg}.", exception)
      ErrorExecuteResponse(responseRef.getErrorMsg, exception)
    }

  private def getAppConnName(nodeType: String) = {
    StringUtils.split(nodeType,".")(0)
  }

  private def getAppInstanceByLabels(labels: String, appConn: AppConn): Option[AppInstance] = {
    var labelStr = labels
    if (labels.contains(LabelKeyConvertor.ROUTE_LABEL_KEY)) {
      val labelMap = DSSCommonUtils.COMMON_GSON.fromJson(labels, classOf[util.Map[_, _]])
      labelStr = labelMap.get(LabelKeyConvertor.ROUTE_LABEL_KEY).asInstanceOf[String]
    }
    val appInstanceList = appConn.getAppDesc.getAppInstancesByLabels(util.Arrays.asList(new EnvDSSLabel(labelStr)));
    if (appInstanceList != null && appInstanceList.size() > 0) {
      return Some(appInstanceList.get(0))
    }
    None
  }

  override def executeCompletely(engineExecutorContext: EngineExecutionContext, code: String, completedLine: String): ExecuteResponse = null

  override def progress(): Float = 0

  override def getProgressInfo: Array[JobProgressInfo] = Array.empty

  override def supportCallBackLogs(): Boolean = false

  override def getExecutorLabels(): util.List[Label[_]] = executorLabels

  override def setExecutorLabels(labels: util.List[Label[_]]): Unit = {
    if (null != labels && !labels.isEmpty) {
      executorLabels.clear()
      executorLabels.addAll(labels)
    }
  }

  override def requestExpectedResource(expectedResource: NodeResource): NodeResource = null

  override def getCurrentNodeResource(): NodeResource = {
    val properties = EngineConnServer.getEngineCreationContext.getOptions
    if (properties.containsKey(EngineConnPluginConf.JAVA_ENGINE_REQUEST_MEMORY.key)) {
      val settingClientMemory = properties.get(EngineConnPluginConf.JAVA_ENGINE_REQUEST_MEMORY.key)
      if (!settingClientMemory.toLowerCase().endsWith("g")) {
        properties.put(EngineConnPluginConf.JAVA_ENGINE_REQUEST_MEMORY.key, settingClientMemory + "g")
      }
    }
    val resource = new CommonNodeResource
    val usedResource = new LoadResource(OverloadUtils.getProcessMaxMemory, 1)
    resource.setUsedResource(usedResource)
    resource
  }

  override def getId(): String = "AppConnEngineExecutor_" + id

}
object AppConnEngineConnExecutor {

  private val WORKSPACE_NAME_STR = "workspace"

  private val PROJECT_NAME_STR = "projectName"

  private val FLOW_NAME_STR = "flowName"

  private val NODE_NAME_STR = "nodeName"

  private val NODE_TYPE = "nodeType"

}