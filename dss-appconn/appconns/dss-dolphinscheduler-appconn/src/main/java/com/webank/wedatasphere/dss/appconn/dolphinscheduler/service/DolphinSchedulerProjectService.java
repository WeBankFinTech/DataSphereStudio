package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.*;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerTokenManager;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

public class DolphinSchedulerProjectService extends ProjectService {

    @Override
    public void setAppInstance(AppInstance appInstance) {
        super.setAppInstance(appInstance);
        DolphinSchedulerTokenManager.getDolphinSchedulerTokenManager(appInstance.getBaseUrl())
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

}
