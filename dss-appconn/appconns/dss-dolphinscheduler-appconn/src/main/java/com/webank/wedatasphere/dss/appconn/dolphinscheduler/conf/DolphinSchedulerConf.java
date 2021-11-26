package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * The interface Dolphin scheduler conf.
 *
 * @author yuxin.yuan
 * @date 2021/10/22
 */
public interface DolphinSchedulerConf {

    CommonVars<String> DS_ADMIN_TOKEN = CommonVars.apply("wds.dss.ds.admin.token", "fcdd944c03d5792719781f2c6e7b7542");

}
