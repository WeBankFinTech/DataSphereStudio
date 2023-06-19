package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.webank.wedatasphere.dss.common.alter.ExecuteAlter;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.entity.CustomAlter;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
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

import javax.annotation.PostConstruct;
import java.util.*;
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

    @PostConstruct
    public void checkSelfExecuteTasks() {
        LOGGER.info("CheckOrchestratorCopyTask: Start checking for tasks that are still running after instance exceptions");
        String thisInstance = Sender.getThisInstance();
        List<DSSOrchestratorCopyInfo> maybeFailedJobs = orchestratorCopyJobMapper.getRunningJob();
        List<DSSOrchestratorCopyInfo> failedJobs = maybeFailedJobs.stream().filter(t -> Objects.equals(t.getInstanceName(), thisInstance))
                .peek(t -> {
                    t.setExceptionInfo("执行复制的实例异常，请稍后重试！");
                    t.setStatus(0);
                    t.setEndTime(new Date());
                    t.setIsCopying(0);
                }).collect(Collectors.toList());
        if (failedJobs.size() > 0){
            LOGGER.warn("实例启动阶段，以下工作流复制任务因该实例异常导致失败！{}", DSSCommonUtils.COMMON_GSON.toJson(failedJobs));
            orchestratorCopyJobMapper.batchUpdateCopyJob(failedJobs);
        }
    }

    @Scheduled(cron = "#{@getCheckInstanceIsActiveCron}")
    public void checkOrchestratorCopyJobTask() {

        ServiceInstance[] allActionInstances = Sender.getInstances(DSSSenderServiceConf.CURRENT_DSS_SERVER_NAME.getValue());
        List<DSSOrchestratorCopyInfo> maybeFailedJobs = orchestratorCopyJobMapper.getRunningJob();
        LOGGER.info("These tasks are maybe failed. " + maybeFailedJobs.toString());
        List<String> activeInstance = Arrays.stream(allActionInstances).map(ServiceInstance::getInstance).collect(Collectors.toList());
        LOGGER.info("Active instances are " + activeInstance);
        List<DSSOrchestratorCopyInfo> failedJobs = new ArrayList<>();
        if (maybeFailedJobs.size() > 0) {
            failedJobs = maybeFailedJobs.stream().filter(t -> !activeInstance.contains(t.getInstanceName()))
                    .peek(t -> {
                        t.setExceptionInfo("执行复制的实例异常，请稍后重试！");
                        t.setStatus(0);
                        t.setEndTime(new Date());
                        t.setIsCopying(0);
                    }).collect(Collectors.toList());
        }

        // update copy job status to failed
        if (failedJobs.size() > 0) {
            LOGGER.warn("以下工作流复制任务因执行实例异常导致失败！{}", DSSCommonUtils.COMMON_GSON.toJson(failedJobs));
            orchestratorCopyJobMapper.batchUpdateCopyJob(failedJobs);
            List<String> exceptionInstances = failedJobs.stream().map(DSSOrchestratorCopyInfo::getInstanceName).distinct().collect(Collectors.toList());
            List<String> exceptionId = failedJobs.stream().map(DSSOrchestratorCopyInfo::getId).collect(Collectors.toList());
            failedJobs.clear();
            // send alter
            CustomAlter customAlter = new CustomAlter("DSS exception of instance: " + exceptionInstances,
                    "以下Id的工作流拷贝失败，请到表dss_orchestrator_copy_info查看失败的工作流信息：" + exceptionId,
                    "1", DSSCommonConf.ALTER_RECEIVER.getValue());
            executeAlter.sendAlter(customAlter);
        }
    }
}
