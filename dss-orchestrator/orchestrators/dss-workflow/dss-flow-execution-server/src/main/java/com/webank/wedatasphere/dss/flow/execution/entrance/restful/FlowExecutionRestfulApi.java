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

package com.webank.wedatasphere.dss.flow.execution.entrance.restful;

import com.webank.wedatasphere.dss.flow.execution.entrance.FlowContext$;
import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob;
import org.apache.linkis.entrance.EntranceServer;
import org.apache.linkis.entrance.annotation.EntranceServerBeanAnnotation;
import org.apache.linkis.protocol.utils.ZuulEntranceUtils;
import org.apache.linkis.scheduler.queue.Job;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import scala.Option;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/dss/flow/entrance")
@RestController
public class FlowExecutionRestfulApi {

    private EntranceServer entranceServer;

    private static final Logger logger = LoggerFactory.getLogger(FlowExecutionRestfulApi.class);

    @EntranceServerBeanAnnotation.EntranceServerAutowiredAnnotation
    public void setEntranceServer(EntranceServer entranceServer){
        this.entranceServer = entranceServer;
    }

    @RequestMapping(value = "/{id}/execution",method = RequestMethod.GET)
    public Message execution(@PathVariable("id") String id,@RequestParam("labels") String labels) {
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
                long nowTime = System.currentTimeMillis();
                flowEntranceJob.getFlowContext().getRunningNodes().forEach((k, v) -> {
                    if (v != null) {
                        v.setNowTime(nowTime);
                    }
                });
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
        return message;
    }

}
