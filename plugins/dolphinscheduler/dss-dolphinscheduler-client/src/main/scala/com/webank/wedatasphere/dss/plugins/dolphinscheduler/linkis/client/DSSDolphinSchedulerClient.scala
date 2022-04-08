package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client

import java.util

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.execution.impl.LinkisNodeExecutionImpl
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job
import com.webank.wedatasphere.dss.linkis.node.execution.listener.LinkisExecutionListener
import com.webank.wedatasphere.dss.linkis.node.execution.log.LinkisJobExecutionLog
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.conf.LinkisJobTypeConf
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.job.DolphinSchedulerJobBuilder
import org.apache.commons.lang.StringUtils
import org.apache.linkis.common.conf.CommonVars
import org.apache.linkis.common.utils.{Logging, Utils}

object DSSDolphinSchedulerClient extends Logging {
  val DEFAULT_PROPERTY_FILE_NAME = "linkis.properties"
  val DEFAULT_CONFIG_DIR = "conf"
  val CHARSET_NAME = "utf-8"

  val logObj: LinkisJobExecutionLog = new LinkisJobExecutionLog {
    override def info(message: Object, t: Throwable): Unit =
      if(message != null) logger.info(message.toString, t) else if(t != null) logger.info(null, t)
    override def warn(message: Object, t: Throwable): Unit =
      if(message != null) logger.warn(message.toString, t) else if(t != null) logger.warn(null, t)
    override def error(message: Object, t: Throwable): Unit =
      if(message != null) logger.error(message.toString, t) else if(t != null) logger.error(null, t)
  }

  def main(args: Array[String]): Unit = {

    val jobProps = new util.HashMap[String, String]
    val getAndSet = (fromKey: String, toKey: String) => {
      val value = CommonVars(fromKey, "").getValue
      if (StringUtils.isNotBlank(value)){
        logObj.info(s"$toKey => $value.")
        jobProps.put(toKey, value)
      }
    }
    getAndSet("LINKIS_TYPE", LinkisJobTypeConf.LINKIS_TYPE)
    getAndSet("RUN_DATE", LinkisJobTypeConf.RUN_DATE)
    getAndSet("CONTEXT_ID", LinkisJobExecutionConfiguration.FLOW_CONTEXTID)
    getAndSet("LINKIS_GATEWAY_URL", LinkisJobExecutionConfiguration.LINKIS_URL_1_X.key)
    getAndSet("PROXY_USER", LinkisJobTypeConf.PROXY_USER)
    getAndSet("JOB_SOURCE", LinkisJobTypeConf.JOB_SOURCE)
    getAndSet("JOB_PARAMS", LinkisJobTypeConf.JOB_PARAMS)
    getAndSet("JOB_COMMAND", LinkisJobTypeConf.JOB_COMMAND)
    getAndSet("JOB_RESOURCES", LinkisJobTypeConf.JOB_RESOURCES)
    getAndSet("FLOW_RESOURCES", LinkisJobTypeConf.FLOW_RESOURCES)
    getAndSet("FLOW_PROPERTIES", LinkisJobTypeConf.FLOW_RESOURCES)
    getAndSet("JOB_LABELS", LinkisJobTypeConf.JOB_LABELS)
    val job: Job = new DolphinSchedulerJobBuilder(jobProps).build()
    job.setLogObj(logObj)

    val execution = LinkisNodeExecutionImpl.getLinkisNodeExecution
    execution.runJob(job)

    Utils.addShutdownHook {
      if(!execution.isCompleted(job)) {
        logObj.info(s"try to kill job ${job.getJobName}, since a shutdown signal is called.")
        execution.cancel(job)
      }
    }
    execution.waitForComplete(job)

    Utils.tryAndWarn {
      val endLog = LinkisNodeExecutionImpl.getLinkisNodeExecution.getLog(job)
      logObj.info(endLog)
    }

    LinkisNodeExecutionImpl.getLinkisNodeExecution match {
      case listener: LinkisExecutionListener =>
        listener.onStatusChanged(null, LinkisNodeExecutionImpl.getLinkisNodeExecution.getState(job), job)
    }

    val resultSize = Utils.tryAndWarn(LinkisNodeExecutionImpl.getLinkisNodeExecution.getResultSize(job))
    if(resultSize > 0) (0 until resultSize).foreach{ index =>
      val resultLine = LinkisNodeExecutionImpl.getLinkisNodeExecution.getResult(job, index, LinkisJobExecutionConfiguration.RESULT_PRINT_SIZE.getValue(jobProps))
      logObj.info(s"The content of the ${index + 1}th resultset is : $resultLine.")
    }
    logObj.info("\n")
    logObj.info("-----------------------------------------------------------")
    logObj.info("Finished to execute job.")
  }

}
