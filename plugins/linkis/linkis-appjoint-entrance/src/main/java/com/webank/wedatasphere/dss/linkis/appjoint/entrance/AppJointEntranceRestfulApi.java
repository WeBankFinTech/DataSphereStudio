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

package com.webank.wedatasphere.dss.linkis.appjoint.entrance;

import com.webank.wedatasphere.dss.appjoint.AppJoint;
import com.webank.wedatasphere.dss.appjoint.execution.NodeExecution;
import com.webank.wedatasphere.dss.appjoint.execution.core.CallbackLongTermNodeExecution;
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.appjoint.AppJointManager;
import com.webank.wedatasphere.linkis.entrance.EntranceContext;
import com.webank.wedatasphere.linkis.server.Message;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * created by cooperyang on 2019/9/30
 * Description:
 */
@Path("/entrance")
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppJointEntranceRestfulApi {

    public static final String APPJOINT_NAME_STR = "appJointName";

    public static final String APPJOINT_LOG_STR = "log";

    public static final String APPJOINT_STATUS_STR = "status";

    public static final String APPJOINT_PROGRESS_STR = "progress";

    public static final String ENGINE_TAG_STR = "engineTag";

    public static final String CALL_BACK_URL = "/api/entrance/callback";

    /**
     * callback接口是为了接收外部系统进行回馈各种信息的接口，外部系统调用此接口必须提供的参数有entranceEngine的唯一标识，
     * 因为只有entranceEngine的唯一标识，才能进行定位到相应的job
     * jsonNode 是外部系统传递的参数,这些参数可以包含有日志，进度 和 状态等等
     */
    @POST
    @Path("/callback")
    public Response callback(@Context HttpServletRequest request,
                             @Context HttpServletResponse response,
                             Map<String, Object> params) {
        Object appJointName = params.get(APPJOINT_NAME_STR);
        if(appJointName == null || StringUtils.isBlank(appJointName.toString())) {
            return error(APPJOINT_NAME_STR + "为空！");
        }
        AppJoint appJoint = AppJointManager.getAppJoint(appJointName.toString());
        if(appJoint == null) {
            return error("找不到AppJoint: " + appJointName);
        }
        NodeExecution nodeExecution = appJoint.getNodeExecution();
        if(!(nodeExecution instanceof CallbackLongTermNodeExecution)) {
            return error("找不到CallbackLongTermNodeExecution来处理该请求，实际NodeExecution为 " + nodeExecution.getClass().getSimpleName());
        }
        ((CallbackLongTermNodeExecution) nodeExecution).acceptCallback(params);
        Message message = Message.ok("处理成功!");
        message.setMethod(CALL_BACK_URL);
        return Message.messageToResponse(message);
    }

    private Response error(String msg) {
        Message message = Message.error(msg);
        message.setMethod(CALL_BACK_URL);
        return Message.messageToResponse(message);
    }
}
