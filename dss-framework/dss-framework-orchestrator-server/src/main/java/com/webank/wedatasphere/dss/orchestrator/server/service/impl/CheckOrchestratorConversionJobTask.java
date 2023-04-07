package com.webank.wedatasphere.dss.orchestrator.server.service.impl;

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
        //TODO 修改PROJECT_SERVER_NAME到对应的值
        ServiceInstance[] allActionInstances = Sender.getInstances(DSSSenderServiceConf.PROJECT_SERVER_NAME.getValue());
        //TODO 节点数也要从配置文件中拿
        if (allActionInstances.length == 2){
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
