package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client

import java.util

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration
import com.webank.wedatasphere.dss.linkis.node.execution.execution.impl.LinkisNodeExecutionImpl
import com.webank.wedatasphere.dss.linkis.node.execution.job.{JobTypeEnum, LinkisJob}
import com.webank.wedatasphere.dss.linkis.node.execution.listener.LinkisExecutionListener
import com.webank.wedatasphere.dss.linkis.node.execution.log.LinkisJobExecutionLog
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.conf.LinkisJobTypeConf
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.job.DolphinSchedulerJobBuilder
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.conf.CommonVars
import org.apache.linkis.common.utils.{Logging, Utils}

object DSSDolphinSchedulerClient extends Logging {

  def main(args: Array[String]): Unit = {

    val dsVersion = CommonVars("DS_VERSION", "").getValue
    info(s"The DolphinScheduler Version => $dsVersion.")

    val logObj: LinkisJobExecutionLog = if(StringUtils.isNotBlank(dsVersion) && dsVersion.startsWith("2.")) {
      new LinkisJobExecutionLog {
        override def info(message: Object, t: Throwable): Unit =
          if(message != null) logger.info(message.toString, t) else if(t != null) logger.info(null, t)
        override def warn(message: Object, t: Throwable): Unit =
          if(message != null) logger.warn(message.toString, t) else if(t != null) logger.warn(null, t)
        override def error(message: Object, t: Throwable): Unit =
          if(message != null) logger.error(message.toString, t) else if(t != null) logger.error(null, t)
      }
    } else {
      warn("For the lower DolphinScheduler versions(< 2.0), we will parse the yarn applicationId to application__\\d+_\\d+.")
      warn("Why? Because the lower DolphinScheduler versions will block the thread until the yarn application is completed.")
      // For more information, please visit the class AbstractCommandExecutor in DolphinScheduler (line 222 ~ 231).
      new LinkisJobExecutionLog {
        private implicit def parseYarnApplication(message: Object): String = message match {
          case str: String if str.contains(" application_") =>
            str.replaceAll(" application_", " application__")
          case str: String => str
          case obj if obj != null => obj.toString
          case _ => ""
        }
        override def info(message: Object, t: Throwable): Unit = logger.info(message, t)
        override def warn(message: Object, t: Throwable): Unit = logger.warn(message, t)
        override def error(message: Object, t: Throwable): Unit = logger.error(message, t)
      }
    }

    val jobProps = new util.HashMap[String, String]
    val getAndSet = (fromKey: String, toKey: String) => {
      val value = CommonVars(fromKey, "").getValue
      if (StringUtils.isNotBlank(value)){
        logObj.info(s"$toKey => $value.")
        jobProps.put(toKey, value)
      }
    }
    getAndSet("LINKIS_TYPE", LinkisJobTypeConf.LINKIS_TYPE)
    getAndSet("LINKIS_VERSION", LinkisJobTypeConf.LINKIS_VERSION)
    getAndSet("RUN_DATE", LinkisJobTypeConf.RUN_DATE)
    getAndSet("CONTEXT_ID", LinkisJobExecutionConfiguration.FLOW_CONTEXTID)
    getAndSet("LINKIS_GATEWAY_URL", LinkisJobExecutionConfiguration.LINKIS_URL_1_X.key)
    getAndSet("PROXY_USER", LinkisJobTypeConf.PROXY_USER)
    getAndSet("JOB_SOURCE", LinkisJobTypeConf.JOB_SOURCE)
    getAndSet("JOB_PARAMS", LinkisJobTypeConf.JOB_PARAMS)
    getAndSet("JOB_COMMAND", LinkisJobTypeConf.JOB_COMMAND)
    getAndSet("JOB_RESOURCES", LinkisJobTypeConf.JOB_RESOURCES)
    getAndSet("FLOW_RESOURCES", LinkisJobTypeConf.FLOW_RESOURCES)
    getAndSet("FLOW_PROPERTIES", LinkisJobTypeConf.FLOW_PROPERTIES)
    getAndSet("JOB_LABELS", LinkisJobTypeConf.JOB_LABELS)
    val job = new DolphinSchedulerJobBuilder(jobProps).build() match {
      case linkisJob: LinkisJob => linkisJob
    }
    job.setLogObj(logObj)
    job.getJobType match {
      case JobTypeEnum.EmptyJob =>
        logObj.warn("This node is empty node, just do nothing and return.")
        return
      case _ =>
    }

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
