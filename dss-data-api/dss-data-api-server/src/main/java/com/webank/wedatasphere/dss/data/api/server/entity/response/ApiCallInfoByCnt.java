package com.webank.wedatasphere.dss.data.api.server.entity.response;

import lombok.Data;

/**
 * @Classname ApiCallInfoByCnt
 * @Description TODO
 * @Date 2021/7/22 18:48
 * @Created by suyc
 */
@Data
public class ApiCallInfoByCnt {
    private Long id;
    private String apiName;

    private Long totalCnt;
    private Long totalTime;
    private Long avgTime;
}
