package com.webank.wedatasphere.dss.appconn.visualis.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

import java.util.Map;

import static com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils.getInt;

/**
 * @author enjoyyin
 * @date 2022-03-07
 * @since 0.5.0
 */
public class VisualisResponseRefBuilder
        extends ResponseRefBuilder.ExternalResponseRefBuilder<VisualisResponseRefBuilder, ResponseRefImpl> {

    @Override
    public VisualisResponseRefBuilder setResponseBody(String responseBody) {
        Map<String, Object> headerMap = (Map<String, Object>) responseMap.get("header");
        if (headerMap.containsKey("code")) {
            status = getInt(headerMap.get("code"));
            if (status != 0 && status != 200) {
                errorMsg = headerMap.get("msg").toString();
            }
        }
        Object payload = responseMap.get("payload");
        if(payload instanceof Map) {
            setResponseMap((Map<String, Object>) payload);
        }
        return super.setResponseBody(responseBody);
    }

}
