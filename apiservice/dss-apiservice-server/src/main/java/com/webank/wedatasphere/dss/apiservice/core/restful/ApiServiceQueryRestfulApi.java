/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.webank.wedatasphere.dss.apiservice.core.restful;

import com.webank.wedatasphere.dss.apiservice.core.util.AssertUtil;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiServiceQueryService;
import com.webank.wedatasphere.dss.apiservice.core.vo.MessageExtVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.MessageVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.QueryRequest;
import com.webank.wedatasphere.dss.apiservice.core.vo.VariableString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ApiServiceRestfulApi
 *
 * @author lidongzhang
 */
@Path("/apiservice")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class ApiServiceQueryRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceQueryRestfulApi.class);

    private static final String SYS_COLUMN_PREFIX = "_";

    private static final String requestBodyDemo = "{\"moduleName\":\"aladdin-demo\",\"params\":{\"param1\": \"value1\"}}";

    @Autowired
    private ApiServiceQueryService queryService;

    @POST
    @Path("/execute/{path:.*}")
    public Response post(@PathParam("path") VariableString path, @RequestBody QueryRequest queryRequest,
                         @Context HttpServletRequest req) {
        return getResponse(path.getPath(), queryRequest, HttpMethod.POST);
    }

    @GET
    @Path("/execute/{path:.*}")
    public Response get(@PathParam("path") VariableString path, @RequestBody QueryRequest queryRequest,
                        @Context HttpServletRequest req) {
        if (queryRequest == null) {
            queryRequest = new QueryRequest();
        }

        // 如果requestBody为空，尝试从url中获取参数
        if (MapUtils.isEmpty(queryRequest.getParams())) {
            String paramSuffix = "params.";
            Enumeration<String> parameterNames = req.getParameterNames();
            Map<String, Object> params = new HashMap<>();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                if (name.startsWith(paramSuffix)) {
                    params.put(name.substring(paramSuffix.length()), req.getParameter(name));
                }
            }
            queryRequest.setParams(params);
        }

        if (StringUtils.isEmpty(queryRequest.getModuleName())) {
            queryRequest.setModuleName(req.getParameter("moduleName"));
        }

        return getResponse(path.getPath(), queryRequest, HttpMethod.GET);
    }

    @PUT
    @Path("/execute/{path:.*}")
    public Response put(@PathParam("path") VariableString path, @RequestBody QueryRequest queryRequest,
                        @Context HttpServletRequest req) {
        return getResponse(path.getPath(), queryRequest, HttpMethod.PUT);
    }

    @DELETE
    @Path("/execute/{path:.*}")
    public Response delete(@PathParam("path") VariableString path, @RequestBody QueryRequest queryRequest,
                           @Context HttpServletRequest req) {
        return getResponse(path.getPath(), queryRequest, HttpMethod.DELETE);
    }

    private void validParam(QueryRequest queryRequest) {
        AssertUtil.notNull(queryRequest, "请求体不能为空，正确的格式:" + requestBodyDemo);
        AssertUtil.notEmpty(queryRequest.getModuleName(), "moduleName不能为空，正确的格式:" + requestBodyDemo);
    }

    private Response getResponse(String path, QueryRequest queryRequest, String httpMethod) {
        return ApiUtils.doAndResponse(() -> {

            validParam(queryRequest);

            List<Map<String, Object>> query = queryService.query("/" + path,
                    queryRequest.getParams() == null ? new HashMap<>() : queryRequest.getParams(),
                    queryRequest.getModuleName(), httpMethod);

            MessageVo messageVo = new MessageVo().setData(query);

            if (CollectionUtils.isNotEmpty(query)) {
                Map<String, Object> row = query.get(0);
                // 包含下划线开头的字段，要把下划线开头的字段提到上一层
                if (row.keySet().stream().anyMatch(s -> s.startsWith(SYS_COLUMN_PREFIX))) {
                    MessageExtVo messageExtVo = MessageExtVo.build(messageVo);
                    Set<String> sysCols = new HashSet<>();

                    row.forEach((k, v) -> {
                        if (k.startsWith(SYS_COLUMN_PREFIX)) {
                            messageExtVo.put(k.substring(1), v);
                            sysCols.add(k);
                        }
                    });
                    for (Map<String, Object> r : query) {
                        for (String sysCol : sysCols) {
                            r.remove(sysCol);
                        }
                    }
                    return messageExtVo;
                }
            }

            return messageVo;
        });
    }
}
