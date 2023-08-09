package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.webank.wedatasphere.dss.common.alter.ExecuteAlter;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.entity.CustomAlter;
import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
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

import javax.annotation.PostConstruct;
import java.util.*;
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

    @PostConstruct
    public void checkSelfExecuteTasks() {
        LOGGER.info("CheckOrchestratorConversionJobTask: Start checking for tasks that are still running after instance exceptions");
        String thisInstance = Sender.getThisInstance();
        List<OrchestratorPublishJob> maybeFailedJobs = orchestratorJobMapper.getPublishJobByJobStatuses
                (Arrays.asList(JobStatus.Inited.getStatus(), JobStatus.Running.getStatus()));
        List<OrchestratorPublishJob> failedJobs = maybeFailedJobs.stream().filter(t -> Objects.equals(t.getInstanceName(), thisInstance))
                .peek(t -> {
                    t.setErrorMsg("执行发布的实例异常，请重新发布！");
                    t.setStatus(JobStatus.Failed.getStatus());
                    t.setUpdateTime(new Date());
                }).collect(Collectors.toList());
        if (failedJobs.size() > 0){
            LOGGER.warn("实例启动阶段，以下工作流发布任务因为该实例异常导致发布失败！{}", DSSCommonUtils.COMMON_GSON.toJson(failedJobs));
            orchestratorJobMapper.batchUpdatePublishJob(failedJobs);
        }
    }

    @Scheduled(cron = "#{@getCheckInstanceIsActiveCron}")
    public void checkOrchestratorConversionJob() {

        ServiceInstance[] allActionInstances = Sender.getInstances(DSSSenderServiceConf.CURRENT_DSS_SERVER_NAME.getValue());
        List<OrchestratorPublishJob> maybeFailedJobs = orchestratorJobMapper.getPublishJobByJobStatuses
                (Arrays.asList(JobStatus.Inited.getStatus(), JobStatus.Running.getStatus()));
        LOGGER.info("These tasks are maybe failed. " + DSSCommonUtils.COMMON_GSON.toJson(maybeFailedJobs));
        List<String> activeInstance = Arrays.stream(allActionInstances).map(ServiceInstance::getInstance).collect(Collectors.toList());
        LOGGER.info("Active instances are " + activeInstance);
        List<OrchestratorPublishJob> failedJobs = new ArrayList<>();
        if (maybeFailedJobs.size() > 0) {
            failedJobs = maybeFailedJobs.stream().filter(t -> !activeInstance.contains(t.getInstanceName()))
                    .peek(t -> {
                        t.setStatus(JobStatus.Failed.getStatus());
                        t.setUpdateTime(new Date());
                        t.setErrorMsg("执行发布的实例异常，请重新发布！");
                    }).collect(Collectors.toList());
        }

        if (failedJobs.size() > 0) {
            // update publish job status to failed
            LOGGER.warn("以下工作流发布任务因为执行实例异常导致发布失败！{}", DSSCommonUtils.COMMON_GSON.toJson(failedJobs));
            orchestratorJobMapper.batchUpdatePublishJob(failedJobs);
            List<String> exceptionInstances = failedJobs.stream().map(OrchestratorPublishJob::getInstanceName).distinct().collect(Collectors.toList());
            List<Long> exceptionId = failedJobs.stream().map(OrchestratorPublishJob::getId).collect(Collectors.toList());
            failedJobs.clear();
            // send alter
            CustomAlter customAlter = new CustomAlter("DSS exception of instance: " + exceptionInstances,
                    "以下Id工作流发布失败，请到表dss_orchestrator_job_info查看失败的工作流信息：" + exceptionId,
                    "1", DSSCommonConf.ALTER_RECEIVER.getValue());
            executeAlter.sendAlter(customAlter);
        }
    }
}
