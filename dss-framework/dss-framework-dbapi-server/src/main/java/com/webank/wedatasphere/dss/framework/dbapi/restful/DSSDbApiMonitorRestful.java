package com.webank.wedatasphere.dss.framework.dbapi.restful;

import com.webank.wedatasphere.dss.framework.dbapi.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiMonitorService;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @Classname DSSDbApiMonitorRestful
 * @Description 服务管理--API监控
 * @Date 2021/7/20 11:57
 * @Created by suyc
 */

@Component
@Path("/dss/framework/dbapi/apimonitor")
@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
public class DSSDbApiMonitorRestful {
    @Autowired
    private ApiMonitorService apiMonitorService;

    @GET
    @Path("onlineApiCnt")
    public Response getOnlineApiCnt(@QueryParam("workspaceId") Long workspaceId){
        return Message.messageToResponse(Message.ok().data("onlineApiCnt",apiMonitorService.getOnlineApiCnt(workspaceId)));
    }

    @GET
    @Path("offlineApiCnt")
    public Response getOfflineApiCnt(@QueryParam("workspaceId") Long workspaceId){
        return Message.messageToResponse(Message.ok().data("offlineApiCnt",apiMonitorService.getOfflineApiCnt(workspaceId)));
    }



    @GET
    @Path("callTotalCnt")
    public Response getCallTotalCnt(@QueryParam("workspaceId") Long workspaceId){
        return Message.messageToResponse(Message.ok().data("callTotalCnt",apiMonitorService.getCallTotalCnt(workspaceId)));
    }

    @GET
    @Path("callTotalTime")
    public Response getCallTotalTime(@QueryParam("workspaceId") Long workspaceId){
        return Message.messageToResponse(Message.ok().data("callTotalTime",apiMonitorService.getCallTotalTime(workspaceId)));
    }

    @GET
    @Path("callListByCnt")
    public Response getCallListByCnt(CallMonitorResquest callMonitorResquest){
        return Message.messageToResponse(Message.ok().data("list",apiMonitorService.getCallListByCnt(callMonitorResquest)));
    }

    @GET
    @Path("callListByFailRate")
    public Response getCallListByFailRate(CallMonitorResquest callMonitorResquest){
        return Message.messageToResponse(Message.ok().data("list",apiMonitorService.getCallListByFailRate(callMonitorResquest)));
    }




}
