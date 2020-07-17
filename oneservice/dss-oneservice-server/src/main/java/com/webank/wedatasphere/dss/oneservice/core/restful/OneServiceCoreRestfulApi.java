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

package com.webank.wedatasphere.dss.oneservice.core.restful;

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.oneservice.core.service.OneServiceQueryService;
import com.webank.wedatasphere.dss.oneservice.core.service.OneServiceService;
import com.webank.wedatasphere.dss.oneservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceQuery;
import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceVo;
import com.webank.wedatasphere.dss.oneservice.core.vo.TestParamVo;
import com.webank.wedatasphere.dss.oneservice.core.service.OneServiceQueryService;
import com.webank.wedatasphere.dss.oneservice.core.service.OneServiceService;
import com.webank.wedatasphere.dss.oneservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceQuery;
import com.webank.wedatasphere.dss.oneservice.core.vo.OneServiceVo;
import com.webank.wedatasphere.dss.oneservice.core.vo.TestParamVo;
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
 * one service
 *
 * @author zhulixin
 */
@Path("/oneservice")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class OneServiceCoreRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(OneServiceCoreRestfulApi.class);
    
    @Autowired
    private OneServiceService oneServiceService;
    
    @Autowired
    private OneServiceQueryService oneServiceQueryService;

    @Autowired
    private Validator beanValidator;

    private static final Pattern WRITABLE_PATTERN = Pattern.compile("^\\s*(insert|update|delete|drop|alter|create).*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @POST
    @Path("/api")
    public Response insert(OneServiceVo oneService, @Context HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(oneService.getScriptPath())) {
                return Message.error("'one service script path' is missing[缺少脚本路径]");
            }
            if (StringUtils.isBlank(oneService.getContent())) {
                return Message.error("'one service script content' is missing[缺少脚本内容]");
            }

            // check data change script
            if (WRITABLE_PATTERN.matcher(oneService.getContent()).matches()) {
                return Message.error("'one service script content' only supports query[脚本内容只支持查询语句]");
            }

            Map<String, Object> metadata = oneService.getMetadata();
            if (oneService.getScriptPath().endsWith(".jdbc")) {
                if (MapUtils.isEmpty(metadata)) {
                    return Message.error("'one service metadata' is missing[请选择数据源]");
                }

                Map<String, Object> configuration = (Map<String, Object>) metadata.get("configuration");
                if (MapUtils.isEmpty(configuration)) {
                    return Message.error("'one service metadata.configuration' is missing[请选择数据源]");
                }

                Map<String, Object> datasource = (Map<String, Object>) configuration.get("datasource");
                if (MapUtils.isEmpty(datasource)) {
                    return Message.error("'one service metadata.configuration.datasource' is missing[请选择数据源]");
                }
            }

            String userName = SecurityFilter.getLoginUsername(req);
            Set<ConstraintViolation<OneServiceVo>> result = beanValidator.validate(oneService, Default.class);
            if (result.size() > 0) {
                throw new ConstraintViolationException(result);
            }
            oneService.setCreator(userName);
            oneService.setModifier(userName);
            oneServiceService.save(oneService);
            return Message.ok().data("insert_id", oneService.getId());
        }, "/oneservice/api", "Fail to insert service api[新增服务api失败]");
    }

    @PUT
    @Path("/api/{one_service_id}")
    public Response update(OneServiceVo oneService,
                                           @PathParam("one_service_id") Long oneServiceId,
                                           @Context HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(oneService.getScriptPath())) {
                return Message.error("'one service script path' is missing[缺少脚本路径]");
            }
            if (StringUtils.isBlank(oneService.getPath())) {
                return Message.error("'one service api path' is missing[缺少api路径]");
            }
            if (StringUtils.isBlank(oneService.getContent())) {
                return Message.error("'one service script content' is missing[缺少脚本内容]");
            }
            if (StringUtils.isBlank(oneService.getResourceId())) {
                return Message.error("'one service resourceId' is missing[缺少bml resourceId]");
            }

            // check data change script
            if (WRITABLE_PATTERN.matcher(oneService.getContent()).matches()) {
                return Message.error("'one service script content' only supports query[脚本内容只支持查询语句]");
            }

            Map<String, Object> metadata = oneService.getMetadata();
            if (oneService.getScriptPath().endsWith(".jdbc")) {
                if (MapUtils.isEmpty(metadata)) {
                    return Message.error("'one service metadata' is missing[请选择数据源]");
                }

                Map<String, Object> configuration = (Map<String, Object>) metadata.get("configuration");
                if (MapUtils.isEmpty(configuration)) {
                    return Message.error("'one service metadata.configuration' is missing[请选择数据源]");
                }

                Map<String, Object> datasource = (Map<String, Object>) configuration.get("datasource");
                if (MapUtils.isEmpty(datasource)) {
                    return Message.error("'one service metadata.configuration.datasource' is missing[请选择数据源]");
                }
            }

            String userName = SecurityFilter.getLoginUsername(req);
            //Bean validation
            Set<ConstraintViolation<OneServiceVo>> result = beanValidator.validate(oneService, Default.class);
            if (result.size() > 0) {
                throw new ConstraintViolationException(result);
            }
            oneService.setId(oneServiceId);
            oneService.setModifier(userName);
//            oneService.setModifyTime(Calendar.getInstance().getTime());
            oneServiceService.update(oneService);
            return Message.ok().data("update_id", oneServiceId);
        }, "/oneservice/api/" + oneServiceId, "Fail to update service api[更新服务api失败]");
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
//                return Message.error("'one service name' is missing[缺少服务api名称]");
//            }
            OneServiceQuery query = new OneServiceQuery(name, tag, status, creator);
            query.setCurrentPage(null != currentPage ? currentPage : 1);
            query.setPageSize(null != pageSize ? pageSize : 10);
            PageInfo<OneServiceVo> queryList = oneServiceService.query(query);
            return Message.ok().data("query_list", queryList.getList()).data("total", queryList.getTotal());
        }, "/oneservice/api", "Fail to query page of service api[查询服务api失败]");
    }

    @GET
    @Path("/query")
    public Response queryByScriptPath(@QueryParam("scriptPath") String scriptPath) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(scriptPath)) {
                return Message.error("'one service scriptPath' is missing[缺少脚本路径]");
            }
            OneServiceVo oneServiceVo = oneServiceService.queryByScriptPath(scriptPath);
            return Message.ok().data("result", oneServiceVo);
        }, "/oneservice/search", "Fail to query page of service api[查询服务api失败]");
    }

    @GET
    @Path("/checkPath")
    public Response checkPath(@QueryParam("scriptPath") String scriptPath, @QueryParam("path") String path) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(scriptPath)) {
                return Message.error("'one service scriptPath' is missing[缺少api脚本路径]");
            }
            if (StringUtils.isBlank(path)) {
                return Message.error("'one service path' is missing[缺少api路径]");
            }
            Integer apiCount = oneServiceService.queryCountByPath(scriptPath, path);
            return Message.ok().data("result", 0 > Integer.valueOf(0).compareTo(apiCount));
        }, "/oneservice/checkPath", "Fail to check path of service api[校验服务api路径失败]");
    }

    @GET
    @Path("/checkName")
    public Response checkName(@QueryParam("name") String name) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(name)) {
                return Message.error("'one service name' is missing[缺少api名称]");
            }
            Integer count = oneServiceService.queryCountByName(name);
            return Message.ok().data("result", count > 0);
        }, "/oneservice/checkName", "Fail to check name of service api[校验服务api名称失败]");
    }

    @GET
    @Path("/apiDisable")
    public Response apiDisable(@QueryParam("id") Integer id) {
        return ApiUtils.doAndResponse(() -> {
            if (null == id) {
                return Message.error("'one service api id' is missing[缺少api id]");
            }
            boolean resultFlag = oneServiceService.disableApi(id);
            return Message.ok().data("result", resultFlag);
        }, "/oneservice/apiDisable", "Fail to disable api[禁用api失败]");
    }

    @GET
    @Path("/apiEnable")
    public Response apiEnable(@QueryParam("id") Integer id) {
        return ApiUtils.doAndResponse(() -> {
            if (null == id) {
                return Message.error("'one service api id' is missing[缺少api id]");
            }
            boolean resultFlag = oneServiceService.enableApi(id);
            return Message.ok().data("result", resultFlag);
        }, "/oneservice/apiEnable", "Fail to enable api[启用api失败]");
    }

    @GET
    @Path("/apiParamQuery")
    public Response apiParamQuery(@QueryParam("scriptPath") String scriptPath, @QueryParam("version") String version) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isEmpty(scriptPath)) {
                return Message.error("'one service api scriptPath' is missing[缺少api scriptPath]");
            }
            if (StringUtils.isEmpty(version)) {
                return Message.error("'one service api version' is missing[缺少api 版本]");
            }
            List<TestParamVo> testParamVoList = oneServiceQueryService.query(scriptPath, version);
            return Message.ok().data("result", testParamVoList);
        }, "/oneservice/apiParamQuery", "Fail to query api info[查询api信息失败]");
    }

    @GET
    @Path("/apiVersionQuery")
    public Response apiVersionQuery(@QueryParam("scriptPath") String scriptPath) {
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isEmpty(scriptPath)) {
                return Message.error("'one service api scriptPath' is missing[缺少api scriptPath]");
            }
            List<ApiVersionVo> apiVersionVoList = oneServiceQueryService.queryApiVersion(scriptPath);
            return Message.ok().data("result", apiVersionVoList);
        }, "/oneservice/apiVersionQuery", "Fail to query api version[查询api版本失败]");
    }
}
