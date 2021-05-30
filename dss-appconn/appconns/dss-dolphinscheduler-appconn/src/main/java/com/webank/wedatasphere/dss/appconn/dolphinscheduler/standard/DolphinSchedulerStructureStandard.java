package com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerRefSchedulerService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerProjectService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerRoleService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.ScheduleFlowService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.SchedulerInfoService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.ServiceBuilder;
import com.webank.wedatasphere.dss.appconn.schedule.core.standard.SchedulerStructureStandard;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefSchedulerService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.role.RoleService;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The type Dolphin scheduler structure standard.
 *
 * @author yuxin.yuan
 * @date 2021/04/30
 */
public class DolphinSchedulerStructureStandard implements SchedulerStructureStandard {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerStructureStandard.class);

    private volatile static DolphinSchedulerStructureStandard instance;

    private AppConn appConn;

    private DolphinSchedulerStructureStandard(AppConn appConn) {
        this.appConn = appConn;
        try {
            init();
        } catch (AppStandardErrorException e) {
            logger.error("Failed to init {}", this.getClass().getSimpleName(), e);
        }
    }

    public static DolphinSchedulerStructureStandard getInstance(AppConn appConn) {
        if (instance == null) {
            synchronized (DolphinSchedulerStructureStandard.class) {
                if (instance == null) {
                    instance = new DolphinSchedulerStructureStandard(appConn);
                }
            }
        }
        return instance;
    }

    @Override
    public RoleService getRoleService() {
        AppDesc appDesc = appConn.getAppDesc();
        return (RoleService)ServiceBuilder.getInstance().
            getDolphinSchedulerStructureService(appDesc.getAppInstances().get(0), appDesc, this,
                DolphinSchedulerRoleService.class);
    }

    @Override
    public ProjectService getProjectService() {
        AppDesc appDesc = appConn.getAppDesc();
        return (ProjectService)ServiceBuilder.getInstance().
            getDolphinSchedulerStructureService(appDesc.getAppInstances().get(0), appDesc, this,
                DolphinSchedulerProjectService.class);
    }

    @Override
    public AppStatusService getAppStateService() {
        AppDesc appDesc = appConn.getAppDesc();
        return (AppStatusService)ServiceBuilder.getInstance().
            getDolphinSchedulerStructureService(null, appDesc, this, DolphinSchedulerRoleService.class);
    }

    @Override
    public String getStandardName() {
        return null;
    }

    @Override
    public int getGrade() {
        return 0;
    }

    @Override
    public boolean isNecessary() {
        return false;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appConn.getAppDesc();
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {

    }

    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public RefSchedulerService getSchedulerService() {
        return new DolphinSchedulerRefSchedulerService();
    }

    public SchedulerInfoService getSchedulerInfoService() {
        return SchedulerInfoService.getInstance(this.getAppDesc());
    }

    public ScheduleFlowService getScheduleFlowService() {
        return ScheduleFlowService.getInstance(this.getAppDesc());
    }

}
