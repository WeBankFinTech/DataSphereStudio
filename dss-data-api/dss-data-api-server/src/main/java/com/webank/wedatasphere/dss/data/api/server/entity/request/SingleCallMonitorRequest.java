package com.webank.wedatasphere.dss.data.api.server.entity.request;

import lombok.Data;

import javax.ws.rs.QueryParam;

/**
 * @Classname SingleCallMonitorRequest
 * @Description 针对单个API的监控请求参数
 * @Date 2021/8/10 11:03
 * @Created by suyc
 */
@Data
public class SingleCallMonitorRequest {
    @QueryParam("apiId")
    private Long apiId;

    @QueryParam("startTime")
    private String startTime;


    @QueryParam("endTime")
    private String endTime;

    private Long hourCnt;
}
