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

package com.webank.wedatasphere.dss.datapipe.restful;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.webank.wedatasphere.dss.datapipe.entrance.background.BackGroundService;
import com.webank.wedatasphere.dss.datapipe.entrance.background.ExportBackGroundService;
import com.webank.wedatasphere.dss.datapipe.entrance.background.LoadBackGroundService;
import com.webank.wedatasphere.dss.datapipe.execute.LinkisJobSubmit;
import com.webank.wedatasphere.linkis.protocol.constants.TaskConstant;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.linkis.server.socket.controller.ServerEvent;
import com.webank.wedatasphere.linkis.ujes.client.UJESClient;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


@Path("/dss/datapipe")
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSEntranceRestfulApi {
    private static final Logger logger = LoggerFactory.getLogger(DSSEntranceRestfulApi.class);

    @POST
    @Path("/backgroundservice")
    public Response backgroundservice(@Context HttpServletRequest req, Map<String, Object> json) {
        Message message = null;
        logger.info("Begin to get an execID");
        String backgroundType = (String) json.get("background");
        BackGroundService bgService = null;
        if("export".equals(backgroundType)){
            bgService = new ExportBackGroundService();
        }else if("load".equals(backgroundType)){
            bgService = new LoadBackGroundService();
        }else{
            message = Message.error("export type is not exist："+backgroundType);
            return Message.messageToResponse(message);
        }
        Gson gson = new Gson();
        Map<String, Object> executionCode = (Map<String, Object>) json.get("executionCode");
        executionCode = gson.fromJson(gson.toJson(executionCode),LinkedTreeMap.class);
        json.put("executionCode",executionCode);
        json.put(TaskConstant.UMUSER, SecurityFilter.getLoginUsername(req));
        ServerEvent serverEvent = new ServerEvent();
        serverEvent.setData(json);
        serverEvent.setUser(SecurityFilter.getLoginUsername(req));
        ServerEvent operation = bgService.operation(serverEvent);


        JobExecuteResult jobExecuteResult = toLinkisEntrance(operation);
        message = Message.ok();
        message.setMethod("/api/dss/datapipe/backgroundservice");
        message.data("operation",operation);
        message.data("execID", jobExecuteResult.getExecID());
        message.data("taskID", jobExecuteResult.getTaskID());
        logger.info("End to get an an execID: {}, taskID: {}", jobExecuteResult.getExecID(), jobExecuteResult.getTaskID());
        return Message.messageToResponse(message);

    }

    public JobExecuteResult toLinkisEntrance(ServerEvent operation){
        JobExecuteResult jobExecuteResult = null;
        try{
            Map<String, String> props = new HashMap<>();
            UJESClient client = LinkisJobSubmit.getClient(props);
            jobExecuteResult = LinkisJobSubmit.execute(operation,client);
        }catch (Exception e){
            logger.error("toLinkisEntranceError-",e);
            throw e;
        }
        return jobExecuteResult;

    }
}
