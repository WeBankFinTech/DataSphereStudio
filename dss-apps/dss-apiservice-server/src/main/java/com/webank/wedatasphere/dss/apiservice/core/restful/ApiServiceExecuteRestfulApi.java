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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.webank.wedatasphere.dss.apiservice.core.bo.*;
import com.webank.wedatasphere.dss.apiservice.core.config.ApiServiceConfiguration;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiServiceQueryException;
import com.webank.wedatasphere.dss.apiservice.core.execute.ExecuteCodeHelper;
import com.webank.wedatasphere.dss.apiservice.core.execute.LinkisJobSubmit;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiServiceQueryService;
import com.webank.wedatasphere.dss.apiservice.core.token.JwtManager;
import com.webank.wedatasphere.dss.apiservice.core.util.ApiUtils;
import com.webank.wedatasphere.dss.apiservice.core.util.AssertUtil;
import com.webank.wedatasphere.dss.apiservice.core.vo.MessageVo;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.response.JobExecuteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RequestMapping(path = "/dss/apiservice", produces = {"application/json"})
@RestController
public class ApiServiceExecuteRestfulApi {
    public static final String XLSX_RESPONSE_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceExecuteRestfulApi.class);
    @Autowired
    private ApiServiceQueryService queryService;
    private static final String SYS_COLUMN_PREFIX = "_";

    private static final String requestBodyDemo = "{\"moduleName\":\"aladdin-demo\",\"params\":{\"param1\": \"value1\"}}";

    @RequestMapping(value = "/execute/{path:.*}",method = RequestMethod.POST)
    public Message post(@PathVariable("path") VariableString path, @RequestBody QueryRequest queryRequest,
                         HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        return getResponse(userName,path.getPath(), queryRequest, HttpMethod.POST);
    }

    @RequestMapping(value = "/execute/{path:.*}",method = RequestMethod.GET)
    public Message get(@PathVariable("path") VariableString path,
                        HttpServletRequest req) throws JsonProcessingException {
        String userName = SecurityFilter.getLoginUsername(req);

        QueryRequest queryRequest = new QueryRequest();


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
        String paramsJsonStr = req.getParameter("params");
        JavaType javaType = BDPJettyServerHelper.jacksonJson().getTypeFactory().constructParametricType(Map.class,String.class,Object.class);
        Map<String,Object> parmas = BDPJettyServerHelper.jacksonJson().readValue(paramsJsonStr, javaType);
        queryRequest.setParams(parmas);

        if (StringUtils.isEmpty(queryRequest.getModuleName())) {
            queryRequest.setModuleName(req.getParameter("moduleName"));
        }

        return getResponse(userName,path.getPath(), queryRequest, HttpMethod.GET);
    }

    @RequestMapping(value = "/execute/{path:.*}",method = RequestMethod.PUT)
    public Message put(@PathVariable("path") VariableString path, @RequestBody QueryRequest queryRequest,
                        HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        return getResponse(userName,path.getPath(), queryRequest, HttpMethod.PUT);
    }

    @RequestMapping(value = "/execute/{path:.*}",method = RequestMethod.DELETE)
    public Message delete(@PathVariable("path") VariableString path, @RequestBody QueryRequest queryRequest,
                           HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        return getResponse(userName,path.getPath(), queryRequest, HttpMethod.DELETE);
    }

    private void validParam(QueryRequest queryRequest) {
        AssertUtil.notNull(queryRequest, "请求体不能为空，正确的格式:" + requestBodyDemo);
        AssertUtil.notEmpty(queryRequest.getModuleName(), "moduleName不能为空，正确的格式:" + requestBodyDemo);
        AssertUtil.notNull(queryRequest.getParams().get(ApiServiceConfiguration.API_SERVICE_TOKEN_KEY.getValue()),"请求token不能为空");
    }

    @RequestMapping(value = "/getDirFileTrees",method = RequestMethod.GET)
    public Message getDirFileTrees(HttpServletRequest req, HttpServletResponse resp,
                                @RequestParam(required = false, name = "path") String path,
                                @RequestParam(required = false, name = "taskId") String taskId) {
        String userName = SecurityFilter.getLoginUsername(req);
        if (!isNumber(taskId)) {
            return Message.error("请求参数 taskId 非法.");
        } else if(StringUtils.isEmpty(taskId)){
            return Message.error("taskId 为空.");
        }
        if (StringUtils.isEmpty(path)) {
            return Message.error("path 为空.");
        }
        ApiServiceJob apiServiceJob = queryService.getJobByTaskId(taskId);
        if(apiServiceJob == null) {
            return Message.error("当前用户不存在运行的TaskId: " + taskId);
        } else if(userName.equals(apiServiceJob.getSubmitUser())) {
            JobExecuteResult jobExecuteResult = new JobExecuteResult();
            jobExecuteResult.setTaskID(taskId);
            jobExecuteResult.setUser(apiServiceJob.getProxyUser());
            UJESClient client = LinkisJobSubmit.getClient();
            String dirFileTree = ExecuteCodeHelper.getResultList(jobExecuteResult, client, path);
            return DSSCommonUtils.COMMON_GSON.fromJson(dirFileTree, Message.class);
        } else {
            return Message.error("You are not the submitUser, cannot open the resultSet.");
        }
    }

    private void writeMessage(HttpServletResponse resp, Message message) throws IOException {
        if(message == null) {
            return;
        } else {
            resp.setStatus(Message.messageToHttpStatus(message));
        }
        String str = DSSCommonUtils.COMMON_GSON.toJson(message);
        resp.getWriter().println(str);
        resp.getWriter().flush();
    }

    @RequestMapping(value = "/openFile",method = RequestMethod.GET)
    public Message openFile(HttpServletRequest req,
                             @RequestParam(required = false, name = "path") String path,
                             @RequestParam(required = false, name = "taskId") String taskId,
                             @RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
                             @RequestParam(required = false, name = "pageSize", defaultValue = "5000") Integer pageSize,
                             @RequestParam(required = false, name = "charset", defaultValue = "utf-8") String charset,
                             @RequestParam(required = false, name = "enableLimit", defaultValue = "false") Boolean enableLimit) {
        String userName = SecurityFilter.getLoginUsername(req);
        logger.info("User {} wants to open resultSet file {} in task {}.", userName, path, taskId);
        if (!isNumber(taskId)) {
            return Message.error("请求参数 taskId 非法.");
        } else if(StringUtils.isEmpty(taskId)){
            return Message.error("taskId 为空.");
        }
        if (StringUtils.isEmpty(path)) {
            return Message.error("path 为空.");
        }

        ApiServiceJob apiServiceJob = queryService.getJobByTaskId(taskId);
        if(apiServiceJob == null) {
            return Message.error("您不存在运行的TaskId: "+taskId);
        } else if (userName.equals(apiServiceJob.getSubmitUser())) {
            UJESClient client = LinkisJobSubmit.getClient();
            try {
                String fileContent = ExecuteCodeHelper.getResultContent(apiServiceJob.getProxyUser(), path, pageSize, client,enableLimit);
                return DSSCommonUtils.COMMON_GSON.fromJson(fileContent, Message.class);
            } catch (Exception e) {
                logger.error("User {} fetch resultSet {} failed.", userName, path, e);
                return Message.error("Get resultSet failed! Reason: " + ExceptionUtils.getRootCauseMessage(e));
            }
        } else {
            return Message.error("You are not the submitUser, cannot open the resultSet.");
        }
    }

    @RequestMapping(value = "resultsetToExcel",method = RequestMethod.GET)
    public void resultsetToExcel(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam(required = false, name = "path") String path,
            @RequestParam(required = false, name = "taskId") String taskId,
            @RequestParam(required = false, name = "charset", defaultValue = "utf-8") String charset,
            @RequestParam(required = false, name = "outputFileType", defaultValue = "csv") String outputFileType,
            @RequestParam(required = false, name = "csvSeperator", defaultValue = ",") String csvSeperator,
            @RequestParam(required = false, name = "outputFileName", defaultValue = "downloadResultset") String outputFileName,
            @RequestParam(required = false, name = "sheetName", defaultValue = "result") String sheetName,
            @RequestParam(required = false, name = "nullValue", defaultValue = "NULL") String nullValue) throws ApiServiceQueryException, IOException {
        String userName = SecurityFilter.getLoginUsername(req);
        logger.info("User {} wants to download resultSet file {} as {} in task {}.", userName, path, outputFileType, taskId);
        if (!isNumber(taskId)) {
            writeMessage(resp, Message.error("请求参数 taskId 非法."));
            return;
        } else if(StringUtils.isEmpty(taskId)){
            writeMessage(resp, Message.error("taskId 为空."));
            return;
        }
        if (StringUtils.isEmpty(path)) {
            writeMessage(resp, Message.error("path 为空."));
            return;
        }
        resp.addHeader("Content-Disposition", "attachment;filename="
                + new String(outputFileName.getBytes("UTF-8"), "ISO8859-1") + "." + outputFileType);
        resp.setCharacterEncoding(charset);

        switch (outputFileType) {
            case "csv":
                resp.addHeader("Content-Type", "text/plain");
                break;
            case "xlsx":
                resp.addHeader("Content-Type", XLSX_RESPONSE_CONTENT_TYPE);
                break;
            default:
                writeMessage(resp, Message.error("不支持的下载类型."));
                return;

        }

        InputStream inputStream = null;
        ApiServiceJob apiServiceJob = queryService.getJobByTaskId(taskId);
        if(null == apiServiceJob) {
            writeMessage(resp, Message.error("您不存在运行的TaskId: "+taskId));
            return;
        } else if(userName.equals(apiServiceJob.getSubmitUser())) {
            UJESClient client = LinkisJobSubmit.getClient();
            inputStream = ExecuteCodeHelper.downloadResultSet(apiServiceJob.getProxyUser(),
                                                           path,
                                                           charset,
                                                           outputFileType,
                                                           csvSeperator,
                                                           outputFileName,
                                                           sheetName,
                                                           nullValue,
                                                           client);
        }
        try {
            IOUtils.copy(inputStream, resp.getOutputStream());
            resp.getOutputStream().flush();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @RequestMapping(value = "/{id}/get",method = RequestMethod.GET)
    public Message getTaskByID(HttpServletRequest req, @PathVariable("id") Long taskId) {
        if(taskId == null || !isNumber(taskId.toString())){
            return Message.error("请求参数taskId非法");
        }
        String username = SecurityFilter.getLoginUsername(req);
        ApiServiceJob apiServiceJob = queryService.getJobByTaskId(taskId.toString());
        if (null != apiServiceJob && username.equals(apiServiceJob.getSubmitUser())) {
            UJESClient client = LinkisJobSubmit.getClient();
            JobExecuteResult jobExecuteResult = apiServiceJob.getJobExecuteResult();
            jobExecuteResult.setUser(apiServiceJob.getProxyUser());
            Map<String, Object> vo = ExecuteCodeHelper.getTaskInfoById(jobExecuteResult, client);
            return Message.ok().data("task", vo);
        } else {
            return Message.ok().data("task", null);
        }
    }

    private Message getResponse(String user,String path, QueryRequest queryRequest, String httpMethod) {
        Message response = ApiUtils.doAndResponse(() -> {
            validParam(queryRequest);
            String token = queryRequest.getParams().get(ApiServiceConfiguration.API_SERVICE_TOKEN_KEY.getValue()).toString();

            Message messageVo = null;
            ApiServiceToken tokenDetail = null;
            boolean isParseRight = true;
            try {
                tokenDetail = JwtManager.parseToken(token);
            }catch (Exception e) {
                isParseRight = false;
                messageVo = Message.error("token解析错误，该token无效！");
            }
            if(false == isParseRight) {
                return messageVo;
            }

            if(tokenDetail.getApplyUser().equals(user)) {
                LinkisExecuteResult query = queryService.query("/" + path,
                        queryRequest.getParams() == null ? new HashMap<>() : queryRequest.getParams(),
                        queryRequest.getModuleName(), httpMethod,tokenDetail,user);
                if(null == query) {
                    messageVo = Message.error("用户输入了非法关键字！");
                    return messageVo;
                }

                messageVo = Message.ok().data("taskId",query.getTaskId()).data("execId",query.getExecId());
            }else {
                messageVo = Message.error("Token is not correct");
            }
            return messageVo;
        });
        return response;
    }

    //convert Response to Message
    public Message convertMessage(Response response) {
        MessageVo tempVo = (MessageVo) response.getEntity();
        Message message = null;
        if (tempVo.getStatus().intValue() == 1) {
            message = Message.error(tempVo.getMessage());
        } else {
            message = Message.ok(tempVo.getMessage());
        }
        HashMap<String, Object> queryRes = (HashMap<String, Object>) tempVo.getData();
        if (!CollectionUtils.isEmpty(queryRes)) {
            for (String key : queryRes.keySet()) {
                message.data(key, queryRes.get(key));
            }
        }
        return message;
    }

    Pattern numberPattern = Pattern.compile("^\\d+$");
    //Judge if the taskId is number
    public boolean isNumber(String taskId) {
        if (taskId == null || taskId.trim().equals("")) {
            return false;
        }
        boolean matches = numberPattern.matcher(taskId).matches();
        return matches;
    }
}