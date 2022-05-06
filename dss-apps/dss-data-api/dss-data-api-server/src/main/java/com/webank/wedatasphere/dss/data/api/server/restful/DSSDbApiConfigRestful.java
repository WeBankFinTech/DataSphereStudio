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
 import com.webank.wedatasphere.dss.data.api.server.entity.VariableString;
 import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiExecuteInfo;
 import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiGroupInfo;
 import com.webank.wedatasphere.dss.data.api.server.exception.DataApiException;
 import com.webank.wedatasphere.dss.data.api.server.service.ApiConfigService;
 import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
 import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
 import org.apache.linkis.server.Message;
 import org.apache.linkis.server.security.SecurityFilter;
 import lombok.extern.slf4j.Slf4j;
 import org.codehaus.jettison.json.JSONException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.web.bind.annotation.*;

 import javax.servlet.http.HttpServletRequest;
 import javax.validation.Valid;
 import java.util.List;
 import java.util.Map;


 @RestController
 @RequestMapping(path = "/dss/data/api", produces = {"application/json"})
 @Slf4j
 public class DSSDbApiConfigRestful {
     private static final Logger LOGGER = LoggerFactory.getLogger(DSSDbApiConfigRestful.class);
     @Autowired
     ApiConfigService apiConfigService;

     /**
      * 保存api配置信息
      *
      * @param request
      * @param apiConfig
      * @return
      */
     @RequestMapping(path = "save", method = RequestMethod.POST)
     public Message saveApi(@Valid @RequestBody ApiConfig apiConfig, HttpServletRequest request) throws JSONException, DataApiException {
         String username = SecurityFilter.getLoginUsername(request);
         apiConfig.setCreateBy(username);
         apiConfig.setUpdateBy(username);
         apiConfigService.saveApi(apiConfig);
         Message message = Message.ok();
         return message;
     }

     /**
      * 创建API 组
      *
      * @param apiGroup
      * @return
      */

     @RequestMapping(path = "/group/create", method = RequestMethod.POST)
     public Message saveGroup(@Valid @RequestBody ApiGroup apiGroup, HttpServletRequest request) {
         String username = SecurityFilter.getLoginUsername(request);
         apiGroup.setCreateBy(username);
         apiConfigService.addGroup(apiGroup);
         Message message = Message.ok().data("groupId", apiGroup.getId());
         return message;
     }

     /**
      * API list
      *
      * @param workspaceId
      * @return
      */

     @RequestMapping(path = "list", method = RequestMethod.GET)
     public Message getApiList(HttpServletRequest httpServletRequest,
                               @RequestParam(value = "workspaceId", required = false) String workspaceId) {
         Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);

         List<ApiGroupInfo> list = apiConfigService.getGroupList(workspace.getWorkspaceName());
         Message message = Message.ok().data("list", list);
         return message;
     }

     /**
      * 查询api详情
      *
      * @param apiId
      * @return
      */

     @RequestMapping(path = "detail", method = RequestMethod.GET)
     public Message getApiDetail(@RequestParam("apiId") int apiId) {
         ApiConfig apiConfig = apiConfigService.getById(apiId);
         Message message = Message.ok().data("detail", apiConfig);
         return message;
     }

     /**
      * 测试 API
      *
      * @param request
      * @param path
      * @param map
      * @return
      */

     @RequestMapping(value = "/test/{path:[a-zA-Z0-9_-]+}", method = RequestMethod.POST)
     public Message testApi(HttpServletRequest request, @PathVariable("path") VariableString path,
                            @RequestBody Map<String, Object> map) {

         try {
             ApiExecuteInfo resJo = apiConfigService.apiTest(path.getPath(), request, map, true);
             Message message = Message.ok().data("response", resJo);
             return message;
         } catch (Exception exception) {
             log.error("ERROR", "Error found: ", exception);
             return Message.error(exception.getMessage());
         }

     }


     /**
      * 第三方调用 api
      *
      * @param request
      * @param path
      * @param map
      * @return
      */

     @RequestMapping(value = "/execute/{path:[a-zA-Z0-9_-]+}", method = RequestMethod.POST)
     public Message executeApi(HttpServletRequest request, @PathVariable("path") VariableString path, @RequestBody Map<String, Object> map) {
         try {
             ApiExecuteInfo resJo = apiConfigService.apiExecute(path.getPath(), request, map);
             Message message = Message.ok().data("response", resJo);
             return message;
         } catch (Exception exception) {
             log.error("ERROR", "Error found: ", exception);
             return Message.error(exception.getMessage());
         }

     }

 }
