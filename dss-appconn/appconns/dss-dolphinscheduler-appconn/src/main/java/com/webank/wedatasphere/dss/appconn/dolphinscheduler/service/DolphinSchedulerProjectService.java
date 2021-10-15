package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProcessDefinitionQueryOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectCreationOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectDeletionOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
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

/**
 * The type Dolphin scheduler project service.
 *
 * @author yuxin.yuan
 * @date 2021/05/18
 */
public class DolphinSchedulerProjectService implements ProjectService {

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
        operationMap.put(DolphinSchedulerProjectCreationOperation.class,
            new DolphinSchedulerProjectCreationOperation(this));
        operationMap.put(RefQueryOperation.class, new DolphinSchedulerProcessDefinitionQueryOperation(this.appDesc));
        operationMap.put(DolphinSchedulerProjectUpdateOperation.class,
            new DolphinSchedulerProjectUpdateOperation(this));
        operationMap.put(DolphinSchedulerProjectDeletionOperation.class,
            new DolphinSchedulerProjectDeletionOperation(this));
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
        if (operationMap.containsKey(DolphinSchedulerProjectUpdateOperation.class)) {
            return (DolphinSchedulerProjectUpdateOperation)operationMap
                .get(DolphinSchedulerProjectUpdateOperation.class);
        } else {
            operationMap.put(DolphinSchedulerProjectUpdateOperation.class,
                new DolphinSchedulerProjectUpdateOperation(this));
            return (DolphinSchedulerProjectUpdateOperation)operationMap
                .get(DolphinSchedulerProjectUpdateOperation.class);
        }
    }

    @Override
    public ProjectDeletionOperation createProjectDeletionOperation() {
        if (operationMap.containsKey(DolphinSchedulerProjectDeletionOperation.class)) {
            return (DolphinSchedulerProjectDeletionOperation)operationMap
                .get(DolphinSchedulerProjectDeletionOperation.class);
        } else {
            operationMap.put(DolphinSchedulerProjectDeletionOperation.class,
                new DolphinSchedulerProjectDeletionOperation(this));
            return (DolphinSchedulerProjectDeletionOperation)operationMap
                .get(DolphinSchedulerProjectDeletionOperation.class);
        }
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
