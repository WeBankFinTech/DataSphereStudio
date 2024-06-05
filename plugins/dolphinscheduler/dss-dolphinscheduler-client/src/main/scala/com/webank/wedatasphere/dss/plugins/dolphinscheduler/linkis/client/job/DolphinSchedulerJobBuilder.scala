package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.job

import java.util
import java.util.{Map => JMap}

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource
import com.webank.wedatasphere.dss.linkis.node.execution.job.{Builder, CommonLinkisJob, Job, LinkisJob}
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.conf.LinkisJobTypeConf
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.utils.JsonUtils

import scala.collection.JavaConverters._

class DolphinSchedulerJobBuilder(jobProps: JMap[String, String]) extends Builder {

  override protected def getJobType: String = jobProps.get(LinkisJobTypeConf.LINKIS_TYPE)

  override protected def creatLinkisJob(isLinkisType: Boolean): LinkisJob = {
    val job = if (isLinkisType) new DolphinSchedulerCommonLinkisJob else new DolphinSchedulerAppConnLinkisJob
    job.setJobProps(jobProps)
    job
  }

  override protected def fillJobInfo(job: Job): Unit = {
    job.setCode(jobProps.get(LinkisJobTypeConf.JOB_COMMAND))
    job.setParams(new java.util.HashMap[String, Object]())
    job.setRuntimeParams(new java.util.HashMap[String, Object])
    job.setJobProps(jobProps)
  }

  override protected def fillLinkisJobInfo(linkisJob: LinkisJob): Unit = {
    val jobParams = jobProps.get(LinkisJobTypeConf.JOB_PARAMS)
    if(StringUtils.isNotBlank(jobParams)){
      val paramsMap = JsonUtils.jackson.readValue(jobParams, classOf[util.Map[String, Object]])
      linkisJob.setConfiguration(getConfiguration(paramsMap))
      linkisJob.setVariables(getVariables(paramsMap))
    }
    val runDate = new util.HashMap[String, Object]
    runDate.put("run_date", jobProps.get(LinkisJobTypeConf.RUN_DATE))
    linkisJob.setVariables(runDate)

    val source = getSource
    linkisJob.setSource(source)
    linkisJob.getRuntimeParams.put("nodeName", source.get("nodeName"))
    linkisJob.getRuntimeParams.put("labels", jobProps.get(LinkisJobTypeConf.JOB_LABELS))
  }

  override protected def getContextID(job: Job): String = jobProps.get(LinkisJobExecutionConfiguration.FLOW_CONTEXTID)

  override protected def fillCommonLinkisJobInfo(commonLinkisJob: CommonLinkisJob): Unit = {
    val jobResources = jobProps.get(LinkisJobTypeConf.JOB_RESOURCES)
    if(StringUtils.isNotBlank(jobResources)){
      commonLinkisJob.setJobResourceList(LinkisJobExecutionUtils.getResourceListByJson(jobResources))
    }
    val flowNameAndResources = new util.HashMap[String, util.List[BMLResource]]
    if(jobProps.containsKey(LinkisJobTypeConf.FLOW_RESOURCES)) {
      flowNameAndResources.put(getSource.get("flowName") + LinkisJobExecutionConfiguration.RESOURCES_NAME,
        LinkisJobExecutionUtils.getResourceListByJson(jobProps.get(LinkisJobTypeConf.FLOW_RESOURCES)))
    }
    commonLinkisJob.setFlowNameAndResources(flowNameAndResources)
  }

  private def getSource: JMap[String, String] = {
    val jobSource = jobProps.get(LinkisJobTypeConf.JOB_SOURCE)
    if(StringUtils.isNotBlank(jobSource)) {
      JsonUtils.jackson.readValue(jobSource, classOf[util.Map[String, String]])
    } else new util.HashMap[String, String]
  }

    private def getVariables(params: util.Map[String, Object]): util.Map[String, Object] = {
      val variable = params.get("variable") match {
        case variables: util.Map[String, Object] =>
          variables
        case _ => new util.HashMap[String, AnyRef]
      }
      val flowProperties = jobProps.get(LinkisJobTypeConf.FLOW_PROPERTIES)
      if(StringUtils.isNotBlank(flowProperties))
        JsonUtils.jackson.readValue(flowProperties, classOf[util.List[util.Map[String, Object]]])
          .asScala.foreach(variable.putAll(_))
      variable
    }

    private def getConfiguration(params: util.Map[String, Object]): util.Map[String, Object] = {
      params.get("configuration") match {
        case configuration: util.Map[String, Object] =>
          configuration
        case _ => new util.HashMap[String, AnyRef]
      }
    }

}
