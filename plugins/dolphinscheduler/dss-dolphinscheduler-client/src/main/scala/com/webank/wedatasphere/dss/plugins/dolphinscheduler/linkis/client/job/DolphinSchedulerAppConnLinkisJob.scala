package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.job

import com.webank.wedatasphere.dss.linkis.node.execution.job.AbstractAppConnLinkisJob
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.conf.LinkisJobTypeConf

class DolphinSchedulerAppConnLinkisJob extends AbstractAppConnLinkisJob {
  override def getSubmitUser: String = if (getJobProps.get(LinkisJobTypeConf.FLOW_SUBMIT_USER).isEmpty) getJobProps.get(LinkisJobTypeConf.PROXY_USER) else getJobProps.get(LinkisJobTypeConf.FLOW_SUBMIT_USER)

  override def getUser: String = getJobProps.get(LinkisJobTypeConf.PROXY_USER)

  override def getJobName: String = getJobProps.get(LinkisJobTypeConf.JOB_ID, "test")
}
