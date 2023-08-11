package com.webank.wedatasphere.dss.appconn.sparketl.standard;

import com.webank.wedatasphere.dss.appconn.sparketl.execution.SparkEtlExecutionService;
import com.webank.wedatasphere.dss.appconn.sparketl.query.SparkEtlRefQueryService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefQueryService;
import com.webank.wedatasphere.dss.standard.app.development.standard.OnlyExecutionDevelopmentStandard;

public class SparkEtlDevelopmentStandard extends OnlyExecutionDevelopmentStandard {

    @Override
    protected RefExecutionService createRefExecutionService() {
        return new SparkEtlExecutionService();
    }

    @Override
    protected RefQueryService createRefQueryService() {
        return new SparkEtlRefQueryService();
    }

    @Override
    public void init() {
    }

    @Override
    public String getStandardName() {
        return "SparkEtlDevelopmentStandard";
    }
}
