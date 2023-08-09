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
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowExecuteInfoVo;
import com.webank.wedatasphere.dss.flow.execution.entrance.job.FlowEntranceJob;
import com.webank.wedatasphere.dss.flow.execution.entrance.service.WorkflowExecutionInfoService;
import org.apache.linkis.entrance.EntranceServer;
import org.apache.linkis.protocol.utils.ZuulEntranceUtils;
import org.apache.linkis.scheduler.queue.Job;
import org.apache.linkis.scheduler.queue.SchedulerEventState;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scala.Enumeration;
import scala.Option;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "/dss/flow/entrance")
@RestController
public class FlowExecutionRestfulApi {

    private EntranceServer entranceServer;

    private static final Logger logger = LoggerFactory.getLogger(FlowExecutionRestfulApi.class);

    @Autowired
    public void setEntranceServer(EntranceServer entranceServer) {
        this.entranceServer = entranceServer;
    }

    @Autowired
    private WorkflowExecutionInfoService workflowExecutionInfoService;

    @RequestMapping(value = "/{id}/execution",method = RequestMethod.GET)
    public Message execution(@PathVariable("id") String id,@RequestParam("labels") String labels) {
        logger.info("Begin to get job execution info, id:{}", id);
        Message message = null;
        String realId = ZuulEntranceUtils.parseExecID(id)[3];
        Option<Job> job = entranceServer.getJob(realId);
        try {
            if (job.isDefined() && job.get() instanceof FlowEntranceJob) {
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
                List<Map<String, Object>> runningJobsList = FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getRunningNodes());
                message.data("runningJobs", runningJobsList);

                List<Map<String, Object>> pendingList = FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getPendingNodes());
                pendingList.addAll(FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getSkippedNodes()));
                message.data("pendingJobs", pendingList);

                List<Map<String, Object>> succeedJobsList = FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getSucceedNodes());
                message.data("succeedJobs", succeedJobsList);

                List<Map<String, Object>> failedJobsList = FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getFailedNodes());
                message.data("failedJobs", failedJobsList);

                List<Map<String, Object>> skippedJobsList = FlowContext$.MODULE$.convertView(flowEntranceJob.getFlowContext().getSkippedNodes());
                message.data("skippedJobs", skippedJobsList);

                //如果执行失败或执行成功，将执行结果存储到数据库
                int status = getJobStatus(flowEntranceJob);
                if (status < 2) {
                    WorkflowExecuteInfoVo workflowExecuteInfoVo = new WorkflowExecuteInfoVo();
                    workflowExecuteInfoVo.setTaskId(flowEntranceJob.getJobRequest().getId());

                    String flowIdStr = BDPJettyServerHelper.jacksonJson().readValue(flowEntranceJob.getJobRequest().getExecutionCode(), Map.class).get("flowId").toString();
                    String version = BDPJettyServerHelper.jacksonJson().readValue(flowEntranceJob.getJobRequest().getExecutionCode(), Map.class).get("version").toString();
                    workflowExecuteInfoVo.setFlowId(Long.parseLong(flowIdStr));
                    workflowExecuteInfoVo.setVersion(version);

                    workflowExecuteInfoVo.setFailedJobsList(failedJobsList);
                    workflowExecuteInfoVo.setPendingJobsList(pendingList);
                    workflowExecuteInfoVo.setSkippedJobsList(skippedJobsList);
                    workflowExecuteInfoVo.setRunningJobsList(runningJobsList);
                    workflowExecuteInfoVo.setSucceedJobsList(succeedJobsList);
                    workflowExecuteInfoVo.setCreatetime(new Date());
                    workflowExecuteInfoVo.setStatus(status);
                    workflowExecutionInfoService.saveExecuteInfo(workflowExecuteInfoVo);
                }
            } else {
                message = Message.error("ID The corresponding job is empty and cannot obtain the corresponding task status.(ID 对应的job为空，不能获取相应的任务状态)");
            }
        } catch (Exception e) {
            logger.error("Failed to get job execution info:", e);
            message = Message.error("Failed to get job execution info:" + e.getMessage());
        }
        logger.info("End to get job execution info, id:{}", id);
        return message;
    }

    /**
     * 获取job的状态
     * @param flowEntranceJob
     * @return
     */
    private int getJobStatus(FlowEntranceJob flowEntranceJob) {
        Enumeration.Value state = flowEntranceJob.getState();
        int status = 2;
        if(SchedulerEventState.Failed().equals(state)
                || SchedulerEventState.Cancelled().equals(state)
                || SchedulerEventState.Timeout().equals(state)){
            //失败
            status = 0;
        }else if(SchedulerEventState.Succeed().equals(state)){
            //成功
            status = 1;
        }
        return status;
    }

}
