package com.webank.wedatasphere.dss.orchestrator.server.service.impl;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorJobMapper;
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
 * Description 监控工作流转换的任务是否在节点执行异常
 */
@Component
@EnableScheduling
public class CheckOrchestratorConversionJobTask {

    @Autowired
    private OrchestratorJobMapper orchestratorJobMapper;

    List<OrchestratorPublishJob> failedJobs;

    @Scheduled(cron = "#{@getCheckOrchestratorConversionJobTaskCron}")
    public void checkOrchestratorConversionJob() {

        ServiceInstance[] allActionInstances = Sender.getInstances(DSSSenderServiceConf.DSS_SERVER_NAME.getValue());
        if (allActionInstances.length == DSSCommonConf.DSS_INSTANCE_NUMBERS.getValue()){
         return;
        }

        List<OrchestratorPublishJob> maybeFailedJobs = orchestratorJobMapper.getPublishJobByJobStatuses
                (Arrays.asList(JobStatus.Inited.getIndex(), JobStatus.Running.getIndex()));

        List<String> activeInstance = Arrays.stream(allActionInstances).map(ServiceInstance::getInstance).collect(Collectors.toList());

        for (OrchestratorPublishJob maybeFailedJob : maybeFailedJobs) {
            if (!activeInstance.contains(maybeFailedJob.getInstanceName())) {
                maybeFailedJob.setStatus(JobStatus.Failed.getIndex());
                failedJobs.add(maybeFailedJob);
            }
        }

        // update publish job status to failed
        orchestratorJobMapper.batchUpdatePublishJob(failedJobs);

        // send alter

    }



}
