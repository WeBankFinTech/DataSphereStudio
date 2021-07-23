package com.webank.wedatasphere.dss.framework.dbapi.restful;

import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiManagerService;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lenovo
 * @Classname DSSDbApiManagerRestful
 * @Description 服务管理--API管理
 * @Date 2021/7/19 15:38
 * @Created by suyc
 */

@Component
@Path("/dss/framework/dbapi/apimanager")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSDbApiManagerRestful {
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
        List<ApiInfo> apiInfoList = apiManagerService.getApiInfoList(workspaceId,apiName,totals,pageNow,pageSize);
        return Message.messageToResponse(Message.ok().data("list",apiInfoList).data("total", totals.get(0)));
    }

    @POST
    @Path("offline")
    public Response offlineApi(@Context HttpServletRequest request, @QueryParam("apiId") Long apiId){
        apiManagerService.offlineApi(apiId);

        Message message = Message.ok("下线API成功");
        return Message.messageToResponse(message);
    }

    @POST
    @Path("online")
    public Response onlineApi(@Context HttpServletRequest request, @QueryParam("apiId") Long apiId){
        apiManagerService.onlineApi(apiId);

        Message message = Message.ok("上线API成功");
        return Message.messageToResponse(message);
    }

    @GET
    @Path("callPath")
    public Response getApiCallPath(@QueryParam("apiId") Long apiId){
        StringBuilder callPath =new StringBuilder("http://xxxx");
        callPath.append("/api/rest_j/v1");

        Message message = Message.ok().data("callPathPrefix", callPath.toString());
        return Message.messageToResponse(message);
    }


}
