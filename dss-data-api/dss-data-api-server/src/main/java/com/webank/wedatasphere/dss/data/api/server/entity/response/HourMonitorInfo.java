package com.webank.wedatasphere.dss.data.api.server.entity.response;

import lombok.Data;

/**
 * @Classname HourMonitorInfo
 * @Description TODO
 * @Date 2021/8/12 16:14
 * @Created by suyc
 */
@Data
public class HourMonitorInfo {
    private String key;
    private Object value;

    public HourMonitorInfo(String key,Object value){
        this.key = key;
        this.value = value;
    }
}
