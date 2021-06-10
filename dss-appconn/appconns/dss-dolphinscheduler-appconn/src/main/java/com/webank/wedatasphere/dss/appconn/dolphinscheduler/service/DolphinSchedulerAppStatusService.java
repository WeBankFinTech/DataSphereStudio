package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusOperation;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusService;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;

public class DolphinSchedulerAppStatusService implements AppStatusService {

    @Override
    public AppStatusOperation createAppStatusOperation() {
        return null;
    }

    @Override
    public void setSSOService(AppIntegrationService ssoService) {

    }

    @Override
    public AppIntegrationService getSSOService() {
        return null;
    }

    @Override
    public void setAppStandard(StructureIntegrationStandard appStandard) {

    }

    @Override
    public StructureIntegrationStandard getAppStandard() {
        return null;
    }

    @Override
    public AppInstance getAppInstance() {
        return null;
    }

    @Override
    public void setAppInstance(AppInstance appInstance) {

    }

    @Override
    public void setAppDesc(AppDesc appDesc) {

    }

    @Override
    public AppDesc getAppDesc() {
        return null;
    }

    @Override
    public Operation createOperation(Class<? extends Operation> clazz) {
        return null;
    }

    @Override
    public boolean isOperationExists(Class<? extends Operation> clazz) {
        return false;
    }

    @Override
    public boolean isOperationNecessary(Class<? extends Operation> clazz) {
        return false;
    }
}
