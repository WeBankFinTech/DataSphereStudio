package com.webank.wedatasphere.dss.data.api.server.entity.request;

import lombok.Data;

import javax.ws.rs.QueryParam;

/**
 * @Classname CallMonitorResquest
 * @Description TODO
 * @Date 2021/7/22 20:04
 * @Created by suyc
 */
@Data
public class CallMonitorResquest {
    @QueryParam("workspaceId")
    private Long workspaceId;

    @QueryParam("startTime")
    private String startTime;


    @QueryParam("endTime")
    private String endTime;

}
