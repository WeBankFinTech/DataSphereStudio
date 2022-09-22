package com.webank.wedatasphere.dss.framework.proxy.conf;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import org.apache.linkis.common.conf.CommonVars;

/**
 * @author enjoyyin
 * @date 2022-09-05
 * @since 0.5.0
 */
public class ProxyUserConfiguration {

    public static boolean isProxyUserEnable() {
        return CommonVars.apply(DSSCommonConf.ALL_GLOBAL_LIMITS_PREFIX.acquireNew() + "proxyEnable", false).acquireNew();
    }

    public static final CommonVars<String> DS_TRUST_TOKEN = CommonVars.apply("wds.dss.trust.token", "");
    public static final CommonVars<Boolean> DS_PROXY_SELF_ENABLE = CommonVars.apply("wds.dss.proxy.self.enable", true);

}
