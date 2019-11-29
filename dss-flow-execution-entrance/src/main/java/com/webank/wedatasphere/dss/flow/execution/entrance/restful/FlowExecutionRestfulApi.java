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

package com.webank.wedatasphere.dss.flow.execution.entrance.restful;

import com.webank.wedatasphere.dss.flow.execution.entrance.FlowContext;
import com.webank.wedatasphere.dss.flow.execution.entrance.FlowContext$;
import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob;
import com.webank.wedatasphere.linkis.entrance.EntranceServer;
import com.webank.wedatasphere.linkis.entrance.annotation.EntranceServerBeanAnnotation;
import com.webank.wedatasphere.linkis.protocol.utils.ZuulEntranceUtils;
import com.webank.wedatasphere.linkis.scheduler.queue.Job;
import com.webank.wedatasphere.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import scala.Option;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by peacewong on 2019/11/5.
 */

@Path("/entrance")
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlowExecutionRestfulApi {

    private EntranceServer entranceServer;

    private static final Logger logger = LoggerFactory.getLogger(FlowExecutionRestfulApi.class);

    @EntranceServerBeanAnnotation.EntranceServerAutowiredAnnotation
    public void setEntranceServer(EntranceServer entranceServer){
        this.entranceServer = entranceServer;
    }

    @GET
    @Path("/{id}/execution")
    public Response execution(@PathParam("id") String id) {
        Message message = null;
        String realId = ZuulEntranceUtils.parseExecID(id)[3];
        Option<Job> job = entranceServer.getJob(realId);
        try {
            if (job.isDefined() && job.get() instanceof FlowEntranceJob){
                logger.info("Start to get job {} execution info", job.get().getId());
                FlowEntranceJob flowEntranceJob = (FlowEntranceJob) job.get();

                message = Message.ok("Successfully get job execution info");
                message.setMethod("/api/entrance/" + id + "/execution");
                message.setStatus(0);
                message.data("runningJobs", FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getRunningNodes()));
                List<Map<String, Object>> pendingList = FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getPendingNodes());
                pendingList.addAll(FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getSkippedNodes()));
                message.data("pendingJobs", pendingList);
                message.data("succeedJobs", FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getSucceedNodes()));
                message.data("failedJobs", FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getFailedNodes()));
                message.data("skippedJobs", FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getSkippedNodes()));
            }else{
                message = Message.error("ID The corresponding job is empty and cannot obtain the corresponding task status.(ID 对应的job为空，不能获取相应的任务状态)");
            }
        } catch (Exception e) {
            message = Message.error("Failed to get job execution info");
        }
        return Message.messageToResponse(message);
    }

}
