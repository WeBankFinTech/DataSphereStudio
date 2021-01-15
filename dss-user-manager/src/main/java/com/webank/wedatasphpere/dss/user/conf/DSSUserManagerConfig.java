package com.webank.wedatasphpere.dss.user.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

import java.util.ResourceBundle;

/**
 * @program: dss-appjoint-auth
 * @description: 用户模块配置文件
 * @author: luxl@chinatelecom.cn
 * @create: 2020-12-30 16:26
 **/


public class DSSUserManagerConfig {
//    private final static ResourceBundle resource = ResourceBundle.getBundle("linkis");
    public static final  String LOCAL_USER_ROOT_PATH = CommonVars.apply("wds.dss.user.root.dir","").getValue();
    public static final String BDP_SERVER_LDAP_SCRIPT_SERVER = CommonVars.apply("wds.linkis.ldap.script.server", "").getValue();
    public static final String BDP_SERVER_LDAP_SCRIPT_ROOT = CommonVars.apply("wds.linkis.ldap.script.root", "").getValue();
    public static final String  BDP_SERVER_LDAP_SCRIPT = CommonVars.apply("wds.linkis.ldap.script", "").getValue();
    public static final String BDP_SERVER_MYBATIS_DATASOURCE_URL = CommonVars.apply("wds.linkis.server.mybatis.datasource.url", "").getValue();
    public static final String BDP_SERVER_MYBATIS_DATASOURCE_USERNAME =  CommonVars.apply("wds.linkis.server.mybatis.datasource.username", "").getValue();
    public static final String BDP_SERVER_MYBATIS_DATASOURCE_PASSWORD = CommonVars.apply("wds.linkis.server.mybatis.datasource.password", "").getValue();
    public static final String SCHEDULER_ADDRESS = CommonVars.apply("wds.dss.appjoint.scheduler.azkaban.address", "").getValue();
    public static final String USER_ACCOUNT_COMMANDS = CommonVars.apply("wds.dss.user.account.command.class", "").getValue();

}
