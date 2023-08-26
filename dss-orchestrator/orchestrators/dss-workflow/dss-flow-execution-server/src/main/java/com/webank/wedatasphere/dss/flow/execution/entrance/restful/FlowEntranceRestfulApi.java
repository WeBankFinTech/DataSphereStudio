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

import com.webank.wedatasphere.dss.common.entity.DSSWorkspace;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionConf;
import com.webank.wedatasphere.dss.flow.execution.entrance.dao.TaskMapper;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowQueryTask;
import com.webank.wedatasphere.dss.flow.execution.entrance.enums.ExecuteStrategyEnum;
import com.webank.wedatasphere.dss.flow.execution.entrance.service.WorkflowExecutionInfoService;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.log.LogUtils;
import org.apache.linkis.entrance.EntranceServer;
import org.apache.linkis.entrance.annotation.EntranceServerBeanAnnotation;
import org.apache.linkis.entrance.execute.EntranceJob;
import org.apache.linkis.entrance.restful.EntranceRestfulApi;
import org.apache.linkis.entrance.utils.JobHistoryHelper;
import org.apache.linkis.governance.common.entity.job.JobRequest;
import org.apache.linkis.protocol.constants.TaskConstant;
import org.apache.linkis.protocol.utils.ZuulEntranceUtils;
import org.apache.linkis.rpc.Sender;
import org.apache.linkis.scheduler.queue.Job;
import org.apache.linkis.scheduler.queue.SchedulerEventState;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scala.Option;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RequestMapping(path = "/dss/flow/entrance")
@RestController
public class FlowEntranceRestfulApi extends EntranceRestfulApi {

    private EntranceServer entranceServer;

    private static final Logger logger = LoggerFactory.getLogger(FlowEntranceRestfulApi.class);

    @Autowired
    private WorkflowExecutionInfoService workflowExecutionInfoService;
    @Autowired
    private TaskMapper taskMapper;
    @Override
    @EntranceServerBeanAnnotation.EntranceServerAutowiredAnnotation
    public void setEntranceServer(EntranceServer entranceServer) {
        super.setEntranceServer(entranceServer);
        this.entranceServer = entranceServer;
    }

    /**
     * The execute function handles the request submitted by the user to execute the task, and the execution ID is returned to the user.
     * execute函数处理的是用户提交执行任务的请求，返回给用户的是执行ID
     * json Incoming key-value pair(传入的键值对)
     * Repsonse
     */
    @Override
    @RequestMapping(value = "/execute",method = RequestMethod.POST)
    public Message execute(HttpServletRequest req, @RequestBody Map<String, Object> json) {
        Message message = null;
//        try{
        logger.info("Begin to execute workflow, the request params is: {}.", json);
        DSSWorkspace workspace = SSOHelper.getWorkspace(req);
        String umUser = SecurityFilter.getLoginUsername(req);
        json.put(TaskConstant.UMUSER, umUser);
        json.put(TaskConstant.EXECUTE_USER, umUser);
        Map<String, Object> params = (Map<String, Object>) json.get("params");
        //失败节点重新执行功能，通过isReExecute字段来判断
        if (json.containsKey(ExecuteStrategyEnum.IS_RE_EXECUTE.getName())) {
            params.put("executeStrategy", ExecuteStrategyEnum.IS_RE_EXECUTE.getValue());
            try {
                String flowIdStr = BDPJettyServerHelper.jacksonJson().readValue(json.get("executionCode").toString(), Map.class).get("flowId").toString();
                String succeedJobIdsStr = workflowExecutionInfoService.getSucceedJobsByFlowId(Long.parseLong(flowIdStr));
                if (StringUtils.isNotEmpty(succeedJobIdsStr)) {
                    params.put("nodeID", succeedJobIdsStr);
                }
            } catch (Exception e) {
                logger.error("re execute job failed:", e);
            }
        } else if (json.containsKey(ExecuteStrategyEnum.IS_SELECTED_EXECUTE.getName())) {
            //选中节点执行功能，通过isSelectedExecute字段来判断
            params.put("executeStrategy", ExecuteStrategyEnum.IS_SELECTED_EXECUTE.getValue());
            String nodeID = Optional.ofNullable(json.get("nodeID").toString()).orElseThrow(() -> {
                logger.error("execute selected node failed because nodeID is empty");
                return new NullPointerException();
            });
            params.put("nodeID", nodeID);
        } else {
            //默认为执行功能
            params.put("executeStrategy", ExecuteStrategyEnum.IS_EXECUTE.getValue());
        }
        params.put("workspace", workspace);
        String label = ((Map<String, Object>) json.get(DSSCommonUtils.DSS_LABELS_KEY)).get("route").toString();
        params.put(DSSCommonUtils.DSS_LABELS_KEY, label);
        params.put(DSSCommonUtils.DSS_EXECUTE_BY_PROXY_USER_KEY, FlowExecutionConf.DSS_EXECUTE_BY_PROXY_USER_ENABLE.getValue());
        logger.info("submit to entranceServer content:{}",json);
        Job job = entranceServer.execute(json);
        String execID = job.getId();
        JobRequest task = ((EntranceJob) job).getJobRequest();
        Long taskID = task.getId();
        pushLog(LogUtils.generateInfo("You have submitted a new job, script code (after variable substitution) is"), job);
        pushLog("************************************SCRIPT CODE************************************", job);
        pushLog(task.getExecutionCode(), job);
        pushLog("************************************SCRIPT CODE************************************", job);
        pushLog(LogUtils.generateInfo("Your job is accepted,  jobID is " + execID + ", taskID is " + taskID + ", and labels is " + label + ". Please wait it to be scheduled"), job);
        execID = ZuulEntranceUtils.generateExecID(execID, Sender.getThisServiceInstance().getApplicationName(), new String[]{Sender.getThisInstance()});
        message = Message.ok();
        message.setMethod("/api/entrance/execute");
        message.data("execID", execID);
        message.data("taskID", taskID);
        logger.info("End to get an an execID: {}, taskID: {}, labels: {}.", execID, taskID, label);
        return message;
    }

    @Override
    @RequestMapping(value = "/{id}/status",method = RequestMethod.GET)
    public Message status(HttpServletRequest req, @PathVariable("id") String id, @RequestParam(required = false, name = "taskID") String taskID) {
        logger.info("Begin to get status for execId:{}", id);
        Message message = null;
        String realId = ZuulEntranceUtils.parseExecID(id)[3];
        Option<Job> job;
        try {
            job = entranceServer.getJob(realId);
        } catch (Exception e) {
            logger.warn("获取任务 {} 状态时出现错误", realId, e);
            //如果获取错误了,证明在内存中已经没有了,去flow task表里面找寻一下taskID代表的任务的状态，然后返回
            long realTaskID = Long.parseLong(taskID);
            WorkflowQueryTask queryTask = new WorkflowQueryTask();
            queryTask.setTaskID(realTaskID);
            List<WorkflowQueryTask> taskList = taskMapper.selectTask(queryTask);
            message = Message.ok();
            if (null == taskList || taskList.size() != 1) {
                int size = 0;
                if (null != taskList) {
                    size = taskList.size();
                }
                logger.error("Got {} task for taskID : {}", size, realTaskID);
                message = Message.error("Got " + size + " task for taskId : " + realTaskID);
            } else {
                String status = taskList.get(0).getStatus();
                message.data("status", status).data("execID", id);
            }
            message.setMethod("/api/entrance/" + id + "/status");
            return message;
        }
        if (job.isDefined()) {
            message = Message.ok();
            message.setMethod("/api/entrance/" + id + "/status");
            message.data("status", job.get().getState().toString()).data("execID", id);
        } else {
            message = Message.error("ID The corresponding job is empty and cannot obtain the corresponding task status.(ID 对应的job为空，不能获取相应的任务状态)");
        }
        logger.info("End to get status for execId:{}", id);
        return message;
    }
    @Override
    @RequestMapping(path = {"/{id}/kill"},method = {RequestMethod.GET})
    public Message kill(HttpServletRequest req, @PathVariable("id") String id, @RequestParam(value = "taskID",required = false) Long taskID) {
        logger.info("Begin to kill task:{}", id);
        String realId = ZuulEntranceUtils.parseExecID(id)[3];
        Option job;
        try {
            job = this.entranceServer.getJob(realId);
        } catch (Exception var10) {
            logger.warn("can not find a job in entranceServer, will force to kill it", var10);
            JobHistoryHelper.forceKill(taskID);
            Message message = Message.ok("Forced Kill task (强制杀死任务)");
            message.setMethod("/api/entrance/" + id + "/kill");
            message.setStatus(0);
            return message;
        }
        Message message;
        if (job.isEmpty()) {
            logger.warn("can not find a job in entranceServer, will force to kill it");
            JobHistoryHelper.forceKill(taskID);
            message = Message.ok("Forced Kill task (强制杀死任务)");
            message.setMethod("/api/entrance/" + id + "/kill");
            message.setStatus(0);
            return message;
        } else {
            try {
                logger.info("begin to kill job {} ", ((Job)job.get()).getId());
                ((Job)job.get()).kill();
                message = Message.ok("Successfully killed the job(成功kill了job)");
                message.setMethod("/api/entrance/" + id + "/kill");
                message.setStatus(0);
                message.data("execID", id);
                if (job.get() instanceof EntranceJob) {
                    EntranceJob entranceJob = (EntranceJob)job.get();
                    JobRequest jobReq = entranceJob.getJobRequest();
                    entranceJob.updateJobRequestStatus(SchedulerEventState.Cancelled().toString());
                    this.entranceServer.getEntranceContext().getOrCreatePersistenceManager().createPersistenceEngine().updateIfNeeded(jobReq);
                }
                logger.info("end to kill job {} ", ((Job)job.get()).getId());
            } catch (Throwable var9) {
                logger.error("kill job {} failed ", ((Job)job.get()).getId(), var9);
                message = Message.error("An exception occurred while killing the job, kill failed(kill job的时候出现了异常，kill失败)");
                message.setMethod("/api/entrance/" + id + "/kill");
                message.setStatus(1);
            }
            return message;
        }
    }

    private void pushLog(String log, Job job) {
        entranceServer.getEntranceContext().getOrCreateLogManager().onLogUpdate(job, log);
    }
}
