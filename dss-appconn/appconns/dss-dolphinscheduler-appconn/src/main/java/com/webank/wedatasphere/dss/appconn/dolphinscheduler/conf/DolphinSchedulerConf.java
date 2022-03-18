package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf;

import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.conf.TimeType;

public interface DolphinSchedulerConf {

    CommonVars<String> DS_ADMIN_USER = CommonVars.apply("wds.dss.appconn.ds.admin.user", "admin");

    CommonVars<String> DS_ADMIN_TOKEN = CommonVars.apply("wds.dss.appconn.ds.admin.token", "fcdd944c03d5792719781f2c6e7b7542");

    CommonVars<TimeType> DS_TOKEN_EXPIRE_TIME = CommonVars.apply("wds.dss.appconn.ds.token.expire.time", new TimeType("2h"));

    CommonVars<TimeType> DS_TOKEN_EXPIRE_TIME_GAP = CommonVars.apply("wds.dss.appconn.ds.token.expire.gap", new TimeType("1m"));

    CommonVars<String> DS_VERSION = CommonVars.apply("wds.dss.appconn.ds.version", "1.3.9");

}
