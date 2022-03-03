package com.webank.wedatasphere.dss.data.governance.conf;


import org.apache.linkis.common.conf.CommonVars;

public class DataWorkspaceRemoteConfig {
    public static final CommonVars<String> SERVER_URL = CommonVars.apply("wds.workspace.client.serverurl", "");
    public static final CommonVars<Long> CONNECTION_TIMEOUT = CommonVars.apply("wds.workspace.client.connection.timeout", 30000L);
    public static final CommonVars<Boolean> DISCOVERY_ENABLED = CommonVars.apply("wds.workspace.client.discovery.enabled", false);
    public static final CommonVars<Long> DISCOVERY_FREQUENCY_PERIOD = CommonVars.apply("wds.workspace.client.discoveryfrequency.period", 1L);
    public static final CommonVars<Boolean> LOAD_BALANCER_ENABLED = CommonVars.apply("wds.workspace.client.loadbalancer.enabled", true);
    public static final CommonVars<Integer> MAX_CONNECTION_SIZE = CommonVars.apply("wds.workspace.client.maxconnection.size", 5);
    public static final CommonVars<Boolean> RETRY_ENABLED = CommonVars.apply("wds.workspace.client.retryenabled", false);
    public static final CommonVars<Long> READ_TIMEOUT = CommonVars.apply("wds.workspace.client.readtimeout", 30000L);
    public static final CommonVars<String> AUTHENTICATION_STRATEGY = CommonVars.apply("wds.workspace.client.authenticationStrategy", "");

    public static final CommonVars<String> AUTHTOKEN_KEY = CommonVars.apply("wds.workspace.client.authtoken.key", "");
    public static final CommonVars<String> AUTHTOKEN_VALUE = CommonVars.apply("wds.workspace.client.authtoken.value", "");
    public static final CommonVars<String> DWS_VERSION = CommonVars.apply("wds.workspace.client.dws.version", "");

}
