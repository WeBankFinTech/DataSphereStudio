package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity;

import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.linkisjob.LinkisJobConverter;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ShareNode;

public class LinkisAirflowShareNode extends LinkisAirflowSchedulerNode implements ShareNode {
    private SchedulerNode schedulerNode;
    private int shareTimes;

    @Override
    public int getShareTimes() {
        return shareTimes;
    }

    @Override
    public void setShareTimes(int num) {
        this.shareTimes = num;
    }

    @Override
    public SchedulerNode getSchedulerNode() {
        return schedulerNode;
    }

    @Override
    public void setSchedulerNode(SchedulerNode node) {
        this.schedulerNode = node;
    }

    @Override
    public String toJobString(LinkisJobConverter converter) {
        return converter.conversion(this);
    }
}
