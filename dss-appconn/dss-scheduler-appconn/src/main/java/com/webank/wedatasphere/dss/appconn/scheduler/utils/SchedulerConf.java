package com.webank.wedatasphere.dss.appconn.scheduler.utils;

import org.apache.linkis.common.conf.CommonVars;

/**
 * @author enjoyyin
 * @date 2022-04-07
 * @since 1.1.0
 */
public class SchedulerConf {

    public static final CommonVars<String> JOB_LABEL = CommonVars.apply("wds.dss.appconn.scheduler.job.label",  "dev");

}
