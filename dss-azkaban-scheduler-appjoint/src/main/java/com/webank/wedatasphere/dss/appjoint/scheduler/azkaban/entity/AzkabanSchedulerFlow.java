package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity;

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;

public class AzkabanSchedulerFlow extends SchedulerFlow {

    private String storePath;

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

}
