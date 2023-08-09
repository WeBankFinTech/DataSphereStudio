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
package com.webank.wedatasphere.dss.flow.execution.entrance.persistence;

import com.webank.wedatasphere.dss.flow.execution.entrance.service.WorkflowQueryService;
import com.webank.wedatasphere.dss.flow.execution.entrance.utils.FlowExecutionUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.entrance.exception.EntranceIllegalParamException;
import org.apache.linkis.entrance.exception.EntranceRPCException;
import org.apache.linkis.entrance.exception.QueryFailedException;
import org.apache.linkis.entrance.persistence.AbstractPersistenceEngine;
import org.apache.linkis.governance.common.entity.job.JobRequest;
import org.apache.linkis.governance.common.entity.job.SubJobDetail;
import org.apache.linkis.governance.common.entity.job.SubJobInfo;
import org.apache.linkis.governance.common.entity.task.*;
import org.apache.linkis.protocol.constants.TaskConstant;
import org.apache.linkis.protocol.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WorkflowPersistenceEngine extends AbstractPersistenceEngine {

    @Autowired
    private WorkflowQueryService workflowQueryService;
    private static final Logger logger = LoggerFactory.getLogger(WorkflowPersistenceEngine.class);

    {
        logger.info("WorkflowPersistenceEngine Registered");
    }

    public WorkflowPersistenceEngine() {

    }


    @Override
    public void persist(JobRequest jobRequest) throws QueryFailedException, EntranceIllegalParamException, EntranceRPCException {

        if (jobRequest == null) {
            throw new EntranceIllegalParamException(20004, "task can not be null, unable to do persist operation");
        }
        RequestInsertTask requestInsertTask = new RequestInsertTask();
        RequestPersistTask requestPersistTask = FlowExecutionUtils.jobRequest2RequestPersistTask(jobRequest);
        BeanUtils.copyProperties(requestPersistTask, requestInsertTask);
        ResponsePersist responsePersist = null;
        try {
            responsePersist = workflowQueryService.add(requestInsertTask,jobRequest);
        } catch (Exception e) {
            throw new EntranceRPCException(20020, "sender rpc failed", e);
        }

        if (responsePersist != null) {
            int status = responsePersist.getStatus();
            String message = responsePersist.getMsg();
            if (status != 0) {
                throw new QueryFailedException(20011, "insert task failed, reason: " + message);
            }
            Map<String, Object> data = responsePersist.getData();
            Object object = data.get(TaskConstant.TASKID);
            if (object == null) {
                throw new QueryFailedException(20011, "insert task failed, reason: " + message);
            }
            String taskStr = object.toString();
            Long taskID = Long.parseLong(taskStr);
            jobRequest.setId(taskID);

        } else {
            //Todo can be throw exception if it is not requestPersistTask(todo 如果不是 requestPersistTask的话，可以进行throw异常)
        }
    }


    //    @Override
//    public Task retrieve(Long taskID)throws EntranceIllegalParamException, QueryFailedException, EntranceRPCException {
//
//        Task task = null;
//
//        if ( taskID == null ||  taskID < 0){
//            throw new EntranceIllegalParamException(20003, "taskID can't be null or less than 0");
//        }
//
//        RequestQueryTask requestQueryTask = new RequestQueryTask();
//        requestQueryTask.setTaskID(taskID);
//        ResponsePersist responsePersist = null;
//        try {
////            responsePersist = (ResponsePersist) sender.ask(requestQueryTask);
//            responsePersist = workflowQueryService.query(requestQueryTask);
//        }catch(Exception e){
//            logger.error("Requesting the corresponding task failed with taskID: {}(通过taskID: {} 请求相应的task失败)", taskID, e);
//            throw new EntranceRPCException(20020, "sender rpc failed", e);
//        }
//        int status = responsePersist.getStatus();
//        //todo I want to discuss it again.(要再讨论下)
//        String message = responsePersist.getMsg();
//        if (status != 0){
//            logger.error("By taskID: {} request the corresponding task return status code is not 0, the query fails(通过taskID: {} 请求相应的task返回状态码不为0，查询失败)", taskID);
//            throw new QueryFailedException(20010, "retrieve task failed, reason: " + message);
//        }
//        Map<String, Object> data = responsePersist.getData();
//        if (data != null){
//           Object object = data.get(TaskConstant.TASK);
//           if (object instanceof List){
//               List list = (List)object;
//               if (list.size() == 0){
//                   logger.info("returned list length is 0, maybe there is no task corresponding to {}", taskID);
//               }else if (list.size() == 1){
//                   Object t = list.get(0);
//                   Gson gson = new Gson();
//                   String json = gson.toJson(t);
//                   task = gson.fromJson(json, RequestPersistTask.class);
//               }
//           }
//        }
//        return task;
//    }


    @Override
    public void updateIfNeeded(JobRequest jobRequest) throws EntranceRPCException, EntranceIllegalParamException {
        if (jobRequest == null) {
            throw new EntranceIllegalParamException(20004, "task can not be null, unable to do update operation");
        }

        RequestUpdateTask requestUpdateTask = new RequestUpdateTask();
        RequestPersistTask requestPersistTask = FlowExecutionUtils.jobRequest2RequestPersistTask(jobRequest);
        BeanUtils.copyProperties(requestPersistTask, requestUpdateTask);
        try {
            workflowQueryService.change(requestUpdateTask);
        } catch (Exception e) {
            logger.error("Request to update task with taskID {} failed, possibly due to RPC failure(请求更新taskID为 {} 的任务失败，原因可能是RPC失败)", requestUpdateTask.getTaskID(), e);
            throw new EntranceRPCException(20020, "sender rpc failed ", e);
        }

    }

    @Override
    public Task[] readAll(String instance) throws EntranceIllegalParamException, EntranceRPCException, QueryFailedException {

        List<Task> retList = new ArrayList<>();

        if (instance == null || "".equals(instance)) {
            throw new EntranceIllegalParamException(20004, "instance can not be null");
        }

        RequestReadAllTask requestReadAllTask = new RequestReadAllTask(instance);
        ResponsePersist responsePersist = null;
        try {
            //todo 目前没有使用到
//            responsePersist = (ResponsePersist)sender.ask(requestReadAllTask);
//            workflowQueryService.
        } catch (Exception e) {
            throw new EntranceRPCException(20020, "sender rpc failed ", e);
        }
        if (responsePersist != null) {
            int status = responsePersist.getStatus();
            String message = responsePersist.getMsg();
            if (status != 0) {
                throw new QueryFailedException(20011, "read all tasks failed, reason: " + message);
            }
            Map<String, Object> data = responsePersist.getData();
            Object object = data.get(TaskConstant.TASK);
            if (object instanceof List) {
                List list = (List) object;
                if (list.size() == 0) {
                    logger.info("no running task in this instance: {}", instance);
                }
                for (Object o : list) {
                    if (o instanceof RequestPersistTask) {
                        retList.add((RequestPersistTask) o);
                    }
                }
            }
        }
        return retList.toArray(new Task[0]);
    }

    @Override
    public JobRequest retrieveJobReq(Long jobGroupId) throws ErrorException {
        return null;
    }


    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }
}
