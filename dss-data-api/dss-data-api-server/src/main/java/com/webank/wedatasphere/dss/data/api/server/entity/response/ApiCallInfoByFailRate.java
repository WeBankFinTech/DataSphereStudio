package com.webank.wedatasphere.dss.data.api.server.entity.response;

import lombok.Data;

/**
 * @Classname ApiCallInfoByFailRate
 * @Description TODO
 * @Date 2021/7/22 19:12
 * @Created by suyc
 */
@Data
public class ApiCallInfoByFailRate {
    private Long id;
    private String apiName;

    private Long totalCnt;
    private Long failCnt;
    private Long failRate;
}
