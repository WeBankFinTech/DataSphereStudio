package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban;

import com.webank.wedatasphere.dss.appjoint.scheduler.SchedulerAppJoint;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.conf.AzkabanConf;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.hooks.LinkisAzkabanProjectPublishHook;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.parser.AzkabanProjectParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.tuning.AzkabanProjectTuning;
import com.webank.wedatasphere.dss.appjoint.scheduler.hooks.ProjectPublishHook;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.ProjectParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerProjectService;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerSecurityService;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.service.AzkabanProjectService;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.service.AzkabanSecurityService;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.ProjectTuning;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.Map;

/**
 * Created by cooperyang on 2019/9/16.
 */

public final class AzkabanSchedulerAppJoint extends AppJointUrlImpl implements SchedulerAppJoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzkabanSchedulerAppJoint.class);

    private SchedulerSecurityService securityService;
    private SchedulerProjectService projectService;
    private ApplicationService applicationService;

    @Override
    public String getAppJointName() {
        return "schedulis";
    }


    @Override
    public void init(String basicUrl, Map<String, Object> params) {
        LOGGER.info("read schedulerAppJoint url from db{}",basicUrl);
        if(StringUtils.isEmpty(basicUrl)){
            basicUrl = AzkabanConf.AZKABAN_BASE_URL.getValue();
            LOGGER.warn("basic url in db is empty,read it from conf{}",basicUrl);
        }
        securityService = new AzkabanSecurityService();
        securityService.setBaseUrl(basicUrl);

        projectService = new AzkabanProjectService();
        projectService.setBaseUrl(basicUrl);
    }

    @Override
    public SchedulerSecurityService getSecurityService() {
        return this.securityService;
    }

    @Override
    public ProjectParser getProjectParser() {
        return new AzkabanProjectParser();
    }

    @Override
    public ProjectTuning getProjectTuning() {
        return new AzkabanProjectTuning();
    }

    @Override
    public ProjectPublishHook[] getProjectPublishHooks() {
        ProjectPublishHook[] projectPublishHooks = {new LinkisAzkabanProjectPublishHook()};
        return projectPublishHooks;
    }

    @Override
    public SchedulerProjectService getProjectService() {
        return this.projectService;
    }

    @Override
    public void close() throws IOException {

    }
}
