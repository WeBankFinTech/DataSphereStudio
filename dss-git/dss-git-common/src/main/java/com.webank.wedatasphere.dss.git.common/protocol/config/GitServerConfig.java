package com.webank.wedatasphere.dss.git.common.protocol.config;

import org.apache.linkis.common.conf.CommonVars;

public class GitServerConfig {
    public static final CommonVars<String> GIT_SERVER_PATH = CommonVars.apply("wds.dss.git.server.path", "/data/GitInstall");

    public static final CommonVars<String> GIT_RESTFUL_API_CREATE_PROJECTS = CommonVars.apply("wds.dss.git.server.rest.create.project", "api/v4/projects/");

    public static final CommonVars<String> GIT_URL_PRE = CommonVars.apply("wds.dss.git.url", "http://***REMOVED***/");

    public static final CommonVars<Integer> GIT_THREAD_NUM = CommonVars.apply("wds.dss.git.thread.num", 23);

    public static final CommonVars<Integer> GIT_SEARCH_RESULT_LIMIT = CommonVars.apply("wds.dss.git.search.limit", 30);

    public static final CommonVars<String> GIT_USER = CommonVars.apply("wds.dss.server.git.user.uiid", "user_login");
    public static final CommonVars<String> GIT_PASSWD = CommonVars.apply("wds.dss.server.git.password.uiid", "user_password");
    public static final CommonVars<String> GIT_SUBMIT = CommonVars.apply("wds.dss.server.git.submit.uiid", "input[class='btn btn-success qa-sign-in-button']");
    public static final CommonVars<String> GIT_TIME = CommonVars.apply("wds.dss.server.git.time", "1");

    public static final CommonVars<String> GIT_SEARCH_EXCLUDE_DIRECTORY = CommonVars.apply("wds.dss.server.git.search.exclude.dir", ".metaConf");

    public static final CommonVars<String> GIT_SEARCH_EXCLUDE_FILE = CommonVars.apply("wds.dss.server.git.search.exclude.file", ".properties,.projectmeta");

    public static final CommonVars<String> LINKIS_MYSQL_PRI_KEY = CommonVars.apply("wds.linkis.mysql.pri.key", "abc");
}
