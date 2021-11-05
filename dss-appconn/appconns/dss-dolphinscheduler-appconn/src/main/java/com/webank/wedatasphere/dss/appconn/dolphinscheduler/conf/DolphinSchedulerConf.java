package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * The interface Dolphin scheduler conf.
 *
 * @author yuxin.yuan
 * @date 2021/10/22
 */
public interface DolphinSchedulerConf {

    CommonVars<String> DS_ADMIN_TOKEN = CommonVars.apply("wds.dss.ds.admin.token", "c1f1e5c8c4b5bcdfd5fead493e7b2b41");

}
