package com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity;

import com.webank.wedatasphere.dss.appconn.schedule.core.entity.AbstractSchedulerProject;

/**
 * The type Dolphin scheduler project.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class DolphinSchedulerProject extends AbstractSchedulerProject {

    private String creator;

    @Override
    public String getCreateBy() {
        return creator;
    }

    @Override
    public void setCreateBy(String creator) {
        this.creator = creator;
    }
}
