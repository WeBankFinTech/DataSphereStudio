package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorCopyJobMapper;
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
 * Description monitor orchestrator copy job if active
 */
@Component
@EnableScheduling
public class CheckOrchestratorCopyJobTask {

    @Autowired
    private OrchestratorCopyJobMapper orchestratorCopyJobMapper;

    List<DSSOrchestratorCopyInfo> failedJobs;

    @Scheduled(cron = "#{@getCheckInstanceIsActiveCron}")
    public void checkOrchestratorCopyJobTask() {

        ServiceInstance[] allActionInstances = Sender.getInstances(DSSSenderServiceConf.DSS_SERVER_NAME.getValue());
        if (allActionInstances.length == DSSCommonConf.DSS_INSTANCE_NUMBERS.getValue()){
            return;
        }

        List<DSSOrchestratorCopyInfo> maybeFailedJobs = orchestratorCopyJobMapper.getRunningJob();

        List<String> activeInstance = Arrays.stream(allActionInstances).map(ServiceInstance::getInstance).collect(Collectors.toList());

        for (DSSOrchestratorCopyInfo maybeFailedJob : maybeFailedJobs) {
            if (!activeInstance.contains(maybeFailedJob.getInstanceName())) {
                maybeFailedJob.setStatus(0);
                maybeFailedJob.setIsCopying(0);
                maybeFailedJob.setExceptionInfo("Execute instance error, maybe the instance is hang up.");
                failedJobs.add(maybeFailedJob);
            }
        }

        // update copy job status to failed
        orchestratorCopyJobMapper.batchUpdateCopyJob(failedJobs);

        // send alter

    }

}
