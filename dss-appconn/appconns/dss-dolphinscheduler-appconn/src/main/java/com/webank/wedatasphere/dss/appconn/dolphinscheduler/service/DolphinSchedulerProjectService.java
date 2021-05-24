package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUrlOperation;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Dolphin scheduler project service.
 *
 * @author yuxin.yuan
 * @date 2021/05/18
 */
public class DolphinSchedulerProjectService implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerProjectService.class);

    private AppDesc appDesc;

    private AppInstance appInstance;

    private StructureIntegrationStandard structureIntegrationStandard;

    private Map<Class<? extends Operation>, Operation<?, ?>> operationMap = new ConcurrentHashMap<>();

    public DolphinSchedulerProjectService(AppDesc appDesc, AppInstance appInstance,
        StructureIntegrationStandard structureIntegrationStandard) {
        this.appDesc = appDesc;
        this.appInstance = appInstance;
        this.structureIntegrationStandard = structureIntegrationStandard;
        init();
    }

    private void init() {
        operationMap.put(DolphinSchedulerProjectCreationOperation.class, new DolphinSchedulerProjectCreationOperation(this));
        //        operationMap.put(SchedulisProjectUpdateOperation.class, new SchedulisProjectUpdateOperation(this));
        //        operationMap.put(SchedulisProjectDeletionOperation.class, new SchedulisProjectDeletionOperation(this));
    }

    @Override
    public ProjectCreationOperation createProjectCreationOperation() {
        if (operationMap.containsKey(DolphinSchedulerProjectCreationOperation.class)) {
            return (DolphinSchedulerProjectCreationOperation)operationMap.get(DolphinSchedulerProjectCreationOperation.class);
        } else {
            operationMap.put(DolphinSchedulerProjectCreationOperation.class, new DolphinSchedulerProjectCreationOperation(this));
            return (DolphinSchedulerProjectCreationOperation)operationMap.get(DolphinSchedulerProjectCreationOperation.class);
        }
    }

    @Override
    public ProjectUpdateOperation createProjectUpdateOperation() {
        //        if (operationMap.containsKey(SchedulisProjectUpdateOperation.class)) {
        //            return (SchedulisProjectUpdateOperation)operationMap.get(SchedulisProjectUpdateOperation.class);
        //        } else {
        //            operationMap.put(SchedulisProjectUpdateOperation.class, new SchedulisProjectUpdateOperation(this));
        //            return (SchedulisProjectUpdateOperation)operationMap.get(SchedulisProjectUpdateOperation.class);
        //        }
        return null;
    }

    @Override
    public ProjectDeletionOperation createProjectDeletionOperation() {
        //        if (operationMap.containsKey(SchedulisProjectDeletionOperation.class)) {
        //            return (SchedulisProjectDeletionOperation)operationMap.get(SchedulisProjectDeletionOperation.class);
        //        } else {
        //            operationMap.put(SchedulisProjectDeletionOperation.class, new SchedulisProjectDeletionOperation(this));
        //            return (SchedulisProjectDeletionOperation)operationMap.get(SchedulisProjectDeletionOperation.class);
        //        }
        return null;
    }

    @Override
    public ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }

    @Override
    public AppInstance getAppInstance() {
        return this.appInstance;
    }

    @Override
    public void setAppInstance(AppInstance appInstance) {
        this.appInstance = appInstance;
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
        this.structureIntegrationStandard = appStandard;
    }

    @Override
    public StructureIntegrationStandard getAppStandard() {
        return this.structureIntegrationStandard;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appDesc;
    }

    @Override
    public Operation createOperation(Class<? extends Operation> clazz) {
        return this.operationMap.get(clazz);
    }

    @Override
    public boolean isOperationExists(Class<? extends Operation> clazz) {
        return this.operationMap.containsKey(clazz);
    }

    @Override
    public boolean isOperationNecessary(Class<? extends Operation> clazz) {
        return isOperationExists(clazz);
    }
}
