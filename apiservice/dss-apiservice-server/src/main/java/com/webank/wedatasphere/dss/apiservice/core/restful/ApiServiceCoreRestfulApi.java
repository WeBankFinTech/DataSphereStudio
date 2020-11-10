/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.dss.apiservice.core.restful;

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiServiceQueryService;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiServiceService;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceQuery;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.TestParamVo;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * api service
 *
 * @author zhulixin
 */
@Path("/apiservice")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class ApiServiceCoreRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceCoreRestfulApi.class);
    
    @Autowired
    private ApiServiceService apiServiceService;
    
    @Autowired
    private ApiServiceQueryService apiServiceQueryService;

    @Autowired
    private Validator beanValidator;

    private static final Pattern WRITABLE_PATTERN = Pattern.compile("^\\s*(insert|update|delete|drop|alter|create).*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @POST
    @Path("/api")
    public Response insert(ApiServiceVo apiService, @Context HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(apiService.getScriptPath())) {
                return Message.error("'api service script path' is missing[缺少脚本路径]");
            }
            if (StringUtils.isBlank(apiService.getContent())) {
                return Message.error("'api service script content' is missing[缺少脚本内容]");
            }

            // check data change script
            if (WRITABLE_PATTERN.matcher(apiService.getContent()).matches()) {
                return Message.error("'api service script content' only supports query[脚本内容只支持查询语句]");
            }

            Map<String, Object> metadata = apiService.getMetadata();
            if (apiService.getScriptPath().endsWith(".jdbc")) {
                if (MapUtils.isEmpty(metadata)) {
                    return Message.error("'api service metadata' is missing[请选择数据源]");
                }

                Map<String, Object> configuration = (Map<String, Object>) metadata.get("configuration");
                if (MapUtils.isEmpty(configuration)) {
                    return Message.error("'api service metadata.configuration' is missing[请选择数据源]");
                }

                Map<String, Object> datasource = (Map<String, Object>) configuration.get("datasource");
                if (MapUtils.isEmpty(datasource)) {
                    return Message.error("'api service metadata.configuration.datasource' is missing[请选择数据源]");
                }
            }

            String userName = SecurityFilter.getLoginUsername(req);
            Set<ConstraintViolation<ApiServiceVo>> result = beanValidator.validate(apiService, Default.class);
            if (result.size() > 0) {
                throw new ConstraintViolationException(result);
            }
            apiService.setCreator(userName);
            apiService.setModifier(userName);
            apiServiceService.save(apiService);
            return Message.ok().data("insert_id", apiService.getId());
        }, "/apiservice/api", "Fail to insert service api[新增服务api失败]");
    }

    @PUT
    @Path("/api/{one_service_id}")
    public Response update(ApiServiceVo apiService,
                           @PathParam("one_service_id") Long oneServiceId,
                           @Context HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(apiService.getScriptPath())) {
                return Message.error("'api service script path' is missing[缺少脚本路径]");
            }
            if (StringUtils.isBlank(apiService.getPath())) {
                return Message.error("'api service api path' is missing[缺少api路径]");
            }
            if (StringUtils.isBlank(apiService.getContent())) {
                return Message.error("'api service script content' is missing[缺少脚本内容]");
            }
            if (StringUtils.isBlank(apiService.getResourceId())) {
                return Message.error("'api service resourceId' is missing[缺少bml resourceId]");
            }

            // check data change script
            if (WRITABLE_PATTERN.matcher(apiService.getContent()).matches()) {
                return Message.error("'api service script content' only supports query[脚本内容只支持查询语句]");
            }

            Map<String, Object> metadata = apiService.getMetadata();
            if (apiService.getScriptPath().endsWith(".jdbc")) {
                if (MapUtils.isEmpty(metadata)) {
                    return Message.error("'api service metadata' is missing[请选择数据源]");
                }

                Map<String, Object> configuration = (Map<String, Object>) metadata.get("configuration");
                if (MapUtils.isEmpty(configuration)) {
                    return Message.error("'api service metadata.configuration' is missing[请选择数据源]");
                }

                Map<String, Object> datasource = (Map<String, Object>) configuration.get("datasource");
                if (MapUtils.isEmpty(datasource)) {
                    return Message.error("'api service metadata.configuration.datasource' is missing[请选择数据源]");
                }
            }

            String userName = SecurityFilter.getLoginUsername(req);
            //Bean validation
            Set<ConstraintViolation<ApiServiceVo>> result = beanValidator.validate(apiService, Default.class);
            if (result.size() > 0) {
                throw new ConstraintViolationException(result);
            }
            apiService.setId(oneServiceId);
            apiService.setModifier(userName);
//            apiService.setModifyTime(Calendar.getInstance().getTime());
            apiServiceService.update(apiService);
            return Message.ok().data("update_id", oneServiceId);
        }, "/apiservice/api/" + oneServiceId, "Fail to update service api[更新服务api失败]");
    }

    @GET
    @Path("/search")
    public Response query(@QueryParam("name") String name,
                                    @QueryParam("tag") String tag,
                                    @QueryParam("status") Integer status,
                                    @QueryParam("creator") String creator,
                                    @QueryParam("currentPage") Integer currentPage,
                                    @QueryParam("pageSize") Integer pageSize) {
        return ApiUtils.doAndResponse(() -> {
//            if (StringUtils.isBlank(name)) {
//                return Message.error("'api service name' is missing[缺少服务api名称]");
//            }
            ApiServiceQuery query = new ApiServiceQuery(name, tag, status, creator);
            query.setCurrentPage(null != currentPage ? currentPage : 1);
            query.setPageSize(null != pageSize ? pageSize : 10);
            PageInfo<ApiServiceVo> queryList = apiServiceService.query(query);
            return Message.ok().data("query_list", queryList.getList()).data("total", queryList.getTotal());
        }, "/apiservice/api", "Fail to query page of service api[查询服务api失败]");
    }

    @GET
    @Path("/query")
    public Response queryByScriptPath(@QueryParam("scriptPath") String scriptPath) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(scriptPath)) {
                return Message.error("'api service scriptPath' is missing[缺少脚本路径]");
            }
            ApiServiceVo apiServiceVo = apiServiceService.queryByScriptPath(scriptPath);
            return Message.ok().data("result", apiServiceVo);
        }, "/apiservice/search", "Fail to query page of service api[查询服务api失败]");
    }

    @GET
    @Path("/checkPath")
    public Response checkPath(@QueryParam("scriptPath") String scriptPath, @QueryParam("path") String path) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(scriptPath)) {
                return Message.error("'api service scriptPath' is missing[缺少api脚本路径]");
            }
            if (StringUtils.isBlank(path)) {
                return Message.error("'api service path' is missing[缺少api路径]");
            }
            Integer apiCount = apiServiceService.queryCountByPath(scriptPath, path);
            return Message.ok().data("result", 0 > Integer.valueOf(0).compareTo(apiCount));
        }, "/apiservice/checkPath", "Fail to check path of service api[校验服务api路径失败]");
    }

    @GET
    @Path("/checkName")
    public Response checkName(@QueryParam("name") String name) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(name)) {
                return Message.error("'api service name' is missing[缺少api名称]");
            }
            Integer count = apiServiceService.queryCountByName(name);
            return Message.ok().data("result", count > 0);
        }, "/apiservice/checkName", "Fail to check name of service api[校验服务api名称失败]");
    }

    @GET
    @Path("/apiDisable")
    public Response apiDisable(@QueryParam("id") Integer id) {
        return ApiUtils.doAndResponse(() -> {
            if (null == id) {
                return Message.error("'api service api id' is missing[缺少api id]");
            }
            boolean resultFlag = apiServiceService.disableApi(id);
            return Message.ok().data("result", resultFlag);
        }, "/apiservice/apiDisable", "Fail to disable api[禁用api失败]");
    }

    @GET
    @Path("/apiEnable")
    public Response apiEnable(@QueryParam("id") Integer id) {
        return ApiUtils.doAndResponse(() -> {
            if (null == id) {
                return Message.error("'api service api id' is missing[缺少api id]");
            }
            boolean resultFlag = apiServiceService.enableApi(id);
            return Message.ok().data("result", resultFlag);
        }, "/apiservice/apiEnable", "Fail to enable api[启用api失败]");
    }

    @GET
    @Path("/apiParamQuery")
    public Response apiParamQuery(@QueryParam("scriptPath") String scriptPath, @QueryParam("version") String version) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isEmpty(scriptPath)) {
                return Message.error("'api service api scriptPath' is missing[缺少api scriptPath]");
            }
            if (StringUtils.isEmpty(version)) {
                return Message.error("'api service api version' is missing[缺少api 版本]");
            }
            List<TestParamVo> testParamVoList = apiServiceQueryService.query(scriptPath, version);
            return Message.ok().data("result", testParamVoList);
        }, "/apiservice/apiParamQuery", "Fail to query api info[查询api信息失败]");
    }

    @GET
    @Path("/apiVersionQuery")
    public Response apiVersionQuery(@QueryParam("scriptPath") String scriptPath) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isEmpty(scriptPath)) {
                return Message.error("'api service api scriptPath' is missing[缺少api scriptPath]");
            }
            List<ApiVersionVo> apiVersionVoList = apiServiceQueryService.queryApiVersion(scriptPath);
            return Message.ok().data("result", apiVersionVoList);
        }, "/apiservice/apiVersionQuery", "Fail to query api version[查询api版本失败]");
    }
}
