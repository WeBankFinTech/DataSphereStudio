package com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;


/**
 * The type Dolphin scheduler structure standard.
 *
 * @author yuxin.yuan
 * @date 2021/10/18
 */
public class DolphinSchedulerStructureStandard extends AbstractStructureIntegrationStandard {

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
}
