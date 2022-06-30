package com.webank.wedatasphere.dss.standard.common.entity.ref;

import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-06
 * @since 0.5.0
 */
public class InternalResponseRef extends ResponseRefImpl {

    private Map<String, Object> data;
    private String message;

    public InternalResponseRef(String responseBody, int status, String errorMsg,
                               Map<String, Object> responseMap) {
        super(responseBody, status, errorMsg, responseMap);
        this.data = (Map<String, Object>) responseMap.get("data");
        this.message = (String) responseMap.get("message");
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
