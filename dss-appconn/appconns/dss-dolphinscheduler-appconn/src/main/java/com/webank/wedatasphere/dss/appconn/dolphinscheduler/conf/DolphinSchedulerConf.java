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

    CommonVars<String> DS_DOLPHIN_KERBEROS_PRINCIPALS= CommonVars.apply("wds.dss.dolphin.kerberos.principals", "hive");
    CommonVars<String> DS_DOLPHIN_KERBEROS_KEYTAB= CommonVars.apply("wds.dss.dolphin.kerberos.keytab", "/opt/soft/hadoop/hive.keytab");
    CommonVars<String> DS_HIVE_SERVER2_URL= CommonVars.apply("wds.dss.hive.server2.url", "jdbc:hive2://10.30.33.24:10000/default;principal=hive/nm-bigdata-030033024.ctc.local@EWS.BIGDATA.CHINATELECOM.CN.UAT");


}
