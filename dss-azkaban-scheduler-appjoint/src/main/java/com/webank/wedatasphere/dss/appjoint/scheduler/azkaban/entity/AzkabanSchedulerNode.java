package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity;


import com.webank.wedatasphere.dss.appjoint.scheduler.entity.AbstractSchedulerNode;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

/**
 * Created by v_wbbmwan on  2019/9/15
 */
public class AzkabanSchedulerNode extends AbstractSchedulerNode {

    private String storePath;

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

}
