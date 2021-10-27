package com.webank.wedatasphere.dss.datamodel.center.common.config;


import com.webank.wedatasphere.linkis.common.conf.CommonVars;

public class DataWarehouseAssetsRemoteConfig {
    public static final CommonVars<String> SERVER_URL = CommonVars.apply("wds.wedatasphere.assets.client.serverurl", "");
    public static final CommonVars<Long> CONNECTION_TIMEOUT = CommonVars.apply("wds.wedatasphere.assets.client.connection.timeout", 30000L);
    public static final CommonVars<Boolean> DISCOVERY_ENABLED = CommonVars.apply("wds.wedatasphere.assets.client.discovery.enabled", false);
    public static final CommonVars<Long> DISCOVERY_FREQUENCY_PERIOD = CommonVars.apply("wds.wedatasphere.assets.client.discoveryfrequency.period", 1L);
    public static final CommonVars<Boolean> LOAD_BALANCER_ENABLED = CommonVars.apply("wds.wedatasphere.assets.client.loadbalancer.enabled", true);
    public static final CommonVars<Integer> MAX_CONNECTION_SIZE = CommonVars.apply("wds.wedatasphere.assets.client.maxconnection.size", 5);
    public static final CommonVars<Boolean> RETRY_ENABLED = CommonVars.apply("wds.wedatasphere.assets.client.retryenabled", false);
    public static final CommonVars<Long> READ_TIMEOUT = CommonVars.apply("wds.wedatasphere.assets.client.readtimeout", 30000L);
    public static final CommonVars<String> AUTHENTICATION_STRATEGY = CommonVars.apply("wds.wedatasphere.assets.client.authenticationStrategy", "");

    public static final CommonVars<String> AUTHTOKEN_KEY = CommonVars.apply("wds.wedatasphere.assets.client.authtoken.key", "");
    public static final CommonVars<String> AUTHTOKEN_VALUE = CommonVars.apply("wds.wedatasphere.assets.client.authtoken.value", "");
    public static final CommonVars<String> DWS_VERSION = CommonVars.apply("wds.wedatasphere.assets.client.dws.version", "");

}
