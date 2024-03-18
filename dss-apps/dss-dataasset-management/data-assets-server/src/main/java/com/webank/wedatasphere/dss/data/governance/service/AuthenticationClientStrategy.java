package com.webank.wedatasphere.dss.data.governance.service;



import com.webank.wedatasphere.dss.data.governance.conf.ClientStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.server.security.SecurityFilter;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationClientStrategy {

    CommonVars<String> CLIENT_STRATEGY = CommonVars.apply("wds.wedatasphere.data.assert.client.strategy", "");

    default String getStrategyUser(HttpServletRequest req){
        if (StringUtils.equals(ClientStrategy.TOKEN.getCode(),CLIENT_STRATEGY.getValue())){
            return SecurityFilter.getLoginUsername(req);
        }
        return "hdfs";
    }
}
