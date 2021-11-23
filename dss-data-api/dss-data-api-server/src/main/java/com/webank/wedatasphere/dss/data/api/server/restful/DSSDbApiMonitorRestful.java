package com.webank.wedatasphere.dss.data.api.server.restful;

import com.webank.wedatasphere.dss.data.api.server.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.data.api.server.entity.request.SingleCallMonitorRequest;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.data.api.server.service.ApiManagerService;
import com.webank.wedatasphere.dss.data.api.server.service.ApiMonitorService;
import com.webank.wedatasphere.dss.data.api.server.util.TimeUtil;
import org.apache.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname DSSDbApiMonitorRestful
 * @Description 服务管理--API监控
 * @Date 2021/7/20 11:57
 * @Created by suyc
 */

@Component
@Path("/dss/data/api/apimonitor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSDbApiMonitorRestful {
    @Autowired
    private ApiMonitorService apiMonitorService;
    @Autowired
    private ApiManagerService apiManagerService;

    @GET
    @Path("/list")
    public Response getApiList(@Context HttpServletRequest request,
                               @QueryParam("workspaceId") Long workspaceId, @QueryParam("apiName") String apiName,
                               @QueryParam("pageNow") Integer pageNow, @QueryParam("pageSize") Integer pageSize){
        if(pageNow == null){
            pageNow = 1;
        }
        if(pageSize == null){
            pageSize = 20;
        }

        List<Long> totals = new ArrayList<>();
        List<ApiInfo> apiInfoList = apiManagerService.getOnlineApiInfoList(workspaceId,apiName,totals,pageNow,pageSize);
        return Message.messageToResponse(Message.ok().data("list",apiInfoList).data("total", totals.get(0)));
    }

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
    public Response getCallTotalCnt(@BeanParam CallMonitorResquest callMonitorResquest){
        return Message.messageToResponse(Message.ok().data("callTotalCnt",apiMonitorService.getCallTotalCnt(callMonitorResquest)));
    }

    @GET
    @Path("callTotalTime")
    public Response getCallTotalTime(@BeanParam CallMonitorResquest callMonitorResquest){
        return Message.messageToResponse(Message.ok().data("callTotalTime",apiMonitorService.getCallTotalTime(callMonitorResquest)));
    }

    @GET
    @Path("callListByCnt")
    public Response getCallListByCnt(@BeanParam CallMonitorResquest callMonitorResquest){
        return Message.messageToResponse(Message.ok().data("list",apiMonitorService.getCallListByCnt(callMonitorResquest)));
    }

    @GET
    @Path("callListByFailRate")
    public Response getCallListByFailRate(@BeanParam CallMonitorResquest callMonitorResquest){
        return Message.messageToResponse(Message.ok().data("list",apiMonitorService.getCallListByFailRate(callMonitorResquest)));
    }


    /**
     * 过去24小时内每小时请求次数（平均QPS）
     */
    @GET
    @Path("callCntForPast24H")
    public Response getCallCntForPast24H(@QueryParam("workspaceId") Long workspaceId) throws Exception {
        return Message.messageToResponse(Message.ok().data("list",apiMonitorService.getCallCntForPast24H(workspaceId)));
    }

    /**
     * 单个API每小时的平均响应时间
     */
    @GET
    @Path("callTimeForSinleApi")
    public Response getCallTimeForSinleApi(@BeanParam SingleCallMonitorRequest singleCallMonitorRequest) throws Exception {
        long hourCnt = TimeUtil.getHourCnt(singleCallMonitorRequest.getStartTime(),singleCallMonitorRequest.getEndTime());
        singleCallMonitorRequest.setHourCnt(hourCnt);

        return Message.messageToResponse(Message.ok().data("list",apiMonitorService.getCallTimeForSinleApi(singleCallMonitorRequest)));
    }

    /**
     *单个API每小时的调用次数
     */
    @GET
    @Path("callCntForSinleApi")
    public Response getCallCntForSinleApi(@BeanParam SingleCallMonitorRequest singleCallMonitorRequest) throws Exception {
        long hourCnt = TimeUtil.getHourCnt(singleCallMonitorRequest.getStartTime(),singleCallMonitorRequest.getEndTime());
        singleCallMonitorRequest.setHourCnt(hourCnt);

        return Message.messageToResponse(Message.ok().data("list",apiMonitorService.getCallCntForSinleApi(singleCallMonitorRequest)));
    }
}
