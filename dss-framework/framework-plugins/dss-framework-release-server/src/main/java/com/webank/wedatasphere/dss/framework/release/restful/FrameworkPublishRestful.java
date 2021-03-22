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

package com.webank.wedatasphere.dss.framework.release.restful;

import com.webank.wedatasphere.dss.framework.common.utils.RestfulUtils;
import com.webank.wedatasphere.dss.framework.release.entity.request.ReleaseOrchestratorRequest;
import com.webank.wedatasphere.dss.framework.release.entity.task.PublishStatus;
import com.webank.wedatasphere.dss.framework.release.service.PublishToSchedulerService;
import com.webank.wedatasphere.dss.framework.release.utils.ReleaseUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * created by cooperyang on 2021/2/22
 * Description:
 */
@Component
@Path("/dss/framework/release")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FrameworkPublishRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkPublishRestful.class);

    @Autowired
    private PublishToSchedulerService publishToSchedulerService;


    @POST
    @Path("/publishToScheduler")
    public Response publishToScheduler(@Context HttpServletRequest request,
                                       @Valid ReleaseOrchestratorRequest releaseOrchestratorRequest){
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        try{
            Long releaseTaskId = publishToSchedulerService.publish(username, releaseOrchestratorRequest, workspace);
            return RestfulUtils.dealOk("提交发布任务成功", new Pair<>("releaseTaskId", releaseTaskId));
        }catch(final Throwable t){
            LOGGER.error("failed to do publish to Scheduler for user {} , req is {}",
                    username, ReleaseUtils.GSON.toJson(releaseOrchestratorRequest), t);
            return RestfulUtils.dealError("提交发布任务失败");
        }
    }

    @GET
    @Path("/getPublishStatus")
    public Response getPublishStatus(@Context HttpServletRequest request,
                                     @NotNull(message = "查询的发布id不能为空") @QueryParam("releaseTaskId") Long releaseTaskId){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            PublishStatus publishStatus = publishToSchedulerService.getStatus(username, releaseTaskId);
            return RestfulUtils.dealOk("获取发布进度成功", new Pair<>("status", publishStatus.getStatus()), new Pair<>("errorMsg", publishStatus.getErrorMsg()));
        }catch(final Throwable t){
            LOGGER.error("Failed to get publish status for user {} , releaseTaskId {}", username, releaseTaskId, t);
            return RestfulUtils.dealError("获取任务进度失败");
        }
    }


}
