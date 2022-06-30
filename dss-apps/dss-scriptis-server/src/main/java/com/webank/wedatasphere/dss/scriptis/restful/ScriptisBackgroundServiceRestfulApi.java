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

package com.webank.wedatasphere.dss.scriptis.restful;

import com.google.gson.internal.LinkedTreeMap;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.scriptis.entrance.background.BackGroundService;
import com.webank.wedatasphere.dss.scriptis.entrance.background.ExportBackGroundService;
import com.webank.wedatasphere.dss.scriptis.entrance.background.LoadBackGroundService;
import com.webank.wedatasphere.dss.scriptis.execute.LinkisJobSubmit;
import org.apache.linkis.protocol.constants.TaskConstant;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.apache.linkis.server.socket.controller.ServerEvent;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.response.JobExecuteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RequestMapping(path = "/dss/scriptis", produces = {"application/json"})
@RestController
public class ScriptisBackgroundServiceRestfulApi {

    private final Logger logger = LoggerFactory.getLogger(ScriptisBackgroundServiceRestfulApi.class);

    @RequestMapping(value = "/backgroundservice", method = RequestMethod.POST)
    public Message backgroundService(HttpServletRequest req, @RequestBody Map<String, Object> json) {
        String user = SecurityFilter.getLoginUsername(req);
        String backgroundType = (String) json.get("background");
        logger.info("Begin to submit a '{}' background job for user {}.", backgroundType, user);
        BackGroundService bgService;
        if("export".equals(backgroundType)){
            bgService = new ExportBackGroundService();
        }else if("load".equals(backgroundType)){
            bgService = new LoadBackGroundService();
        }else{
            return Message.error("export type is not exist："+backgroundType);
        }
        Map<String, Object> executionCode = (Map<String, Object>) json.get("executionCode");
        executionCode = DSSCommonUtils.COMMON_GSON.fromJson(DSSCommonUtils.COMMON_GSON.toJson(executionCode), LinkedTreeMap.class);
        String username = ModuleUserUtils.getOperationUser(req, "backgroundservice");
        json.put("executionCode", executionCode);
        json.put(TaskConstant.UMUSER, username);
        ServerEvent serverEvent = new ServerEvent();
        serverEvent.setData(json);
        serverEvent.setUser(username);
        ServerEvent operation = bgService.operation(serverEvent);
        JobExecuteResult jobExecuteResult = toLinkisEntrance(operation);
        logger.info("submitted background job with execID: {}, taskID: {}.", jobExecuteResult.getExecID(), jobExecuteResult.getTaskID());
        return Message.ok().data("operation", operation)
            .data("execID", jobExecuteResult.getExecID())
            .data("taskID", jobExecuteResult.getTaskID());
    }

    public JobExecuteResult toLinkisEntrance(ServerEvent operation){
        JobExecuteResult jobExecuteResult;
        try{
            jobExecuteResult = LinkisJobSubmit.execute(operation);
        }catch (Exception e){
            logger.error("submit background job failed.", e);
            throw e;
        }
        return jobExecuteResult;

    }
}
