package com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;

import java.util.Map;

public class DolphinSchedulerInstanceResponseRef extends AbstractResponseRef {

    public DolphinSchedulerInstanceResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }
}
