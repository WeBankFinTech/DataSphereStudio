package com.webank.wedatasphere.dss.flow.execution.entrance.job;

import com.webank.wedatasphere.dss.common.alter.ExecuteAlter;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.entity.CustomAlter;
import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.flow.execution.entrance.dao.TaskMapper;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowQueryTask;
import com.webank.wedatasphere.dss.sender.service.conf.DSSSenderServiceConf;
import org.apache.linkis.common.ServiceInstance;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description monitor workflow execute job if active
 */
@Component
@EnableScheduling
public class CheckWorkflowExecuteTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckWorkflowExecuteTask.class);
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ExecuteAlter executeAlter;

    @Scheduled(cron = "#{@getCheckInstanceIsActiveCron}")
    public void checkWorkflowExecuteTask() {

        ServiceInstance[] allActionInstances = Sender.getInstances(DSSSenderServiceConf.DSS_SERVER_NAME.getValue());
        if (allActionInstances.length == DSSCommonConf.DSS_INSTANCE_NUMBERS.getValue()){
            LOGGER.info("All instances are active!");
            return;
        }

        List<WorkflowQueryTask> maybeFailedJobs = taskMapper.search(null, null, Arrays.asList(JobStatus.Inited.getStatus(), JobStatus.Running.getStatus()), null, null, null, null, null);
        LOGGER.info("These tasks maybe are failed. " + maybeFailedJobs.toString());
        List<String> activeInstance = Arrays.stream(allActionInstances).map(ServiceInstance::getInstance).collect(Collectors.toList());
        LOGGER.info("Active instances are " + activeInstance);
        List<WorkflowQueryTask> failedJobs = new ArrayList<>();
        if (maybeFailedJobs.size() > 0) {
            for (WorkflowQueryTask maybeFailedJob : maybeFailedJobs) {
                if (!activeInstance.contains(maybeFailedJob.getInstance())) {
                    maybeFailedJob.setStatus(JobStatus.Failed.getStatus());
                    failedJobs.add(maybeFailedJob);
                }
            }
        }

        // update execute job status to failed
        if (failedJobs.size() > 0) {
            taskMapper.batchUpdateTasks(failedJobs);
            List<String> exceptionInstances = failedJobs.stream().map(WorkflowQueryTask::getInstance).distinct().collect(Collectors.toList());
            List<Long> exceptionId = failedJobs.stream().map(WorkflowQueryTask::getTaskID).collect(Collectors.toList());
            failedJobs.clear();
            // send alter
            CustomAlter customAlter = new CustomAlter("DSS exception of instance: " + exceptionInstances,
                    "以下taskId的工作流执行失败，请到表dss_workflow_task查看失败的工作流信息：" + exceptionId,
                    "1", DSSCommonConf.ALTER_RECEIVER.getValue());
            executeAlter.sendAlter(customAlter);
        }
    }
}
