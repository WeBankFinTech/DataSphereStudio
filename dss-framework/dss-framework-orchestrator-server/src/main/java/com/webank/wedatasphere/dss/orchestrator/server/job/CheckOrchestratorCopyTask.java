package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorCopyJobMapper;
import com.webank.wedatasphere.dss.sender.service.conf.DSSSenderServiceConf;
import org.apache.linkis.common.ServiceInstance;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description monitor orchestrator copy job if active
 */
@Component
@EnableScheduling
public class CheckOrchestratorCopyTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckOrchestratorCopyTask.class);
    @Autowired
    private OrchestratorCopyJobMapper orchestratorCopyJobMapper;

    List<DSSOrchestratorCopyInfo> failedJobs;

    @Scheduled(cron = "#{@getCheckInstanceIsActiveCron}")
    public void checkOrchestratorCopyJobTask() {

        ServiceInstance[] allActionInstances = Sender.getInstances(DSSSenderServiceConf.DSS_SERVER_NAME.getValue());
        if (allActionInstances.length == DSSCommonConf.DSS_INSTANCE_NUMBERS.getValue()){
            LOGGER.info("All instances are active!");
            return;
        }

        List<DSSOrchestratorCopyInfo> maybeFailedJobs = orchestratorCopyJobMapper.getRunningJob();
        LOGGER.info("These tasks are maybe failed. " + maybeFailedJobs.toString());
        List<String> activeInstance = Arrays.stream(allActionInstances).map(ServiceInstance::getInstance).collect(Collectors.toList());
        LOGGER.info("Active instances are " + activeInstance);
        if (maybeFailedJobs.size() > 0) {
            for (DSSOrchestratorCopyInfo maybeFailedJob : maybeFailedJobs) {
                if (!activeInstance.contains(maybeFailedJob.getInstanceName())) {
                    maybeFailedJob.setStatus(0);
                    maybeFailedJob.setIsCopying(0);
                    maybeFailedJob.setExceptionInfo("系统打盹，请稍后重试！");
                    failedJobs.add(maybeFailedJob);
                }
            }
        }

        // update copy job status to failed
        if (failedJobs.size() > 0) {
            orchestratorCopyJobMapper.batchUpdateCopyJob(failedJobs);
            failedJobs.clear();
        }

        // send alter

    }

}
