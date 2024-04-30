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

package com.webank.wedatasphere.dss.flow.execution.entrance.utils

import java.lang.reflect.Type
import java.util.Date
import java.{lang, util}

import com.google.gson._
import com.webank.wedatasphere.dss.common.entity.Resource
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.flow.execution.entrance.exception.FlowExecutionErrorException
import com.webank.wedatasphere.dss.flow.execution.entrance.strategy.StrategyFactory
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode
import org.apache.linkis.governance.common.entity.job.JobRequest
import org.apache.linkis.governance.common.entity.task.RequestPersistTask
import org.apache.linkis.manager.label.entity.Label
import org.apache.linkis.manager.label.entity.engine.CodeLanguageLabel
import org.apache.linkis.manager.label.utils.LabelUtil

import scala.collection.JavaConversions._
import scala.collection.JavaConverters.asScalaBufferConverter

object FlowExecutionUtils {

  def isSkippedNode(node: WorkflowNode, paramsMap: java.util.Map[String, Any]): Boolean = {
    val skip = Option(node.getDSSNode).map(_.getParams).map(_.get("configuration"))
      .map(_.asInstanceOf[java.util.Map[String, Object]].get("special"))
      .map(_.asInstanceOf[java.util.Map[String, Object]].get("auto.disabled"))
      .map(_.toString).exists("true".equalsIgnoreCase)
    if (skip) {
      return true
    }
    val executeStrategy = paramsMap.get("executeStrategy")
    if (executeStrategy != null) {
      return StrategyFactory.getNodeSkipStrategy(executeStrategy.toString).isSkippedNode(node, paramsMap, isReversedChoose = false)
    }
    false
  }

  def getRunTypeLabel(labels: util.List[Label[_]]): CodeLanguageLabel = {
    labels.find {
      case label: CodeLanguageLabel => true
      case _ => false
    }.map(_.asInstanceOf[CodeLanguageLabel]).getOrElse(throw new FlowExecutionErrorException(90106, "Cannot find runType label."))
  }

  def isSignalNode(jobType: String): Boolean = {
    FlowExecutionEntranceConfiguration.SIGNAL_NODES.getValue.split(",").exists(_.equalsIgnoreCase(jobType))
  }

  def isAppConnJob(engineType: String): Boolean = LinkisJobExecutionConfiguration.APPCONN.equalsIgnoreCase(engineType)


  def resourcesAdaptation(resources: util.List[Resource]): util.ArrayList[BMLResource] = {
    val bmlResources = new util.ArrayList[BMLResource]()
    for (resource <- resources) {
      bmlResources.add(resourceConvertToBMLResource(resource))
    }
    bmlResources
  }

  def resourceConvertToBMLResource(resource: Resource): BMLResource = {
    val bmlResource = new BMLResource()
    bmlResource.setFileName(resource.getFileName)
    bmlResource.setResourceId(resource.getResourceId)
    bmlResource.setVersion(resource.getVersion)
    bmlResource
  }


  def jobRequest2RequestPersistTask(jobReq: JobRequest): RequestPersistTask = {
    if (null == jobReq) return null
    val persistTask = new RequestPersistTask
    persistTask.setTaskID(jobReq.getId)
    persistTask.setExecId(jobReq.getReqId)
    //    jobHistory.setPriority(jobReq.getProgress)
    persistTask.setSubmitUser(jobReq.getSubmitUser)
    persistTask.setUmUser(jobReq.getExecuteUser)
    persistTask.setSource(jobReq.getSource)
    //    persistTask.setExecutionCode(jobReq.getExecutionCode)
    if (null != jobReq.getLabels) {
      val labelMap = new util.HashMap[String, String](jobReq.getLabels.size())
      jobReq.getLabels.asScala.map(l => l.getLabelKey -> l.getStringValue).foreach(kv => labelMap.put(kv._1, kv._2))
      persistTask.setLabels(jobReq.getLabels)
    }
    if (null != jobReq.getParams) persistTask.setParams(jobReq.getParams)
    persistTask.setProgress(jobReq.getProgress.toFloat)
    persistTask.setStatus(jobReq.getStatus)
    persistTask.setLogPath(jobReq.getLogPath)
    persistTask.setErrCode(jobReq.getErrorCode)
    persistTask.setErrDesc(jobReq.getErrorDesc)
    if (null != jobReq.getCreatedTime) persistTask.setCreatedTime(new Date(jobReq.getCreatedTime.getTime))
    if (null != jobReq.getUpdatedTime) persistTask.setUpdatedTime(new Date(jobReq.getUpdatedTime.getTime))
    persistTask.setInstance(jobReq.getInstances)
    //    if (null != jobReq.getMetrics) persistTask.set(gson.toJson(jobReq.getMetrics))
    val engineType = LabelUtil.getEngineType(jobReq.getLabels)
    persistTask.setEngineType(engineType)
    persistTask
  }

  implicit val gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls
    .registerTypeAdapter(classOf[java.lang.Double], new JsonSerializer[java.lang.Double] {
      override def serialize(t: lang.Double, `type`: Type, jsonSerializationContext: JsonSerializationContext): JsonElement =
        if (t == t.longValue()) new JsonPrimitive(t.longValue()) else new JsonPrimitive(t)
    }).create

}
