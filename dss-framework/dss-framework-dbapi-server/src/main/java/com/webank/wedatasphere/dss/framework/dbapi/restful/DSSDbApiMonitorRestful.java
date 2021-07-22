package com.webank.wedatasphere.dss.framework.dbapi.restful;

import com.webank.wedatasphere.dss.framework.dbapi.service.ApiMonitorService;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
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
@Consumes(MediaType.APPLICATION_JSON)
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

    //总调用次数

    //总调用时长


}
