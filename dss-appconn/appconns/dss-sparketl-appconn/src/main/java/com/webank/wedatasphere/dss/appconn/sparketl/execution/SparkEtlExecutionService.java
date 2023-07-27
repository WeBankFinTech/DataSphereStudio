package com.webank.wedatasphere.dss.appconn.sparketl.execution;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefExecutionService;

public class SparkEtlExecutionService extends AbstractRefExecutionService {

    @Override
    public RefExecutionOperation createRefExecutionOperation() {
        return new SparkEtlRefExecutionOperation();
    }

}
