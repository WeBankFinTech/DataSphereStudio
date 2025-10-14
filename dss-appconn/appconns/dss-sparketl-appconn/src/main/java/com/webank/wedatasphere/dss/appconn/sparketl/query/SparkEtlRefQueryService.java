package com.webank.wedatasphere.dss.appconn.sparketl.query;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryJumpUrlOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefQueryService;

public class SparkEtlRefQueryService extends AbstractRefQueryService {

    @Override
    protected RefQueryJumpUrlOperation createRefQueryOperation() {
        return new SparkEtlRefQueryJumpUrlOperation();
    }
}
