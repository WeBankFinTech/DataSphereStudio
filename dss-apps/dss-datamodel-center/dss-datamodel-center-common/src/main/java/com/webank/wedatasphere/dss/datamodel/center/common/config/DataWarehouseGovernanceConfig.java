package com.webank.wedatasphere.dss.datamodel.center.common.config;


import com.webank.wedatasphere.linkis.common.conf.CommonVars;

public class DataWarehouseGovernanceConfig {
    public static final CommonVars<String> SERVER_URL = CommonVars.apply("wds.wedatasphere.warehouse.client.serverurl", "");
    public static final CommonVars<Long> CONNECTION_TIMEOUT = CommonVars.apply("wds.wedatasphere.warehouse.client.connection.timeout", 30000L);
    public static final CommonVars<Boolean> DISCOVERY_ENABLED = CommonVars.apply("wds.wedatasphere.warehouse.client.discovery.enabled", true);
    public static final CommonVars<Long> DISCOVERY_FREQUENCY_PERIOD = CommonVars.apply("wds.wedatasphere.warehouse.client.discoveryfrequency.period", 1L);
    public static final CommonVars<Boolean> LOAD_BALANCER_ENABLED = CommonVars.apply("wds.wedatasphere.warehouse.client.loadbalancer.enabled", true);
    public static final CommonVars<Integer> MAX_CONNECTION_SIZE = CommonVars.apply("wds.wedatasphere.warehouse.client.maxconnection.size", 5);
    public static final CommonVars<Boolean> RETRY_ENABLED = CommonVars.apply("wds.wedatasphere.warehouse.client.retryenabled", false);
    public static final CommonVars<Long> READ_TIMEOUT = CommonVars.apply("wds.wedatasphere.warehouse.client.readtimeout", 30000L);
    public static final CommonVars<String> AUTHENTICATION_STRATEGY = CommonVars.apply("wds.wedatasphere.warehouse.client.authenticationStrategy", "com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy");

    public static final CommonVars<String> AUTHTOKEN_KEY = CommonVars.apply("wds.wedatasphere.warehouse.client.authtoken.key", "");
    public static final CommonVars<String> AUTHTOKEN_VALUE = CommonVars.apply("wds.wedatasphere.warehouse.client.authtoken.value", "");
    public static final CommonVars<String> DWS_VERSION = CommonVars.apply("wds.wedatasphere.warehouse.client.dws.version", "");

}
