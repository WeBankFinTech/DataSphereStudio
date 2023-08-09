package com.webank.wedatasphere.dss.appconn.sparketl.standard;

import com.webank.wedatasphere.dss.appconn.sparketl.execution.SparkEtlExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.standard.OnlyExecutionDevelopmentStandard;

public class SparkEtlDevelopmentStandard extends OnlyExecutionDevelopmentStandard {

    @Override
    protected RefExecutionService createRefExecutionService() {
        return new SparkEtlExecutionService();
    }

    @Override
    public void init() {
    }

    @Override
    public String getStandardName() {
        return "SparkEtlDevelopmentStandard";
    }
}
