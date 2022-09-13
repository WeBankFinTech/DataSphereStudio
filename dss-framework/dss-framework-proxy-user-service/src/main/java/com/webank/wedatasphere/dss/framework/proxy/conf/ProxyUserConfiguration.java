package com.webank.wedatasphere.dss.framework.proxy.conf;

import org.apache.linkis.common.conf.CommonVars;

/**
 * @author enjoyyin
 * @date 2022-09-05
 * @since 0.5.0
 */
public class ProxyUserConfiguration {

    public static final CommonVars<String> DS_TRUST_TOKEN = CommonVars.apply("wds.dss.trust.token", "");
    public static final CommonVars<Boolean> DS_PROXY_SELF_ENABLE = CommonVars.apply("wds.dss.proxy.self.enable", true);

}
