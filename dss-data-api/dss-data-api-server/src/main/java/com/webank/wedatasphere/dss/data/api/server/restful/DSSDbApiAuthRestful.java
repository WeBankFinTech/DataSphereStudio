/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.data.api.server.restful;



import com.webank.wedatasphere.dss.data.api.server.entity.ApiAuth;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiAuthInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.data.api.server.service.ApiAuthService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Classname DSSDataAPIAuthRestful
 * @Description 服务管理--API授权
 * @Date 2021/7/14 10:44
 * @Created by suyc
 */
@Component
@Path("/dss/data/api/apiauth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class DSSDbApiAuthRestful {
    @Autowired
    private ApiAuthService apiAuthService;

    @POST
    @Path("/save")
    public Response saveApiAuth(@Context HttpServletRequest request, @RequestBody ApiAuth apiAuth) throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(request);
        if(apiAuth.getId() ==null) {
            String token = DigestUtils.md5Hex(UUID.randomUUID().toString());
            apiAuth.setToken(token);

            apiAuth.setCreateBy(userName);
            apiAuth.setCreateTime(new Date(System.currentTimeMillis()));
            apiAuth.setUpdateTime(new Date(System.currentTimeMillis()));
        }
        else{
            apiAuth.setUpdateBy(userName);
            apiAuth.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        boolean flag = apiAuthService.saveApiAuth(apiAuth);
        if(flag) {
            return Message.messageToResponse(Message.ok("保存成功"));
        }else{
            return Message.messageToResponse(Message.error("保存失败"));
        }
    }

    @GET
    @Path("/token")
    public Response generateToken( ) {
        String token = DigestUtils.md5Hex(UUID.randomUUID().toString());
        return Message.messageToResponse(Message.ok().data("token",token));
    }

    @GET
    @Path("/list")
    public Response getApiAuthList(@QueryParam("workspaceId") Long workspaceId, @QueryParam("caller") String caller,
                                   @QueryParam("pageNow") Integer pageNow, @QueryParam("pageSize") Integer pageSize){
        if(pageNow == null){
            pageNow = 1;
        }
        if(pageSize == null){
            pageSize = 20;
        }

        List<Long> totals = new ArrayList<>();
        List<ApiAuthInfo> apiAuths = apiAuthService.getApiAuthList(workspaceId,caller,totals,pageNow,pageSize);
        return Message.messageToResponse(Message.ok().data("list",apiAuths).data("total", totals.get(0)));
    }

    @POST
    @Path("/{id}")
    public Response deleteApiAuth(@PathParam("id") Long id){
        log.info("-------delete apiauth:    " + id + ", begin");
        apiAuthService.deleteApiAuth(id);

        Message message = Message.ok("删除成功");
        return Message.messageToResponse(message);
    }

    @GET
    @Path("/apigroup")
    public Response getApiGroup(@QueryParam("workspaceId") Long workspaceId){
        List<ApiGroupInfo> apiGroupInfoList = apiAuthService.getApiGroupList(workspaceId);

        Message message = Message.ok().data("list",apiGroupInfoList);
        return Message.messageToResponse(message);
    }
}

