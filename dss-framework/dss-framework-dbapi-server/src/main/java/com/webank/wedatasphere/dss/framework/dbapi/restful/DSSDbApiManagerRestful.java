package com.webank.wedatasphere.dss.framework.dbapi.restful;

import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiAuthService;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
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
    private ApiAuthService apiAuthService;

    @GET
    @Path("/list")
    public Response getApiList(@Context HttpServletRequest request, @QueryParam("workspaceId") Long workspaceId,
                                       @QueryParam("pageNow") Integer pageNow, @QueryParam("pageSize") Integer pageSize){
        if(pageNow == null){
            pageNow = 1;
        }
        if(pageSize == null){
            pageSize = 20;
        }

        List<Long> totals = new ArrayList<>();
        List<ApiInfo> apiInfoList = apiAuthService.getApiInfoList(workspaceId,totals,pageNow,pageSize);
        return Message.messageToResponse(Message.ok().data("apiInfoList",apiInfoList).data("total", totals.get(0)));
    }
}
