package com.webank.wedatasphere.dss.appjoint.scheduler.airflow;

import com.webank.wedatasphere.dss.appjoint.scheduler.SchedulerAppJoint;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.conf.AirflowConf;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.constant.AirflowConstant;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.hooks.LinkisAirflowProjectPublishHook;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.parser.AirflowProjectParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.tuning.AirflowProjectTuning;
import com.webank.wedatasphere.dss.appjoint.scheduler.hooks.ProjectPublishHook;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.ProjectParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerProjectService;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerSecurityService;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.service.AirflowProjectService;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.service.AirflowSecurityService;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.ProjectTuning;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */

public final class AirflowSchedulerAppJoint extends AppJointUrlImpl implements SchedulerAppJoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(AirflowSchedulerAppJoint.class);

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
            basicUrl = String.format("http://%s:%s",
                    AirflowConf.AIRFLOW_HOST.getValue(),
                    AirflowConf.AIRFLOW_PORT.getValue());
            LOGGER.warn("basic url in db is empty,read it from conf{}",basicUrl);
        }
        securityService = new AirflowSecurityService();
        securityService.setBaseUrl(basicUrl);

        projectService = new AirflowProjectService();
        projectService.setBaseUrl(basicUrl);
    }

    @Override
    public SchedulerSecurityService getSecurityService() {
        return this.securityService;
    }

    @Override
    public ProjectParser getProjectParser() {
        return new AirflowProjectParser();
    }

    @Override
    public ProjectTuning getProjectTuning() {
        return new AirflowProjectTuning();
    }

    @Override
    public ProjectPublishHook[] getProjectPublishHooks() {
        ProjectPublishHook[] projectPublishHooks = {new LinkisAirflowProjectPublishHook()};
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
