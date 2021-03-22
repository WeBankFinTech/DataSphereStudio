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

package com.webank.wedatasphere.dss.flow.execution.entrance.node

import java.{lang, util}

import com.webank.wedatasphere.dss.appconn.schedule.core.entity.{ReadNode, SchedulerNode, ShareNode}
import com.webank.wedatasphere.dss.common.entity.Resource
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.flow.execution.entrance.job._
import com.webank.wedatasphere.dss.flow.execution.entrance.utils.FlowExecutionUtils
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource
import com.webank.wedatasphere.dss.linkis.node.execution.execution.impl.LinkisNodeExecutionImpl
import com.webank.wedatasphere.dss.linkis.node.execution.job._
import com.webank.wedatasphere.dss.linkis.node.execution.parser.JobParamsParser
import org.apache.commons.lang.StringUtils

/**
  * Created by johnnwang on 2019/11/5.
  */
object AppConnJobBuilder {

  val signalKeyCreator = new FlowExecutionJobSignalKeyCreator

  init()

  def init(): Unit ={
    val jobParamsParser = new JobParamsParser

    jobParamsParser.setSignalKeyCreator(signalKeyCreator)

    LinkisNodeExecutionImpl.getLinkisNodeExecution.asInstanceOf[LinkisNodeExecutionImpl].registerJobParser(jobParamsParser)
  }

  def builder():FlowBuilder = new FlowBuilder

  class FlowBuilder extends Builder {


    private var jobProps: java.util.Map[String, String] = _

    private var node: SchedulerNode = _

    def setJobProps(jobProps: java.util.Map[String, String]): FlowBuilder = {
      this.jobProps = jobProps
      this
    }

    def setNode(node: SchedulerNode): FlowBuilder = {
      this.node = node
      this
    }

    override protected def getJobType: String =  jobProps.getOrDefault(LinkisJobExecutionConfiguration.LINKIS_TYPE, LinkisJobExecutionConfiguration.LINKIS_DEFAULT_TYPE.getValue(jobProps))

    override protected def creatLinkisJob(isLinkisType: Boolean): LinkisJob = {
      if(isLinkisType){
        val linkisJob = new FlowExecutionCommonLinkisJob
        linkisJob.setJobProps(this.jobProps)
        linkisJob
      } else {
        val linkisJob = new FlowExecutionAppJointLinkisJob
        linkisJob.setJobProps(this.jobProps)
        linkisJob
      }
    }

    override protected def fillJobInfo(job: Job): Unit = {
      if(StringUtils.isNotEmpty(jobProps.get(FlowExecutionEntranceConfiguration.COMMAND)))
        job.setCode(jobProps.get(FlowExecutionEntranceConfiguration.COMMAND))
      job.setParams(new util.HashMap[String,AnyRef]())
      val runtimeMap = new util.HashMap[String, AnyRef]()
      //update by peaceWong add contextID to runtimeMap
      if (null != jobProps.get(FlowExecutionEntranceConfiguration.CONTEXT_ID)){
        runtimeMap.put(FlowExecutionEntranceConfiguration.CONTEXT_ID, jobProps.get(FlowExecutionEntranceConfiguration.CONTEXT_ID))
      }
      runtimeMap.put(FlowExecutionEntranceConfiguration.NODE_NAME,jobProps.get(FlowExecutionEntranceConfiguration.JOB_ID))
      runtimeMap.put(FlowExecutionEntranceConfiguration.WORKSPACE, jobProps.get(FlowExecutionEntranceConfiguration.WORKSPACE))
      runtimeMap.put(FlowExecutionEntranceConfiguration.LABELS, jobProps.get(FlowExecutionEntranceConfiguration.LABELS))
      job.setRuntimeParams(runtimeMap)
    }

    override protected def fillLinkisJobInfo(linkisJob: LinkisJob): Unit = {
      this.node.getDSSNode.getParams.get(FlowExecutionEntranceConfiguration.NODE_CONFIGURATION_KEY) match {
        case configuration:util.Map[String, AnyRef] =>
          linkisJob.setConfiguration(configuration)
        case _ =>
      }
      this.node.getDSSNode.getParams.remove(FlowExecutionEntranceConfiguration.FLOW_VAR_MAP) match {
        case flowVar:util.Map[String, AnyRef] =>
          linkisJob.setVariables(flowVar)
        case _ =>
      }
      linkisJob.setSource(getSource)
    }

    override protected def fillCommonLinkisJobInfo(linkisAppjointJob: CommonLinkisJob): Unit = {
      linkisAppjointJob.setJobResourceList(FlowExecutionUtils.resourcesAdaptation(this.node.getDSSNode.getResources))
      this.node.getDSSNode.getParams.remove(FlowExecutionEntranceConfiguration.PROJECT_RESOURCES) match {
        case projectResources:util.List[Resource] =>
          linkisAppjointJob.setProjectResourceList(FlowExecutionUtils.resourcesAdaptation(projectResources))
        case _ =>
      }
      this.node.getDSSNode.getParams.remove(FlowExecutionEntranceConfiguration.FLOW_RESOURCES) match {
        case flowResources:util.HashMap[String, util.List[BMLResource]] =>
          linkisAppjointJob.setFlowNameAndResources(flowResources)
        case _ =>
      }
    }


    private def getSource: util.Map[String, String] = {
      val source = new util.HashMap[String, String]()
      source.put("projectName", jobProps.get(FlowExecutionEntranceConfiguration.PROJECT_NAME))
      source.put("flowName", jobProps.get(FlowExecutionEntranceConfiguration.FLOW_NAME))
      source.put("nodeName", jobProps.get(FlowExecutionEntranceConfiguration.JOB_ID))
      source
    }


  }
}
