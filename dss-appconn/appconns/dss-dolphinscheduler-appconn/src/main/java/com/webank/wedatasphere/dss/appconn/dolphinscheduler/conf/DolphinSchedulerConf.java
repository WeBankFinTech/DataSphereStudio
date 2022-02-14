package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf;

import org.apache.linkis.common.conf.CommonVars;

/**
 * The interface Dolphin scheduler conf.
 *
 * @author yuxin.yuan
 * @date 2021/10/22
 */
public interface DolphinSchedulerConf {

    CommonVars<String> DS_ADMIN_TOKEN = CommonVars.apply("wds.dss.ds.admin.token", "fcdd944c03d5792719781f2c6e7b7542");
    CommonVars<String>  JOB_EXECUTE_TYPE = CommonVars.apply("wds.dss.job.execute.type", "linkis"); //调度作业执行方式 dolphin,linkis
    CommonVars<String> NGINX_URL = CommonVars.apply("wds.dss.nginx.url", "http://172.24.2.230:8088");


}
