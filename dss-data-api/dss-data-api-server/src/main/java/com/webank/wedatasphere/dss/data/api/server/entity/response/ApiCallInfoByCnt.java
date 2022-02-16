package com.webank.wedatasphere.dss.data.api.server.entity.response;

import lombok.Data;

@Data
public class ApiCallInfoByCnt {
    private Long id;
    private String apiName;

    private Long totalCnt;
    private Long totalTime;
    private Long avgTime;
}
