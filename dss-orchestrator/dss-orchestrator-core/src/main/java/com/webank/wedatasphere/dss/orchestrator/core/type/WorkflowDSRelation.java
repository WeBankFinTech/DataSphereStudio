package com.webank.wedatasphere.dss.orchestrator.core.type;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;

import java.util.function.Predicate;

public class WorkflowDSRelation implements DSSOrchestratorRelation {

    @Override
    public String getDSSOrchestratorMode() {
        return "pom_work_flow_ds";
    }

    @Override
    public String getDSSOrchestratorName() {
        return "workflow_ds";
    }

    @Override
    public String getDSSOrchestratorName_CN() {
        return "DS工作流";
    }

    @Override
    public String getBindingAppConnName() {
        return "workflow";
    }

    @Override
    public String getBindingSchedulerAppConnName() {
        return "dolphinscheduler";
    }

    @Override
    public Predicate<AppConn> isLinkedAppConn() {
        return appConn -> appConn instanceof OnlyDevelopmentAppConn;
    }
}
