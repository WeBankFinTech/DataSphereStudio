/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import com.webank.wedatasphere.dss.apiservice.core.bo.ApiCommentUpdateRequest;
import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceQuery;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiService;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiServiceQueryService;
import com.webank.wedatasphere.dss.apiservice.core.util.ApiUtils;
import com.webank.wedatasphere.dss.apiservice.core.util.AssertUtil;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApprovalVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.QueryParamVo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequestMapping(path = "/dss/apiservice", produces = {"application/json"})
@RestController
public class ApiServiceCoreRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceCoreRestfulApi.class);

    @Autowired
    private ApiService apiService;

    @Autowired
    private ApiServiceQueryService apiServiceQueryService;

    @Autowired
    private Validator beanValidator;

    private static final Pattern WRITABLE_PATTERN = Pattern.compile("^\\s*(insert|update|delete|drop|alter|create).*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @RequestMapping(value = "/api",method = RequestMethod.POST)
    public Message insert(@RequestBody ApiServiceVo apiService, HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {

            if (apiService.getWorkspaceId() == null){
                apiService.setWorkspaceId(180L);
            }

            if (StringUtils.isBlank(apiService.getAliasName())) {
                return Message.error("'api service alias name' is missing[缺少中文名]");
            }

            if (StringUtils.isBlank(apiService.getScriptPath())) {
                return Message.error("'api service script path' is missing[缺少脚本路径]");
            }
            if (StringUtils.isBlank(apiService.getContent())) {
                    return Message.error("'api service script content' is missing[缺少脚本内容]");
            }

            if (null == apiService.getWorkspaceId()) {
                return Message.error("'api service workspaceId ' is missing[缺少工作空间ID]");
            }
            if (apiService.getContent().contains(";")) {
                if(!apiService.getContent().toLowerCase().startsWith("use ")) {
                    return Message.error("'api service script content exists semicolon[脚本内容包含分号]");
                }
            }

//                     check data change script
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

            ApprovalVo approvalVo = apiService.getApprovalVo();

//            if (StringUtils.isBlank(approvalVo.getApprovalName())) {
//                return Message.error("'approvalName' is missing[缺少审批单名字]");
//            }

            if (StringUtils.isBlank(approvalVo.getApplyUser())) {
                return Message.error("'applyUser' is missing[缺少申请用户名字]");
            }

            apiService.setCreator(userName);
            apiService.setModifier(userName);
            this.apiService.save(apiService);
            return Message.ok().data("insert_id", apiService.getId()).data("approval_no",approvalVo.getApprovalNo());
        }, "/apiservice/api", "Fail to insert service api[新增服务api失败]");
    }

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public Message create(@RequestBody ApiServiceVo apiService, HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {

            if (apiService.getWorkspaceId() == null){
                apiService.setWorkspaceId(180L);
            }

            if (StringUtils.isBlank(apiService.getAliasName())) {
                return Message.error("'api service alias name' is missing[缺少中文名]");
            }

            if (StringUtils.isBlank(apiService.getScriptPath())) {
                return Message.error("'api service script path' is missing[缺少脚本路径]");
            }
            if (StringUtils.isBlank(apiService.getContent())) {
                return Message.error("'api service script content' is missing[缺少脚本内容]");
            }

            if (null == apiService.getWorkspaceId()) {
                return Message.error("'api service workspaceId ' is missing[缺少工作空间ID]");
            }
            if (apiService.getContent().contains(";")) {
                if(!apiService.getContent().toLowerCase().startsWith("use ")) {
                    return Message.error("'api service script content exists semicolon[脚本内容包含分号]");
                }
            }

//                     check data change script
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
            LOG.info("user {} begin to create api, params: {}", userName, apiService);
            Set<ConstraintViolation<ApiServiceVo>> result = beanValidator.validate(apiService, Default.class);
            if (result.size() > 0) {
                throw new ConstraintViolationException(result);
            }

            ApprovalVo approvalVo = apiService.getApprovalVo();

            if (StringUtils.isBlank(approvalVo.getApprovalName())) {
                return Message.error("'approvalName' is missing[缺少审批单名字]");
            }

            if (StringUtils.isBlank(approvalVo.getApplyUser())) {
                return Message.error("'applyUser' is missing[缺少申请用户名字]");
            }

            apiService.setCreator(userName);
            apiService.setModifier(userName);
            this.apiService.saveByApp(apiService);
            return Message.ok().data("insert_id", apiService.getId()).data("approval_no",approvalVo.getApprovalNo());
        }, "/apiservice/api", "Fail to insert service api[新增服务api失败]");
    }

    @RequestMapping(value = "/api/{api_service_version_id}",method = RequestMethod.PUT)
    public Message update(@RequestBody ApiServiceVo apiService,
                           @PathVariable("api_service_version_id") Long apiServiceVersionId,
                           HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {

            if (StringUtils.isBlank(apiService.getScriptPath())) {
                return Message.error("'api service script path' is missing[缺少脚本路径]");
            }
            if(apiServiceVersionId !=0) {
                if (StringUtils.isBlank(apiService.getPath())) {
                    return Message.error("'api service api path' is missing[缺少api路径]");
                }
            }
            if (StringUtils.isBlank(apiService.getContent())) {
                return Message.error("'api service script content' is missing[缺少脚本内容]");
            }

            if (null == apiService.getWorkspaceId()) {
                return Message.error("'api service workspaceId ' is missing[缺少工作空间ID]");
            }

            if (null == apiService.getTargetServiceId()) {
                return Message.error("'api service update to target service id ' is missing[缺少更新目标服务ID]");
            }

            if (apiService.getContent().contains(";")) {
                return Message.error("'api service script content exists semicolon[脚本内容包含分号]");
            }

            ApprovalVo approvalVo = apiService.getApprovalVo();

//            if (StringUtils.isBlank(approvalVo.getApprovalName())) {
//                return Message.error("'approvalName' is missing[缺少审批单名字]");
//            }

            if (StringUtils.isBlank(approvalVo.getApplyUser())) {
                return Message.error("'applyUser' is missing[缺少申请用户名字]");
            }
//            if (StringUtils.isBlank(apiService.getResourceId())) {
//                return Message.error("'api service resourceId' is missing[缺少bml resourceId]");
//            }

//             check data change script
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
            LOG.info("user {} try to update service api, params: {}", userName, apiService);
//            Bean validation
            Set<ConstraintViolation<ApiServiceVo>> result = beanValidator.validate(apiService, Default.class);
            if (result.size() > 0) {
                throw new ConstraintViolationException(result);
            }
            apiService.setLatestVersionId(apiServiceVersionId);
            apiService.setModifier(userName);
            apiService.setModifyTime(Calendar.getInstance().getTime());
            this.apiService.update(apiService);
            return Message.ok().data("update_id", apiServiceVersionId);
        }, "/apiservice/api/" + apiServiceVersionId, "Fail to update service api[更新服务api失败]");
    }




    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public Message query(@RequestParam(required = false, name = "name") String name,
                                    @RequestParam(required = false, name = "tag") String tag,
                                    @RequestParam(required = false, name = "status") Integer status,
                                    @RequestParam(required = false, name = "creator") String creator,
                                    @RequestParam(required = false, name = "workspaceId") Integer workspaceId,
                                    HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);

        return ApiUtils.doAndResponse(() -> {
            if (null == workspaceId) {
                return Message.error("'api service search workspaceId' is missing[缺少工作空间Id]");
            }
            ApiServiceQuery query = new ApiServiceQuery(userName,name, tag, status, creator);
            query.setWorkspaceId(workspaceId);
            if(!this.apiService.checkUserWorkspace(userName,workspaceId) ){
                return Message.error("'api service search workspaceId' is wrong[该用户不属于该工作空间Id]");
            }
            List<ApiServiceVo> queryList = apiService.query(query);
            return Message.ok().data("query_list", queryList);
        }, "/apiservice/search", "Fail to query page of service api[查询服务api失败]");
    }


    @RequestMapping(value = "/getUserServices",method = RequestMethod.GET)
    public Message getUserServices(@RequestParam(required = false, name = "workspaceId") Integer workspaceId,
            HttpServletRequest req){
        String userName = SecurityFilter.getLoginUsername(req);
        return ApiUtils.doAndResponse(() -> {
            if(!this.apiService.checkUserWorkspace(userName,workspaceId) ){
                return Message.error("'api service getUserServices workspaceId' is wrong[该用户不属于该工作空间Id]");
            }
        List<ApiServiceVo> apiServiceList = apiService.queryByWorkspaceId(workspaceId,userName);
        return Message.ok().data("query_list", apiServiceList);
        }, "/apiservice/getUserServices", "Fail to query page of user service api[查询用户服务api列表失败]");
    }



    @RequestMapping(value = "/tags",method = RequestMethod.GET)
    public Message query( HttpServletRequest req,@RequestParam(required = false, name = "workspaceId") Integer workspaceId) {
        String userName = SecurityFilter.getLoginUsername(req);
        return ApiUtils.doAndResponse(() -> {

            List<String> tags= apiService.queryAllTags(userName,workspaceId);
            return Message.ok().data("tags", tags);
        }, "/apiservice/tags", "Fail to query page of service tag[查询服务tag失败]");
    }




    @RequestMapping(value = "/query",method = RequestMethod.GET)
    public Message queryByScriptPath(@RequestParam(required = false, name = "scriptPath") String scriptPath,
                                      HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {
            String userName = SecurityFilter.getLoginUsername(req);
            if (StringUtils.isBlank(scriptPath)) {
                return Message.error("'api service scriptPath' is missing[缺少脚本路径]");
            }
            ApiServiceVo apiServiceVo = apiService.queryByScriptPath(scriptPath);
            if(null != apiServiceVo) {
                if (!this.apiService.checkUserWorkspace(userName, apiServiceVo.getWorkspaceId().intValue())) {
                    return Message.error("'api service query workspaceId' is wrong[该用户不属于该工作空间Id]");
                }

                if (apiServiceVo.getCreator().equals(userName)) {
                    return Message.ok().data("result", apiServiceVo);
                } else {
                    return Message.error("'api service belong to others' [该脚本路径数据服务不属于当前用户]");
                }
            }else {
                return Message.ok().data("result", apiServiceVo);
            }
        }, "/apiservice/query", "Fail to query page of service api[查询服务api失败]");
    }

    @RequestMapping(value = "/queryById",method = RequestMethod.GET)
    public Message queryById(@RequestParam(required = false, name = "id") Long id,
                              HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        return ApiUtils.doAndResponse(() -> {
            if (id==null) {
                return Message.error("'api service id' is missing[缺少服务ID]");
            }
            ApiServiceVo apiServiceVo = apiService.queryById(id,userName);
            AssertUtil.notNull(apiServiceVo,"未找到数据服务，有可能已经被删除");
            if(!this.apiService.checkUserWorkspace(userName,apiServiceVo.getWorkspaceId().intValue()) ){
                return Message.error("'api service queryById for workspaceId' is wrong[该用户不属于该工作空间Id]");
            }
            return Message.ok().data("result", apiServiceVo);
        }, "/apiservice/queryById", "Fail to query page of service api[查询服务api失败]");
    }

    @RequestMapping(value = "/checkPath",method = RequestMethod.GET)
    public Message checkPath(@RequestParam(required = false, name = "scriptPath") String scriptPath, @RequestParam(required = false, name = "path") String path) {
        //需要跨用户查询
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(scriptPath)) {
                return Message.error("'api service scriptPath' is missing[缺少api脚本路径]");
            }
            if (StringUtils.isBlank(path)) {
                return Message.error("'api service path' is missing[缺少api路径]");
            }
            Integer apiCount = apiService.queryCountByPath(scriptPath, path);
            return Message.ok().data("result", 0 > Integer.valueOf(0).compareTo(apiCount));
        }, "/apiservice/checkPath", "Fail to check path of service api[校验服务api路径失败]");
    }

    @RequestMapping(value = "/checkName",method = RequestMethod.GET)
    public Message checkName(@RequestParam(required = false, name = "name") String name) {
        //需要跨用户查询
        return ApiUtils.doAndResponse(() -> {
            if (StringUtils.isBlank(name)) {
                return Message.error("'api service name' is missing[缺少api名称]");
            }
            Integer count = apiService.queryCountByName(name);
            return Message.ok().data("result", count > 0);
        }, "/apiservice/checkName", "Fail to check name of service api[校验服务api名称失败]");
    }

    @RequestMapping(value = "/apiDisable",method = RequestMethod.GET)
    public Message apiDisable(@RequestParam(required = false, name = "id") Long id,
                               HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {
            String userName = SecurityFilter.getLoginUsername(req);
            if (null == id) {
                return Message.error("'api service api id' is missing[缺少api id]");
            }
            LOG.info("user {} begin to disable service api, id: {}", userName, id);
            boolean resultFlag = apiService.disableApi(id,userName);
            return Message.ok().data("result", resultFlag);
        }, "/apiservice/apiDisable", "Fail to disable api[禁用api失败]");
    }

    @RequestMapping(value = "/apiEnable",method = RequestMethod.GET)
    public Message apiEnable(@RequestParam(required = false, name = "id") Long id,
                              HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {
            String userName = SecurityFilter.getLoginUsername(req);
            if (null == id) {
                return Message.error("'api service api id' is missing[缺少api id]");
            }
            LOG.info("user {} begin to enable service api, id: {}", userName, id);
            boolean resultFlag = apiService.enableApi(id,userName);
            return Message.ok().data("result", resultFlag);
        }, "/apiservice/apiEnable", "Fail to enable api[启用api失败]");
    }

    @RequestMapping(value = "/apiDelete",method = RequestMethod.GET)
    public Message apiDelete(@RequestParam(required = false, name = "id") Long id,
                               HttpServletRequest req) {
        //目前暂时不实际删除数据，只做不可见和不可用。
        return ApiUtils.doAndResponse(() -> {
            String userName = SecurityFilter.getLoginUsername(req);
            if (null == id) {
                return Message.error("'api service api id' is missing[缺少api id]");
            }
            LOG.info("user {} try to delete service api, id: {}", userName, id);
            boolean resultFlag = apiService.deleteApi(id,userName);
            return Message.ok().data("result", resultFlag);
        }, "/apiservice/apiDelete", "Fail to delete api[删除api失败]");
    }

    @RequestMapping(value = "/apiCommentUpdate",method = RequestMethod.POST)
    public Message apiCommentUpdate(HttpServletRequest req,
                                    @RequestBody ApiCommentUpdateRequest apiCommentUpdateRequest) {
        Long id = apiCommentUpdateRequest.getId();
        String comment = apiCommentUpdateRequest.getComment();
        //目前暂时不实际删除数据，只做不可见和不可用。
        return ApiUtils.doAndResponse(() -> {
            String userName = SecurityFilter.getLoginUsername(req);
            if (null == id) {
                return Message.error("'api service api id' is missing[缺少api id]");
            }
            LOG.info("user {} try to update comment, params: {}", userName, apiCommentUpdateRequest);
            boolean resultFlag = apiService.updateComment(id,comment,userName);
            return Message.ok().data("result", resultFlag);
        }, "/apiservice/apiDelete", "Fail to update comment api[更新评论失败]");
    }


    @RequestMapping(value = "/apiParamQuery",method = RequestMethod.GET)
    public Message apiParamQuery(@RequestParam(required = false, name = "scriptPath") String scriptPath,
                                  @RequestParam(required = false, name = "versionId") Long versionId,
                                  HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {
            String userName = SecurityFilter.getLoginUsername(req);
            if (StringUtils.isEmpty(scriptPath)) {
                return Message.error("'api service api scriptPath' is missing[缺少api scriptPath]");
            }
            if (null == versionId) {
                return Message.error("'api service api version' is missing[缺少api 版本号]");
            }
            List<QueryParamVo> queryParamVoList = apiServiceQueryService.queryParamList(scriptPath, versionId);
            return Message.ok().data("result", queryParamVoList);
        }, "/apiservice/apiParamQuery", "Fail to query api info[查询api信息失败]");
    }

    @RequestMapping(value = "/apiVersionQuery",method = RequestMethod.GET)
    public Message apiVersionQuery(@RequestParam(required = false, name = "serviceId") Long serviceId,
                                    HttpServletRequest req) {
        return ApiUtils.doAndResponse(() -> {
            String userName = SecurityFilter.getLoginUsername(req);
            if (null == serviceId) {
                return Message.error("'api service api serviceId' is missing[缺少api serviceId]");
            }
            List<ApiVersionVo> apiVersionVoList = apiServiceQueryService.queryApiVersionById(serviceId)
                                                  .stream().filter(apiVersionVo -> apiVersionVo.getCreator().equals(userName))
                                                  .collect(Collectors.toList());
            return Message.ok().data("result", apiVersionVoList);
        }, "/apiservice/apiVersionQuery", "Fail to query api version[查询api版本失败]");
    }

    @RequestMapping(value = "/apiContentQuery",method = RequestMethod.GET)
    public Message apiContentQuery(@RequestParam(required = false, name = "versionId") Long versionId,
                                    HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        return ApiUtils.doAndResponse(() -> {
            if (null== versionId) {
                return Message.error("'api service api versionId' is missing[缺少api versionId]");
            }
            ApiServiceVo apiServiceVo = apiServiceQueryService.queryByVersionId(userName,versionId);
            if(!this.apiService.checkUserWorkspace(userName,apiServiceVo.getWorkspaceId().intValue()) ){
                return Message.error("'api service apiContentQuery for workspaceId' is wrong[该用户不属于该工作空间Id]");
            }
            return Message.ok().data("result", apiServiceVo);
        }, "/apiservice/apiContentQuery", "Fail to query api Content[查询api版本内容失败]");
    }
}
