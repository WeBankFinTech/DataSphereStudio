package com.webank.wedatasphere.dss.data.api.server.restful;

import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.data.api.server.exception.DataApiException;
import com.webank.wedatasphere.dss.data.api.server.service.ApiManagerService;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
@Path("/dss/data/api/apimanager")
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
    @Path("/offline/{apiId}")
    public Response offlineApi(@PathParam("apiId") Long apiId){
        apiManagerService.offlineApi(apiId);
        ApiInfo apiInfo = apiManagerService.getApiInfo(apiId);

        Message message = Message.ok("下线API成功").data("apiInfo",apiInfo);
        return Message.messageToResponse(message);
    }

    @POST
    @Path("/online/{apiId}")
    public Response onlineApi(@PathParam("apiId") Long apiId) throws DataApiException {


        ApiInfo apiInfo = apiManagerService.getApiInfo(apiId);
        if(apiInfo.getIsTest() == 0){
            throw new DataApiException("请测试通过后再上线");
        }

        if(apiInfo.getStatus() == 1){
            throw new DataApiException("该Api已发布,请勿重复发布");
        }

        apiManagerService.onlineApi(apiId);
        apiInfo = apiManagerService.getApiInfo(apiId);
        Message message = Message.ok("上线API成功").data("apiInfo",apiInfo);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("/callPath/{apiId}")
    public Response getApiCallPath(@PathParam("apiId") Long apiId){
        StringBuilder callPath =new StringBuilder("{protocol}://{host}");
        callPath.append("/api/rest_j/v1/dss/data/api/execute");
        ApiInfo apiInfo = apiManagerService.getApiInfo(apiId);
        callPath.append("/" + apiInfo.getApiPath());

        Message message = Message.ok().data("callPathPrefix", callPath.toString());
        return Message.messageToResponse(message);
    }


}
