package com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 0.5.0
 */
public class DolphinSchedulerDataResponseRef extends ResponseRefImpl {

    private Object data;

    public DolphinSchedulerDataResponseRef(String responseBody, int status,
                                           String errorMsg,
                                           Map<String, Object> responseMap,
                                           Object data) {
        super(responseBody, status, errorMsg, responseMap);
        this.data = data;
    }

    public <T> T getData() {
        return (T) data;
    }

}
