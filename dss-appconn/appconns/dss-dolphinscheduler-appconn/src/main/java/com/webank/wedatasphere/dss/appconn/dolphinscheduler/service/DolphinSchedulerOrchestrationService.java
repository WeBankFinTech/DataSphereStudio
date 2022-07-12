package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerWorkflowCreationOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerWorkflowDeletionOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerWorkflowSearchOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerWorkflowUpdateOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerTokenManager;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.*;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 0.5.0
 */
public class DolphinSchedulerOrchestrationService extends OrchestrationService {

    @Override
    public void setAppInstance(AppInstance appInstance) {
        super.setAppInstance(appInstance);
        DolphinSchedulerTokenManager.getDolphinSchedulerTokenManager(appInstance.getBaseUrl())
                .setSSORequestOperation(getSSORequestService()
                        .createSSORequestOperation(DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME));
    }

    @Override
    protected OrchestrationCreationOperation createOrchestrationCreationOperation() {
        return new DolphinSchedulerWorkflowCreationOperation();
    }

    @Override
    protected OrchestrationUpdateOperation createOrchestrationUpdateOperation() {
        return new DolphinSchedulerWorkflowUpdateOperation();
    }

    @Override
    protected OrchestrationDeletionOperation createOrchestrationDeletionOperation() {
        return new DolphinSchedulerWorkflowDeletionOperation();
    }

    @Override
    protected OrchestrationSearchOperation createOrchestrationSearchOperation() {
        return new DolphinSchedulerWorkflowSearchOperation();
    }
}
