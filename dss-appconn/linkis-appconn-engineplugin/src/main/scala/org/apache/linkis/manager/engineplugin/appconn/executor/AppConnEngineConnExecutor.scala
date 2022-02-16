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

package org.apache.linkis.manager.engineplugin.appconn.executor

import java.util
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.webank.wedatasphere.dss.appconn.core.AppConn
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager
import com.webank.wedatasphere.dss.common.label.{EnvDSSLabel, LabelKeyConvertor}
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.standard.app.development.listener.common.{AbstractRefExecutionAction, AsyncExecutionRequestRef, AsyncExecutionResponseRef, CompletedExecutionResponseRef}
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Killable
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.sso.Workspace
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance
import com.webank.wedatasphere.dss.standard.common.entity.ref.{AsyncResponseRef, DefaultRefFactory, ResponseRef}
import org.apache.linkis.common.utils.{OverloadUtils, Utils}
import org.apache.linkis.engineconn.computation.executor.async.AsyncConcurrentComputationExecutor
import org.apache.linkis.engineconn.computation.executor.execute.EngineExecutionContext
import org.apache.linkis.engineconn.launch.EngineConnServer
import org.apache.linkis.governance.common.utils.GovernanceConstant
import org.apache.linkis.manager.common.entity.resource.{CommonNodeResource, LoadResource, NodeResource}
import org.apache.linkis.manager.engineplugin.appconn.conf.AppConnEngineConnConfiguration
import org.apache.linkis.manager.engineplugin.appconn.exception.AppConnExecuteFailedException
import org.apache.linkis.manager.engineplugin.common.conf.EngineConnPluginConf
import org.apache.linkis.manager.label.entity.Label
import org.apache.linkis.manager.label.entity.engine.EngineTypeLabel
import org.apache.linkis.protocol.UserWithCreator
import org.apache.linkis.protocol.engine.JobProgressInfo
import org.apache.linkis.scheduler.executer.{AsynReturnExecuteResponse, ErrorExecuteResponse, ExecuteResponse, SuccessExecuteResponse}
import org.apache.linkis.server.BDPJettyServerHelper
import org.apache.commons.lang.StringUtils
import org.apache.linkis.manager.engineplugin.appconn.executor.AppConnEngineConnExecutor._

import scala.beans.BeanProperty

class AppConnEngineConnExecutor(override val outputPrintLimit: Int, val id: Int) extends AsyncConcurrentComputationExecutor(outputPrintLimit) {

  @BeanProperty
  var userWithCreator: UserWithCreator = _

  private val executorLabels: util.List[Label[_]] = new util.ArrayList[Label[_]](2)

  private val refFactory = new DefaultRefFactory

  //  private var engineExecutionContext: EngineExecutionContext = _

  private val taskHashMap: ConcurrentHashMap[String, (AsyncResponseRef, RefExecutionOperation)] = new ConcurrentHashMap[String, (AsyncResponseRef, RefExecutionOperation)](8)

  //定时清除map元素
  Utils.defaultScheduler.scheduleAtFixedRate(new Runnable {
    override def run(): Unit = Utils.tryAndError({
      val iterator = taskHashMap.entrySet().iterator()
      while (iterator.hasNext) {
        val current = iterator.next()
        if (current.getValue._1.isCompleted) {
          info("Begin to remove task map " + current.getKey)
          iterator.remove()
        }
      }
    })

  }, 0, 5, TimeUnit.MINUTES)

  override def executeLine(engineExecutorContext: EngineExecutionContext, code: String): ExecuteResponse = {
    info("got code:[" + code + "]")
    info("got params:[" + BDPJettyServerHelper.gson.toJson(engineExecutorContext.getProperties) + "]")

    //    if (engineExecutorContext != this.engineExecutionContext) {
    //      this.engineExecutionContext = engineExecutorContext
    //      info("Appconn executor reset new engineExecutorContext!")
    //    }
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
        engineExecutorContext.getLabels.find(_.isInstanceOf[EngineTypeLabel]).foreach {
          case engineTypeLabel: EngineTypeLabel =>
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
    if (appConn == null) {
      error("appconn is null")
      throw AppConnExecuteFailedException(510001, "Cannot Find appConnName: " + appConnName)
    }
    //val labels = getDSSLabels(engineExecutorContext.getLabels)
    var labels = engineExecutorContext.getProperties.get("labels").toString
    getAppInstanceByLabels(labels, appConn) match {
      case Some(appInstance) =>
        val developmentIntegrationStandard = appConn.asInstanceOf[OnlyDevelopmentAppConn].getOrCreateDevelopmentStandard
        val refExecutionService = developmentIntegrationStandard.getRefExecutionService(appInstance)
        val refExecutionOperation = refExecutionService.getRefExecutionOperation
        Utils.tryCatch({
          val res = refExecutionOperation.execute(requestRef)
          res match {
            case asyncResponseRef: AsyncResponseRef =>
              if (refExecutionOperation.isInstanceOf[Killable]) {
                if (null != engineExecutorContext.getJobId.get) {
                  info("add async response to task map " + engineExecutorContext.getJobId.get)
                  taskHashMap.put(engineExecutorContext.getJobId.get, (asyncResponseRef, refExecutionOperation))
                }
              }
              new AsynReturnExecuteResponse {
                private var er: ExecuteResponse => Unit = null

                def tryToNotifyAll(responseRef: ResponseRef): Unit = {
                  val executeResponse = createExecuteResponse(responseRef, appConnName)
                  if (er == null) this synchronized (while (er == null) this.wait(1000))
                  er(executeResponse)
                }

                override def notify(rs: ExecuteResponse => Unit): Unit = {
                  er = rs
                  this synchronized notifyAll()
                }

                asyncResponseRef.notifyMe(new java.util.function.Consumer[ResponseRef] {
                  override def accept(t: ResponseRef): Unit = tryToNotifyAll(t)
                })
              }
            case responseRef: ResponseRef =>
              createExecuteResponse(responseRef, appConnName)
            case _ =>
              throw AppConnExecuteFailedException(510009, "appconn execute return error for: " + res)
          }
        })(ErrorExecuteResponse("Failed to execute appconn", _))
      case None =>
        throw AppConnExecuteFailedException(510000, "Cannot Find AppInstance by labels " + labels)
    }
  }


  private def createExecuteResponse(responseRef: ResponseRef, appConnName: String): ExecuteResponse =
    if (responseRef.isSucceed) SuccessExecuteResponse()
    else {
      val exception = responseRef match {
        case response: CompletedExecutionResponseRef => response.getException
        case _ => null
      }
      error(s"$appConnName execute failed, failed reason is ${
        responseRef.getErrorMsg
      }.", exception)
      ErrorExecuteResponse(responseRef.getErrorMsg, exception)
    }

  private def getAppConnName(nodeType: String) = {
    StringUtils.split(nodeType, ".")(0)
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

  override def progress(taskID: String): Float = 0

  override def getProgressInfo(taskID: String): Array[JobProgressInfo] = Array.empty

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

  override def getConcurrentLimit: Int = {
    AppConnEngineConnConfiguration.CONCURRENT_LIMIT.getValue
  }

  override def killAll(): Unit = {
  }

  override def killTask(taskID: String): Unit = {
    warn(s"Appconn Kill job : ${taskID},size is " + taskHashMap.size())
    if (taskHashMap.containsKey(taskID)) {
      val response = taskHashMap.get(taskID).asInstanceOf[(AsyncExecutionResponseRef, RefExecutionOperation)]
      if (response._1.getAction.isInstanceOf[AbstractRefExecutionAction]) {

        Utils.tryCatch {
          response._1.getAction.asInstanceOf[AbstractRefExecutionAction].setKilledFlag(true)
          taskHashMap.remove(taskID)
          response._2.asInstanceOf[Killable].kill(response._1.getAction)
          info(s"Appconn Kill job : ${taskID} success " + taskHashMap.size())
        } {
          case e: Exception =>
            warn("kill error. " + e.getMessage)
        }
      }

    } else {
      warn(s"Appconn Kill job : ${taskID} failed for taskID is not exsit")
    }
    super.killTask(taskID)
  }
}


object AppConnEngineConnExecutor {

  private val WORKSPACE_NAME_STR = "workspace"

  private val PROJECT_NAME_STR = "projectName"

  private val FLOW_NAME_STR = "flowName"

  private val NODE_NAME_STR = "nodeName"

  private val NODE_TYPE = "nodeType"

}