package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectCreationOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectDeletionOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUrlOperation;
import com.webank.wedatasphere.dss.standard.common.service.Operation;

/**
 * The type Dolphin scheduler project service.
 *
 * @author yuxin.yuan
 * @date 2021/10/18
 */
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
    protected ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }

}
