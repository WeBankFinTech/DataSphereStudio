package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.webank.wedatasphere.dss.common.alter.ExecuteAlter;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.entity.CustomAlter;
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

    @Autowired
    private ExecuteAlter executeAlter;

    List<DSSOrchestratorCopyInfo> failedJobs;

    CustomAlter customAlter = new CustomAlter();

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
            List<String> exceptionInstances = failedJobs.stream().map(DSSOrchestratorCopyInfo::getInstanceName).distinct().collect(Collectors.toList());
            List<String> exceptionId = failedJobs.stream().map(DSSOrchestratorCopyInfo::getId).collect(Collectors.toList());
            failedJobs.clear();
            // send alter
            customAlter.setAlterTitle("DSS exception of instance: " + exceptionInstances);
            customAlter.setAlterInfo("以下id的工作流拷贝失败，请到表dss_orchestrator_copy_info查看失败的工作流信息：" + exceptionId);
            customAlter.setAlterLevel("1");
            customAlter.setAlterReceiver(DSSCommonConf.ALTER_RECEIVER.getValue());
            executeAlter.sendAlter(customAlter);
        }
    }
}
