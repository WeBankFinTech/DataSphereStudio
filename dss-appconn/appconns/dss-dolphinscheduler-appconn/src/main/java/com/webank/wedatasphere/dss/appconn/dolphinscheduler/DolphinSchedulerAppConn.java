package com.webank.wedatasphere.dss.appconn.dolphinscheduler;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard.DolphinSchedulerStructureStandard;
import com.webank.wedatasphere.dss.appconn.schedule.core.SchedulerAppConn;
import com.webank.wedatasphere.dss.standard.app.sso.origin.OriginSSOIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Dolphin scheduler app conn.
 *
 * @author yuxin.yuan
 * @date 2021/04/23
 */
public class DolphinSchedulerAppConn implements SchedulerAppConn {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerAppConn.class);

    private final List<AppStandard> standards = new ArrayList<>();

    private AppDesc appDesc;

    public DolphinSchedulerAppConn() {
        init();
    }

    public void init() {
        //        standards.add(OriginSSOIntegrationStandard.getSSOIntegrationStandard());
        standards.add(DolphinSchedulerStructureStandard.getInstance(this));
    }

    @Override
    public StructureIntegrationStandard getStructureIntegrationStandard() {
        for (AppStandard appStandard : standards) {
            if (appStandard instanceof DolphinSchedulerStructureStandard) {
                return (StructureIntegrationStandard)appStandard;
            }
        }
        return null;
    }

    @Override
    public OriginSSOIntegrationStandard getOriginSSOIntegrationStandard() {
        for (AppStandard appStandard : standards) {
            if (appStandard instanceof OriginSSOIntegrationStandard) {
                return (OriginSSOIntegrationStandard)appStandard;
            }
        }
        return null;
    }

    @Override
    public List<AppStandard> getAppStandards() {
        return this.standards;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appDesc;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public boolean supportFlowSchedule() {
        return true;
    }

}
