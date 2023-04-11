package com.webank.wedatasphere.dss.flow.execution.entrance.job;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.flow.execution.entrance.dao.TaskMapper;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowQueryTask;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorPublishJob;
import com.webank.wedatasphere.dss.sender.service.conf.DSSSenderServiceConf;
import org.apache.linkis.common.ServiceInstance;
import org.apache.linkis.rpc.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description monitor workflow execute job if active
 */
@Component
@EnableScheduling
public class CheckWorkflowExecuteTask {

    @Autowired
    private TaskMapper taskMapper;

    List<WorkflowQueryTask> failedJobs;

    @Scheduled(cron = "#{@getCheckInstanceIsActiveCron}")
    public void checkOrchestratorConversionJob() {

        ServiceInstance[] allActionInstances = Sender.getInstances(DSSSenderServiceConf.DSS_SERVER_NAME.getValue());
        if (allActionInstances.length == DSSCommonConf.DSS_INSTANCE_NUMBERS.getValue()){
            return;
        }

        List<WorkflowQueryTask> maybeFailedJobs = taskMapper.search(null, null, Arrays.asList(JobStatus.Inited.getStatus(), JobStatus.Running.getStatus()), null, null, null, null, null);

        List<String> activeInstance = Arrays.stream(allActionInstances).map(ServiceInstance::getInstance).collect(Collectors.toList());

        for (WorkflowQueryTask maybeFailedJob : maybeFailedJobs) {
            if (!activeInstance.contains(maybeFailedJob.getInstance())) {
                maybeFailedJob.setStatus(JobStatus.Failed.getStatus());
                failedJobs.add(maybeFailedJob);
            }
        }

        // update execute job status to failed
        taskMapper.batchUpdateTasks(failedJobs);

        // send alter

    }
}
