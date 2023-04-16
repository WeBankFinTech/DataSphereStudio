package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.webank.wedatasphere.dss.common.alter.ExecuteAlter;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.entity.CustomAlter;
import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorJobMapper;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorPublishJob;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description monitor workflow convert job if active
 */
@Component
@EnableScheduling
public class CheckOrchestratorConversionJobTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckOrchestratorConversionJobTask.class);
    @Autowired
    private OrchestratorJobMapper orchestratorJobMapper;

    @Autowired
    private ExecuteAlter executeAlter;

    @Scheduled(cron = "#{@getCheckInstanceIsActiveCron}")
    public void checkOrchestratorConversionJob() {

        ServiceInstance[] allActionInstances = Sender.getInstances(DSSSenderServiceConf.DSS_SERVER_NAME.getValue());
        if (allActionInstances.length == DSSCommonConf.DSS_INSTANCE_NUMBERS.getValue()){
            LOGGER.info("All instances are active!");
            return;
        }

        List<OrchestratorPublishJob> maybeFailedJobs = orchestratorJobMapper.getPublishJobByJobStatuses
                (Arrays.asList(JobStatus.Inited.getStatus(), JobStatus.Running.getStatus()));
        LOGGER.info("These tasks are maybe failed. " + maybeFailedJobs.toString());
        List<String> activeInstance = Arrays.stream(allActionInstances).map(ServiceInstance::getInstance).collect(Collectors.toList());
        LOGGER.info("Active instances are " + activeInstance);
        List<OrchestratorPublishJob> failedJobs = new ArrayList<>();
        if (maybeFailedJobs.size() > 0) {
            for (OrchestratorPublishJob maybeFailedJob : maybeFailedJobs) {
                if (!activeInstance.contains(maybeFailedJob.getInstanceName())) {
                    maybeFailedJob.setStatus(JobStatus.Failed.getStatus());
                    maybeFailedJob.setUpdateTime(new Date());
                    failedJobs.add(maybeFailedJob);
                }
            }
        }

        if (failedJobs.size() > 0) {
            // update publish job status to failed
            orchestratorJobMapper.batchUpdatePublishJob(failedJobs);
            List<String> exceptionInstances = failedJobs.stream().map(OrchestratorPublishJob::getInstanceName).distinct().collect(Collectors.toList());
            List<Long> exceptionId = failedJobs.stream().map(OrchestratorPublishJob::getId).collect(Collectors.toList());
            failedJobs.clear();
            // send alter
            CustomAlter customAlter = new CustomAlter("DSS exception of instance: " + exceptionInstances,
                    "以下id的工作流发布失败，请到表dss_orchestrator_job_info查看失败的工作流信息：" + exceptionId,
                    "1", DSSCommonConf.ALTER_RECEIVER.getValue());
            executeAlter.sendAlter(customAlter);
        }
    }
}
