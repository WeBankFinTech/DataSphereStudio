package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.conf;


import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * Created by cooperyang on 2019/9/16.
 */
public class AzkabanConf {

    public static final CommonVars<String> AZKABAN_BASE_URL = CommonVars.apply("wds.dss.appjoint.scheduler.azkaban.address", "");
    public static final CommonVars<String> DEFAULT_STORE_PATH = CommonVars.apply("wds.dss.appjoint.scheduler.project.store.dir", "/appcom/tmp/wds/dss");
    public static final CommonVars<String> AZKABAN_LOGIN_PWD =
        CommonVars.apply("wds.dss.appjoint.scheduler.azkaban.login.passwd", "userpwd");

    public static final CommonVars<String> DATASOURCE_URL =
        CommonVars.apply("wds.linkis.server.mybatis.datasource.url", "");
    public static final CommonVars<String> DATASOURCE_USERNAME =
        CommonVars.apply("wds.linkis.server.mybatis.datasource.username", "");
    public static final CommonVars<String> DATASOURCE_PASSWORD =
        CommonVars.apply("wds.linkis.server.mybatis.datasource.password", "");
    public static final CommonVars<String> DATASOURCE_DRIVER_CLASS_NAME =
        CommonVars.apply("wds.linkis.server.mybatis.datasource.driver-class-name", "com.mysql.jdbc.Driver");
}
