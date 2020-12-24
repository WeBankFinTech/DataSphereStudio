package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity;


import com.webank.wedatasphere.dss.appjoint.scheduler.entity.AbstractSchedulerNode;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */
public class AirflowSchedulerNode extends AbstractSchedulerNode {

    private String storePath;

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

}
