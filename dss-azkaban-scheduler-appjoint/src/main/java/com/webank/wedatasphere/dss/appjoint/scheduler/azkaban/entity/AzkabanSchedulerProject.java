package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity;

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.AbstractSchedulerProject;

/**
 * Created by cooperyang on  2019/9/19
 */
public class AzkabanSchedulerProject extends AbstractSchedulerProject {

    private String storePath;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

}
