package com.webank.wedatasphere.linkis.oneservice.core.restful;

import com.alibaba.fastjson.JSON;
import com.webank.wedatasphere.linkis.oneservice.core.service.OneServiceQueryService;
import com.webank.wedatasphere.linkis.oneservice.core.util.AssertUtil;
import com.webank.wedatasphere.linkis.oneservice.core.vo.MessageExtVo;
import com.webank.wedatasphere.linkis.oneservice.core.vo.MessageVo;
import com.webank.wedatasphere.linkis.oneservice.core.vo.QueryRequest;
import com.webank.wedatasphere.linkis.oneservice.core.vo.VariableString;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * OneServiceRestfulApi
 *
 * @author lidongzhang
 */
@Path("/oneservice")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class OneServiceQueryRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(OneServiceQueryRestfulApi.class);

    private static final String SYS_COLUMN_PREFIX = "_";

    private static final String requestBodyDemo = "{\"moduleName\":\"aladdin-demo\",\"params\":{\"param1\": \"value1\"}}";

    @Autowired
    private OneServiceQueryService queryService;

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
        LOG.info("get params raw:{}",
                JSON.toJSONString(queryRequest.getParams()));
        // 如果requestBody为空，尝试从url中获取参数
        Enumeration <String> parameterNames = req.getParameterNames();
        while(parameterNames.hasMoreElements()){
            String paramName = parameterNames.nextElement();
            if(!StringUtils.isEmpty(paramName)) {
                String value = req.getParameter(paramName);
                if("moduleName".equals(paramName)){
                    queryRequest.setModuleName(value);
                }else{
                    Map<String,Object> paramsMap= JSON.parseObject(value);
                    queryRequest.setParams(paramsMap);
                }
            }
        }


        LOG.info("get params:{}", JSON.toJSONString(queryRequest.getParams()));
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
