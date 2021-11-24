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

import com.webank.wedatasphere.dss.data.api.server.entity.ApiConfig;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiGroup;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiExecuteInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.data.api.server.exception.DataApiException;
import com.webank.wedatasphere.dss.data.api.server.service.ApiConfigService;
import com.webank.wedatasphere.dss.data.api.server.util.RestfulUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;


@Component
@Path("/dss/data/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class DSSDbApiConfigRestful {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSDbApiConfigRestful.class);
    @Autowired
    ApiConfigService apiConfigService;

    /**
     * 保存api配置信息
     * @param request
     * @param apiConfig
     * @return
     */
    @POST
    @Path("save")
    public Message saveApi(@Valid @RequestBody ApiConfig apiConfig, @Context HttpServletRequest request) throws JSONException, DataApiException {
        String username = SecurityFilter.getLoginUsername(request);
        apiConfig.setCreateBy(username);
        apiConfig.setUpdateBy(username);
        apiConfigService.saveApi(apiConfig);
        Message message = Message.ok();
        return Message.messageToResponse(message);
    }

    /**
     * 创建API 组
     * @param apiGroup
     * @return
     */
    @POST
    @Path("/group/create")
    public Message saveGroup(@Valid @RequestBody ApiGroup apiGroup, @Context HttpServletRequest request) {
        String username = SecurityFilter.getLoginUsername(request);
        apiGroup.setCreateBy(username);
        apiConfigService.addGroup(apiGroup);
        Message message = Message.ok().data("groupId", apiGroup.getId());
        return Message.messageToResponse(message);
    }

    /**
     * API list
     * @param workspaceId
     * @return
     */
    @GET
    @Path("/list")
    public Message getApiList(@QueryParam("workspaceId") String workspaceId) {
        List<ApiGroupInfo> list = apiConfigService.getGroupList(workspaceId);
        Message message = Message.ok().data("list", list);
        return Message.messageToResponse(message);
    }

    /**
     * 查询api详情
     * @param apiId
     * @return
     */

    @GET
    @Path("/detail")
    public Message getApiDetail(@QueryParam("apiId") int apiId) {
        ApiConfig apiConfig = apiConfigService.getById(apiId);
        Message message = Message.ok().data("detail", apiConfig);
        return Message.messageToResponse(message);
    }

    /**
     * 测试 API
     * @param request
     * @param path
     * @param map
     * @return
     */

    @POST
    @Path("/test/{path:[a-zA-Z0-9_/]+}")
    public Message testApi(@Context HttpServletRequest request, @PathParam("path") String path, Map<String, Object> map) {

        try {
            ApiExecuteInfo resJo = apiConfigService.apiTest(path, request, map,true);
            Message message = Message.ok().data("response", resJo);
            return Message.messageToResponse(message);
        } catch (Exception exception) {
            log.error("ERROR", "Error found: ", exception);
            return RestfulUtils.dealError(exception.getMessage());
        }

    }


    /**
     * 第三方调用 api
     * @param request
     * @param path
     * @param map
     * @return
     */
    @POST
    @Path("/execute/{path:[a-zA-Z0-9_/]+}")
    public Message executeApi(@Context HttpServletRequest request, @PathParam("path") String path, Map<String, Object> map) {
        try {
            ApiExecuteInfo resJo = apiConfigService.apiExecute(path, request, map);
            Message message = Message.ok().data("response", resJo);
            return Message.messageToResponse(message);
        } catch (Exception exception) {
            log.error("ERROR", "Error found: ", exception);
            return RestfulUtils.dealError(exception.getMessage());
        }

    }

}
