package com.webank.wedatasphere.dss.data.api.server.entity.request;

import lombok.Data;

import javax.ws.rs.QueryParam;

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
