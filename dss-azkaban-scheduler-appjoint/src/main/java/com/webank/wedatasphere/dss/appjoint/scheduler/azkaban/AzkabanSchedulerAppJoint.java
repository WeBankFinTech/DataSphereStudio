package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban;

import com.webank.wedatasphere.dss.appjoint.scheduler.SchedulerAppJoint;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.conf.AzkabanConf;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerProjectService;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerSecurityService;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.service.AzkabanProjectService;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.service.AzkabanSecurityService;
import com.webank.wedatasphere.dss.application.entity.Application;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

/**
 * Created by cooperyang on 2019/9/16.
 */
@Component
public final class AzkabanSchedulerAppJoint implements SchedulerAppJoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzkabanSchedulerAppJoint.class);

    private SchedulerSecurityService securityService;
    private SchedulerProjectService projectService;
    @Autowired
    private ApplicationService applicationService;

    @Override
    public String getAppJointName() {
        return "schedulis";
    }

    @PostConstruct
    public void beforeInit(){
        Application schedulis = applicationService.getApplication("schedulis");
        String basicUrl = null;
        if(schedulis != null){
            basicUrl = schedulis.getUrl();
        }
        LOGGER.info("read schedulerAppJoint url from db{}",basicUrl);
        if(StringUtils.isEmpty(basicUrl)){
            basicUrl = AzkabanConf.AZKABAN_BASE_URL.getValue();
            LOGGER.warn("basic url in db is empty,read it from conf{}",basicUrl);
        }
        init(basicUrl,null);
    }

    @Override
    public void init(String basicUrl, Map<String, Object> params) {
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
    public SchedulerProjectService getProjectService() {
        return this.projectService;
    }

    @Override
    public void close() throws IOException {

    }
}
