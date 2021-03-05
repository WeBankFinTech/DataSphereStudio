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
    public static final  String LOCAL_USER_ROOT_PATH = CommonVars.apply("wds.dss.user.root.dir","null").getValue().trim();
    public static final String BDP_SERVER_MYBATIS_DATASOURCE_URL = CommonVars.apply("wds.linkis.server.mybatis.datasource.url", "null").getValue().trim();
    public static final String BDP_SERVER_MYBATIS_DATASOURCE_USERNAME =  CommonVars.apply("wds.linkis.server.mybatis.datasource.username", "null").getValue().trim();
    public static final String BDP_SERVER_MYBATIS_DATASOURCE_PASSWORD = CommonVars.apply("wds.linkis.server.mybatis.datasource.password", "null").getValue().trim();
    public static final String SCHEDULER_ADDRESS = CommonVars.apply("wds.dss.appjoint.scheduler.azkaban.address", "null").getValue().trim();
    public static final String USER_ACCOUNT_COMMANDS = CommonVars.apply("wds.dss.user.account.command.class", "null").getValue().trim();

    public static final String METASTORE_HDFS_PATH = CommonVars.apply("wds.linkis.metastore.hive.hdfs.base.path", "null").getValue().trim();
    public static final String METASTORE_SCRIPT_PAHT = CommonVars.apply("wds.linkis.metastore.script.path", "null").getValue().trim();
    public static final String METASTORE_DB_TAIL = CommonVars.apply("wds.linkis.metastore.db.tail", "_default").getValue().trim();

    public static final String KERBEROS_REALM = CommonVars.apply("wds.linkis.kerberos.realm", "null").getValue().trim();
    public static final String KERBEROS_ADMIN = CommonVars.apply("wds.linkis.kerberos.admin", "null").getValue().trim();
    public static final String KERBEROS_SCRIPT_PATH = CommonVars.apply("wds.linkis.kerberos.script.path", "null").getValue().trim();
    public static final String KERBEROS_KEYTAB_PATH = CommonVars.apply("wds.linkis.kerberos.keytab.path", "null").getValue().trim();
    public static final String KERBEROS_SSH_PORT = CommonVars.apply("wds.linkis.kerberos.ssh.port", "22").getValue().trim();
    public static final String KERBEROS_KDC_NODE = CommonVars.apply("wds.linkis.kerberos.kdc.node", "null").getValue().trim();
    public static final String KERBEROS_KDC_USER_NAME = CommonVars.apply("wds.linkis.kerberos.kdc.user.name", "null").getValue().trim();
    public static final String KERBEROS_KDC_USER_PASSWORD = CommonVars.apply("wds.linkis.kerberos.kdc.user.password", "null").getValue().trim();
    public static final String KERBEROS_ENABLE_SWITCH = CommonVars.apply("wds.linkis.kerberos.enable.switch", "null").getValue().trim();
    public static final String DSS_DEPLOY_PATH = CommonVars.apply("wds.dss.deploy.path", "null").getValue().trim();
    public static final String DSS_SCHEDULER_URL = CommonVars.apply("wds.dss.scheduler.url", "/luban/schedule/system").getValue().trim();



}
