package com.webank.wedatasphere.dss.data.api.server.entity.response;

import lombok.Data;

@Data
public class HourMonitorInfo {
    private String key;
    private Object value;

    public HourMonitorInfo(String key,Object value){
        this.key = key;
        this.value = value;
    }
}
