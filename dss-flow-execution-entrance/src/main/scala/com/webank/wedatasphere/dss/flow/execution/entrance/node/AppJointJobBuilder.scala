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

package com.webank.wedatasphere.dss.flow.execution.entrance.node

import java.{lang, util}

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.{ReadNode, SchedulerNode, ShareNode}
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
object AppJointJobBuilder {

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

    override protected def isReadNode: lang.Boolean = this.node.isInstanceOf[ReadNode]

    override protected def isShareNod: lang.Boolean = this.node.isInstanceOf[ShareNode]

    override protected def isSignalSharedNode: lang.Boolean = {
      FlowExecutionUtils.isSignalNode(getJobType)
    }

    override protected def createReadJob(isLinkisType: Boolean): ReadJob = {
      if(isLinkisType){
        val readJob = new FlowExecutionCommonLinkisReadJob
        readJob.setReadNode(this.node.asInstanceOf[ReadNode])
        readJob.setJobProps(this.jobProps)
        readJob
      } else {
        val readJob = new FlowExecutionAppJointLinkisReadJob
        readJob.setReadNode(this.node.asInstanceOf[ReadNode])
        readJob.setJobProps(this.jobProps)
        readJob
      }
    }


    override protected def createSharedJob(isLinkisType: Boolean): SharedJob = {
      if(isLinkisType){
        val sharedJob = new FlowExecutionCommonLinkisSharedJob
        sharedJob.setJobProps(this.jobProps)
        sharedJob.setSharedNum(this.node.asInstanceOf[ShareNode].getShareTimes)
        sharedJob
      } else {
        val sharedJob = new FlowExecutionAppJointLinkisSharedJob
        sharedJob.setJobProps(this.jobProps)
        sharedJob.setSharedNum(this.node.asInstanceOf[ShareNode].getShareTimes)
        sharedJob
      }
    }



    override protected def createSignalSharedJob(isLinkisType: Boolean): SignalSharedJob = {
      if(isLinkisType){
        null
      } else {
        val signalJob = new FlowExecutionAppJointSignalSharedJob
        signalJob.setSignalKeyCreator(signalKeyCreator)
        signalJob.setJobProps(this.jobProps)
        signalJob
      }
    }

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
      job.setRuntimeParams(new util.HashMap[String,AnyRef]())
    }

    override protected def fillLinkisJobInfo(linkisJob: LinkisJob): Unit = {
      this.node.getDWSNode.getParams.get(FlowExecutionEntranceConfiguration.NODE_CONFIGURATION_KEY) match {
        case configuration:util.Map[String, AnyRef] =>
          linkisJob.setConfiguration(configuration)
        case _ =>
      }
      this.node.getDWSNode.getParams.remove(FlowExecutionEntranceConfiguration.FLOW_VAR_MAP) match {
        case flowVar:util.Map[String, AnyRef] =>
          linkisJob.setVariables(flowVar)
        case _ =>
      }
      linkisJob.setSource(getSource)
    }

    override protected def fillCommonLinkisJobInfo(linkisAppjointJob: CommonLinkisJob): Unit = {
      linkisAppjointJob.setJobResourceList(FlowExecutionUtils.resourcesAdaptation(this.node.getDWSNode.getResources))
      this.node.getDWSNode.getParams.remove(FlowExecutionEntranceConfiguration.PROJECT_RESOURCES) match {
        case projectResources:util.List[Resource] =>
          linkisAppjointJob.setProjectResourceList(FlowExecutionUtils.resourcesAdaptation(projectResources))
        case _ =>
      }
      this.node.getDWSNode.getParams.remove(FlowExecutionEntranceConfiguration.FLOW_RESOURCES) match {
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
