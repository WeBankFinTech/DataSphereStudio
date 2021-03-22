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

package com.webank.wedatasphere.dss.flow.execution.entrance.restful;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.linkis.common.log.LogUtils;
import com.webank.wedatasphere.linkis.entrance.EntranceServer;
import com.webank.wedatasphere.linkis.entrance.annotation.EntranceServerBeanAnnotation;
import com.webank.wedatasphere.linkis.entrance.execute.EntranceJob;
import com.webank.wedatasphere.linkis.entrance.restful.EntranceRestfulApi;
import com.webank.wedatasphere.linkis.entrance.utils.JobHistoryHelper;
import com.webank.wedatasphere.linkis.governance.common.entity.task.RequestPersistTask;
import com.webank.wedatasphere.linkis.protocol.constants.TaskConstant;
import com.webank.wedatasphere.linkis.protocol.task.Task;
import com.webank.wedatasphere.linkis.protocol.utils.ZuulEntranceUtils;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.linkis.scheduler.queue.Job;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import scala.Option;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Map;


@Path("/dss/flow/entrance")
@Component
public class FlowEntranceRestfulApi extends EntranceRestfulApi {

    private EntranceServer entranceServer;

    private static final Logger logger = LoggerFactory.getLogger(FlowEntranceRestfulApi.class);

    @Override
    @EntranceServerBeanAnnotation.EntranceServerAutowiredAnnotation
    public void setEntranceServer(EntranceServer entranceServer){
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
    @POST
    @Path("/execute")
    public Response execute(@Context HttpServletRequest req, Map<String, Object> json) {
        Message message = null;
//        try{
        logger.info("Begin to get an execID");
        Workspace workspace = SSOHelper.getWorkspace(req);
        json.put(TaskConstant.UMUSER, SecurityFilter.getLoginUsername(req));
        Map<String, Object> params = (Map<String, Object>) json.get("params");
        params.put("workspace", workspace);
        String label = ((Map<String, Object>) json.get("labels")).get("route").toString();
        params.put("labels", label);
        String execID = entranceServer.execute(json);
        Job job = entranceServer.getJob(execID).get();
        Task task = ((EntranceJob) job).getTask();
        Long taskID = ((RequestPersistTask) task).getTaskID();
        pushLog(LogUtils.generateInfo("You have submitted a new job, script code (after variable substitution) is"), job);
        pushLog("************************************SCRIPT CODE************************************", job);
        pushLog(((RequestPersistTask) task).getCode(), job);
        pushLog("************************************SCRIPT CODE************************************", job);
        pushLog(LogUtils.generateInfo("Your job is accepted,  jobID is " + execID + " and taskID is " + taskID + ". Please wait it to be scheduled"), job);
        execID = ZuulEntranceUtils.generateExecID(execID, Sender.getThisServiceInstance().getApplicationName(), new String[]{Sender.getThisInstance()});
        message = Message.ok();
        message.setMethod("/api/entrance/execute");
        message.data("execID", execID);
        message.data("taskID", taskID);
        logger.info("End to get an an execID: {}, taskID: {}", execID, taskID);
//        }catch(ErrorException e){
//            message = Message.error(e.getDesc());
//            message.setStatus(1);
//            message.setMethod("/api/entrance/execute");
//        }
        return Message.messageToResponse(message);

    }

    @Override
    @GET
    @Path("/{id}/status")
    public Response status(@PathParam("id") String id, @QueryParam("taskID") String taskID) {
        Message message = null;
        String realId = ZuulEntranceUtils.parseExecID(id)[3];
        Option<Job> job = Option.apply(null);
        try {
            job = entranceServer.getJob(realId);
        } catch (Exception e) {
            logger.warn("获取任务 {} 状态时出现错误", realId, e);
            //如果获取错误了,证明在内存中已经没有了,去jobhistory找寻一下taskID代表的任务的状态，然后返回
            long realTaskID = Long.parseLong(taskID);
            String status = JobHistoryHelper.getStatusByTaskID(realTaskID);
            message = Message.ok();
            message.setMethod("/api/entrance/" + id + "/status");
            message.data("status", status).data("execID", id);
            return Message.messageToResponse(message);
        }
        if (job.isDefined()) {
            message = Message.ok();
            message.setMethod("/api/entrance/" + id + "/status");
            message.data("status", job.get().getState().toString()).data("execID", id);
        } else {
            message = Message.error("ID The corresponding job is empty and cannot obtain the corresponding task status.(ID 对应的job为空，不能获取相应的任务状态)");
        }
        return Message.messageToResponse(message);
    }

    private void pushLog(String log, Job job) {
        entranceServer.getEntranceContext().getOrCreateLogManager().onLogUpdate(job, log);
    }

}
