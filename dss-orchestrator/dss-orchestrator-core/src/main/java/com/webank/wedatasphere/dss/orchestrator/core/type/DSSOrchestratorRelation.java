package com.webank.wedatasphere.dss.orchestrator.core.type;

import com.webank.wedatasphere.dss.appconn.core.AppConn;

import java.util.function.Predicate;

/**
 * @author enjoyyin
 * @date 2022-03-20
 * @since 1.1.0
 */
public interface DSSOrchestratorRelation {

    String getDSSOrchestratorMode();

    String getDSSOrchestratorName();

    String getDSSOrchestratorName_CN();

    /**
     * 绑定的 AppConn
     * @return 绑定的 AppConn
     */
    String getBindingAppConnName();

    String getBindingSchedulerAppConnName();

    Predicate<AppConn> isLinkedAppConn();

}
