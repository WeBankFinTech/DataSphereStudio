package com.webank.wedatasphere.dss.framework.common.conf;

import org.apache.linkis.common.conf.CommonVars;

public class TokenConf {
    private TokenConf() {
        throw new IllegalStateException("Configution class");
    }
    public static final String HPMS_USER_TOKEN = CommonVars.apply("wds.dss.workspace.hpms.user.token", "HPMS-KhFGSQkdaaCPBYfE").getValue();

}
