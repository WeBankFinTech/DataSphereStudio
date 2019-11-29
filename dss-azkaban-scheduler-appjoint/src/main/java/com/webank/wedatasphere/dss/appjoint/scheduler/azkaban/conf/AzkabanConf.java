package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.conf;


import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * Created by cooperyang on 2019/9/16.
 */
public class AzkabanConf {

    public static final CommonVars<String> AZKABAN_BASE_URL = CommonVars.apply("wds.dss.appjoint.scheduler.azkaban.address", "");
    public static final CommonVars<String> DEFAULT_STORE_PATH = CommonVars.apply("wds.dss.appjoint.scheduler.project.store.dir", "/appcom/tmp/wds/dss");
}
