package com.webank.wedatasphere.dss.flow.execution.entrance.conf;

import org.apache.linkis.common.conf.CommonVars;

/**
 * Author: xlinliu
 * Date: 2023/8/25
 */
public interface FlowExecutionConf {
    CommonVars<Boolean> DSS_EXECUTE_BY_PROXY_USER_ENABLE = CommonVars.apply("wds.dss.flowexecution.execute.byproxyuser.enable", Boolean.FALSE);
}
