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

package com.webank.wedatasphere.dss.framework.dbapi.restful;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiConfig;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiGroup;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.framework.dbapi.service.DSSApiConfigService;

import com.webank.wedatasphere.linkis.server.Message;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Component
@Path("/dss/framework/dbapi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSDbApiConfigController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSDbApiConfigController.class);
    @Autowired
    DSSApiConfigService apiConfigService;

    /**
     * 保存api配置信息
     *
     * @param request
     * @param apiConfig
     * @return
     */
    @POST
    @Path("save")
    public Response saveApi(@Context HttpServletRequest request, ApiConfig apiConfig) throws JSONException {
//        String username = SecurityFilter.getLoginUsername(request);
        apiConfigService.saveApi(apiConfig);
        Message message = Message.ok("创建API成功");
        return Message.messageToResponse(message);
    }


    @POST
    @Path("/group/create")
    public Response saveApi(ApiGroup apiGroup) {
//        String username = SecurityFilter.getLoginUsername(request);
        String userName = "demo";
        apiGroup.setCreateBy(userName);
        apiConfigService.addGroup(apiGroup);
        Message message = Message.ok("创建API group 成功").data("groupId", apiGroup.getId());
        return Message.messageToResponse(message);
    }

    @GET
    @Path("/list")
    public Response getApiList(@QueryParam("workspaceId") String workspaceId) {
        List<ApiGroupInfo> list = apiConfigService.getGroupList(workspaceId);
        Message message = Message.ok("获取API列表成功").data("list", list);
        return Message.messageToResponse(message);
    }


    @GET
    @Path("/detail")
    public Response getApiDetail(@QueryParam("apiId") int apiId) {
        ApiConfig apiConfig = apiConfigService.getById(apiId);
        Message message = Message.ok("获取API详情成功").data("detail", apiConfig);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("/test/{path}")
    public Response testApi(@Context HttpServletRequest request, @PathParam("path") String path) {
        ApiConfig apiConfig = apiConfigService.getOne(new QueryWrapper<ApiConfig>().eq("api_path",path));
        if(apiConfig != null){
            String reqFields = apiConfig.getReqFields();
            String sqlText = apiConfig.getSql();
        }

        Message message = Message.ok("获取API详情成功").data("detail", apiConfig);
        return Message.messageToResponse(message);
    }

}
