package com.webank.wedatasphere.dss.standard.common.entity.ref;

import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-03
 * @since 0.5.0
 */
public class ResponseRefImpl implements ResponseRef {

    protected String responseBody;
    protected int status = -1;
    protected String errorMsg;
    protected Map<String, Object> responseMap;

    public ResponseRefImpl(String responseBody, int status, String errorMsg, Map<String, Object> responseMap) {
        this.responseBody = responseBody;
        this.status = status;
        this.errorMsg = errorMsg;
        this.responseMap = responseMap;
    }

    @Override
    public Map<String, Object> toMap() {
        return responseMap;
    }

    @Override
    public Object getValue(String key) {
        if(responseMap == null) {
            return null;
        } else {
            return responseMap.get(key);
        }
    }

    @Override
    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

}
