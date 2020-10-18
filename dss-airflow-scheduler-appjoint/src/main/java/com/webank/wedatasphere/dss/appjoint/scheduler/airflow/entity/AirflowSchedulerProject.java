package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity;

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.AbstractSchedulerProject;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */
public class AirflowSchedulerProject extends AbstractSchedulerProject {

    private String storePath;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

}
