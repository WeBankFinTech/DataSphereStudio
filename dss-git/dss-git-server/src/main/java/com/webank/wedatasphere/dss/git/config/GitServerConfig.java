package com.webank.wedatasphere.dss.git.config;

import org.apache.linkis.common.conf.CommonVars;

public class GitServerConfig {
    public static final CommonVars<String> GIT_SERVER_PATH = CommonVars.apply("wds.dss.git.server.path", "/data/GitInstall");

    public static final CommonVars<String> GIT_RESTFUL_API_CREATE_PROJECTS = CommonVars.apply("wds.dss.git.server.rest.create.project", "api/v4/projects/");

    public static final CommonVars<String> GIT_URL_PRE = CommonVars.apply("wds.dss.git.url", ***REMOVED***

    public static final CommonVars<Integer> GIT_THREAD_NUM = CommonVars.apply("wds.dss.git.thread.num", 23);

    public static final CommonVars<String> GIT_SERVER_META_PATH = CommonVars.apply("wds.dss.git.server.meta.path", ".metaConf");

    public static final CommonVars<Integer> GIT_SEARCH_RESULT_LIMIT = CommonVars.apply("wds.dss.git.search.limit", 30);

    public static final CommonVars<String> GIT_THREAD_NAME = CommonVars.apply("wds.dss.git.thread.name", "gitServer-Thread-");
}
