package com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerOrchestrationService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerProjectService;
import com.webank.wedatasphere.dss.appconn.scheduler.AbstractSchedulerStructureIntegrationStandard;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;

public class DolphinSchedulerStructureStandard extends AbstractSchedulerStructureIntegrationStandard {

    private static volatile DolphinSchedulerStructureStandard instance;


    private DolphinSchedulerStructureStandard() {
    }

    public static DolphinSchedulerStructureStandard getInstance() {
        if (instance == null) {
            synchronized (DolphinSchedulerStructureStandard.class) {
                if (instance == null) {
                    instance = new DolphinSchedulerStructureStandard();
                }
            }
        }
        return instance;
    }

    @Override
    protected ProjectService createProjectService() {
        return new DolphinSchedulerProjectService();
    }

    @Override
    protected OrchestrationService createOrchestrationService() {
        return new DolphinSchedulerOrchestrationService();
    }
}
