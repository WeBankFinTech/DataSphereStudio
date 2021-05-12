package com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.linkisjob.NodeConverter;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.AbstractSchedulerNode;

/**
 * The type Dolphin scheduler scheduler node.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class DolphinSchedulerSchedulerNode extends AbstractSchedulerNode {

    private String user;

    /**
     * 将DSS中SchedulerNode转为DolphinScheduler中节点.
     *
     * @param converter the converter
     * @return the dolphin scheduler task
     */
    public DolphinSchedulerTask toDolphinSchedulerTask(NodeConverter converter) {
        return converter.conversion(this);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
