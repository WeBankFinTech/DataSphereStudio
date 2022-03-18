package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.*;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerTokenManager;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;

public class DolphinSchedulerProjectService extends ProjectService {

    public DolphinSchedulerProjectService() {
        DolphinSchedulerTokenManager.getDolphinSchedulerTokenManager(getAppInstance().getBaseUrl())
                .setSSORequestOperation(getSSORequestService()
                        .createSSORequestOperation(DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME));
    }

    @Override
    protected ProjectCreationOperation createProjectCreationOperation() {
        return new DolphinSchedulerProjectCreationOperation();
    }

    @Override
    protected ProjectUpdateOperation createProjectUpdateOperation() {
        return new DolphinSchedulerProjectUpdateOperation();
    }

    @Override
    protected ProjectDeletionOperation createProjectDeletionOperation() {
        return new DolphinSchedulerProjectDeletionOperation();
    }

    @Override
    protected ProjectSearchOperation createProjectSearchOperation() {
        return new DolphinSchedulerProjectSearchOperation();
    }

    public DolphinSchedulerProjectGrantOperation getProjectGrantOperation() {
        return this.getOrCreate(DolphinSchedulerProjectGrantOperation::new, DolphinSchedulerProjectGrantOperation.class);
    }

    public DolphinSchedulerTokenGetOperation getDolphinSchedulerTokenGetOperation() {
        return this.getOrCreate(DolphinSchedulerTokenGetOperation::new, DolphinSchedulerTokenGetOperation.class);
    }

}
