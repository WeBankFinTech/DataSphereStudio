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

package com.webank.wedatasphere.dss.flow.execution.entrance.node

import java.util

import com.webank.wedatasphere.dss.common.entity.Resource
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration
import com.webank.wedatasphere.dss.flow.execution.entrance.job._
import com.webank.wedatasphere.dss.flow.execution.entrance.utils.FlowExecutionUtils
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource
import com.webank.wedatasphere.dss.linkis.node.execution.job._
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode
import org.apache.commons.lang.StringUtils


object AppConnJobBuilder {

  def builder():FlowBuilder = new FlowBuilder

  class FlowBuilder extends Builder {

    private var jobProps: java.util.Map[String, String] = _

    private var node: WorkflowNode = _

    def setJobProps(jobProps: java.util.Map[String, String]): FlowBuilder = {
      this.jobProps = jobProps
      this
    }

    def setNode(node: WorkflowNode): FlowBuilder = {
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
        val linkisJob = new FlowExecutionAppConnLinkisJob
        linkisJob.setJobProps(this.jobProps)
        linkisJob
      }
    }

    override protected def fillJobInfo(job: Job): Unit = {
      if(StringUtils.isNotEmpty(jobProps.get(FlowExecutionEntranceConfiguration.COMMAND)))
        job.setCode(jobProps.get(FlowExecutionEntranceConfiguration.COMMAND))
      job.setParams(new util.HashMap[String,AnyRef]())
      val runtimeMap = new util.HashMap[String, AnyRef]()
      runtimeMap.put(FlowExecutionEntranceConfiguration.NODE_NAME, jobProps.get(FlowExecutionEntranceConfiguration.JOB_ID))
      runtimeMap.put(FlowExecutionEntranceConfiguration.WORKSPACE, jobProps.get(FlowExecutionEntranceConfiguration.WORKSPACE))
      runtimeMap.put(DSSCommonUtils.DSS_LABELS_KEY, jobProps.get(DSSCommonUtils.DSS_LABELS_KEY))
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

    override protected def fillCommonLinkisJobInfo(linkisAppConnJob: CommonLinkisJob): Unit = {
      linkisAppConnJob.setJobResourceList(FlowExecutionUtils.resourcesAdaptation(this.node.getDSSNode.getResources))
      this.node.getDSSNode.getParams.remove(FlowExecutionEntranceConfiguration.PROJECT_RESOURCES) match {
        case projectResources:util.List[Resource] =>
          linkisAppConnJob.setProjectResourceList(FlowExecutionUtils.resourcesAdaptation(projectResources))
        case _ =>
      }
      this.node.getDSSNode.getParams.remove(FlowExecutionEntranceConfiguration.FLOW_RESOURCES) match {
        case flowResources:util.HashMap[String, util.List[BMLResource]] =>
          linkisAppConnJob.setFlowNameAndResources(flowResources)
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

    override protected def getContextID(job: Job): String = jobProps.get(FlowExecutionEntranceConfiguration.CONTEXT_ID)
  }
}
