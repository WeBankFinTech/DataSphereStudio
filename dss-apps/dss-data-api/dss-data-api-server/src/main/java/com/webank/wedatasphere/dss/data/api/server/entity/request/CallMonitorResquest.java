package com.webank.wedatasphere.dss.data.api.server.entity.request;

import lombok.Data;

import javax.ws.rs.QueryParam;

@Data
public class CallMonitorResquest {
    @QueryParam("workspaceId")
    private Long workspaceId;

    @QueryParam("startTime")
    private String startTime;


    @QueryParam("endTime")
    private String endTime;

}
