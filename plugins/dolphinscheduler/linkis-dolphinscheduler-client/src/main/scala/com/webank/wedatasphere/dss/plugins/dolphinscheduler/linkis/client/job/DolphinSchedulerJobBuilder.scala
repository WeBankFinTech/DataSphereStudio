package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.job

import com.webank.wedatasphere.dss.linkis.node.execution.job.{Builder, CommonLinkisJob, Job, LinkisJob}
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.conf.LinkisJobTypeConf

import java.util.{Map => JMap}

class DolphinSchedulerJobBuilder(jobProps: JMap[String, String]) extends Builder {

  override protected def getJobType: String = jobProps.get(LinkisJobTypeConf.LINKIS_TYPE)

  override protected def creatLinkisJob(isLinkisType: Boolean): LinkisJob = {
    val job = if (isLinkisType) new DolphinSchedulerCommonLinkisJob else new DolphinSchedulerAppConnLinkisJob
    job.setJobProps(jobProps)
    job
  }

  override protected def fillJobInfo(job: Job): Unit = {
    job.setCode(jobProps.get("command"))
    job.setParams(new java.util.HashMap[String, Object]())
    job.setRuntimeParams(new java.util.HashMap[String, Object]())
    job.setJobProps(jobProps)
  }

  override protected def fillLinkisJobInfo(linkisJob: LinkisJob): Unit = {
    //    linkisJob.setConfiguration(findConfiguration(LinkisJobExecutionConfiguration.NODE_CONF_PREFIX))
    //    linkisJob.setVariables(findVariables(LinkisJobExecutionConfiguration.FLOW_VARIABLE_PREFIX))
    linkisJob.setSource(getSource)
  }

  override protected def fillCommonLinkisJobInfo(commonLinkisJob: CommonLinkisJob): Unit = {
    commonLinkisJob.setJobResourceList(LinkisJobExecutionUtils.getResourceListByJson(jobProps.get("resources")))
    //
    //    val projectResourceName = LinkisJobExecutionConfiguration.PROJECT_PREFIX + "." + jobProps.get(LinkisJobTypeConf.PROJECT_NAME) + LinkisJobExecutionConfiguration.RESOURCES_NAME;
    //    commonLinkisJob.setProjectResourceList(LinkisJobExecutionUtils.getResourceListByJson(jobProps.get(projectResourceName)));
    //
    //    commonLinkisJob.setFlowNameAndResources(findFLowNameAndResources());
  }

  private def getSource: JMap[String, String] = {
    val source = new java.util.HashMap[String, String]
    source.put("projectName", jobProps.getOrDefault(LinkisJobTypeConf.PROJECT_NAME, ""))
    source.put("flowName", jobProps.getOrDefault(LinkisJobTypeConf.FLOW_ID, ""))
    source.put("nodeName", jobProps.getOrDefault(LinkisJobTypeConf.JOB_ID, ""))
    source
  }
  //
  //  private def findVariables(prefix: String) = {
  //    val map = new util.HashMap[String, AnyRef]
  //    val keyIterator = jobProps.keySet.iterator
  //    while ( {
  //      keyIterator.hasNext
  //    }) {
  //      val next = keyIterator.next
  //      if (next.startsWith(prefix)) map.put(next.substring(prefix.length), jobProps.get(next))
  //    }
  //    map
  //  }
  //
  //  private def findConfiguration(prefix: String) = {
  //    def putConf(configuration: util.Map[String, AnyRef], key: String, value: String, confTypeAndName: String): Unit = {
  //      if (configuration.get(key) == null) {
  //        val startup = new util.HashMap[String, String]
  //        startup.put(confTypeAndName.substring(key.length + 1), value)
  //        configuration.put(key, startup)
  //      }
  //      else {
  //        val startup = configuration.get(key).asInstanceOf[util.Map[String, String]]
  //        startup.put(confTypeAndName.substring(key.length + 1), value)
  //      }
  //    }
  //
  //    val configuration = new util.HashMap[String, AnyRef]
  //    val keyIterator = jobProps.keySet.iterator
  //    while ( {
  //      keyIterator.hasNext
  //    }) {
  //      val next = keyIterator.next
  //      if (next.startsWith(prefix)) {
  //        val confTypeAndName = next.substring(prefix.length)
  //        if (confTypeAndName.startsWith(LinkisJobExecutionConfiguration.CONF_STARTUP)) putConf(configuration, LinkisJobExecutionConfiguration.CONF_STARTUP, jobProps.get(next), confTypeAndName)
  //        else if (confTypeAndName.startsWith(LinkisJobExecutionConfiguration.CONF_RUNTIME)) putConf(configuration, LinkisJobExecutionConfiguration.CONF_RUNTIME, jobProps.get(next), confTypeAndName)
  //        else if (confTypeAndName.startsWith(LinkisJobExecutionConfiguration.CONF_SPECIAL)) putConf(configuration, LinkisJobExecutionConfiguration.CONF_SPECIAL, jobProps.get(next), confTypeAndName)
  //      }
  //    }
  //    configuration
  //  }

  //  private def findFLowNameAndResources(): util.Map[String, util.List[BMLResource]] = {
  //    val flowNameAndResources: util.Map[String, util.List[BMLResource]] = new util.HashMap[String, util.List[BMLResource]]
  //    val iterator: util.Iterator[String] = jobProps.keySet.iterator
  //    while ( {
  //      iterator.hasNext
  //    }) {
  //      val next: String = iterator.next
  //      if (next.endsWith("resources") && next.startsWith("flow")) {
  //        flowNameAndResources.put(next, LinkisJobExecutionUtils.getResourceListByJson(jobProps.get(next)))
  //      }
  //    }
  //    flowNameAndResources
  //  }


}
