package com.webank.wedatasphere.dss.datamodel.center.common.config;

import org.apache.linkis.common.conf.CommonVars;

public class LinkisJobConfiguration {
    public static final CommonVars<String> LINKIS_SERVER_URL = CommonVars.apply("wds.wedatasphere.linkis.serverurl", "");
    public static final CommonVars<Long> CONNECTION_TIMEOUT = CommonVars.apply("wds.wedatasphere.linkis.connection.timeout", 30000L);
    public static final CommonVars<Boolean> DISCOVERY_ENABLED = CommonVars.apply("wds.wedatasphere.linkis.discovery.enabled", false);
    public static final CommonVars<Long> DISCOVERY_FREQUENCY_PERIOD = CommonVars.apply("wds.wedatasphere.linkis.discoveryfrequency.period", 1L);
    public static final CommonVars<Boolean> LOAD_BALANCER_ENABLED = CommonVars.apply("wds.wedatasphere.linkis.loadbalancer.enabled", true);
    public static final CommonVars<Integer> MAX_CONNECTION_SIZE = CommonVars.apply("wds.wedatasphere.linkis.maxconnection.size", 5);
    public static final CommonVars<Boolean> RETRY_ENABLED = CommonVars.apply("wds.wedatasphere.linkis.retryenabled", false);
    public static final CommonVars<Long> READ_TIMEOUT = CommonVars.apply("wds.wedatasphere.linkis.readtimeout", 30000L);
    public static final CommonVars<String> AUTHENTICATION_STRATEGY = CommonVars.apply("wds.wedatasphere.linkis.authenticationStrategy", "");

    public static final CommonVars<String> AUTHTOKEN_KEY = CommonVars.apply("wds.wedatasphere.linkis.authtoken.key", "");
    public static final CommonVars<String> AUTHTOKEN_VALUE = CommonVars.apply("wds.wedatasphere.linkis.authtoken.value", "");
    public static final CommonVars<String> DWS_VERSION = CommonVars.apply("wds.wedatasphere.linkis.dws.version", "");

}
