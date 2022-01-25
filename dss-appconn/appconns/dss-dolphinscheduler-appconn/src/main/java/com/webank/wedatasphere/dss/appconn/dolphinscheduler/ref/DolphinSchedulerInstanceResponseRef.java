package com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;

public class DolphinSchedulerInstanceResponseRef extends AbstractResponseRef {

    public DolphinSchedulerInstanceResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }

    @Override
    public Map<String, Object> toMap() {
        if (StringUtils.isNotBlank(responseBody)) {
            responseMap = DSSCommonUtils.COMMON_GSON.fromJson(responseBody, Map.class);
        } else {
            responseMap = new HashMap<>();
        }
        return responseMap;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }
}
