package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectCreationOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectDeletionOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.common.service.Operation;

public class DolphinSchedulerProjectService extends ProjectService {

    private Map<Class<? extends Operation>, Operation<?, ?>> operationMap = new ConcurrentHashMap<>();

    private void init() {
        // operationMap.put(RefQueryOperation.class, new DolphinSchedulerProcessDefinitionQueryOperation(this.appDesc));
    }

    public DolphinSchedulerProjectService() {}

    @Override
    public ProjectCreationOperation createProjectCreationOperation() {
        return new DolphinSchedulerProjectCreationOperation();
    }

    @Override
    public ProjectUpdateOperation createProjectUpdateOperation() {
        return new DolphinSchedulerProjectUpdateOperation();
    }

    @Override
    public ProjectDeletionOperation createProjectDeletionOperation() {
        return new DolphinSchedulerProjectDeletionOperation();
    }

    @Override
    protected ProjectGetOperation createProjectGetOperation() {
        return null;
    }

    @Override
    protected ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }



}
