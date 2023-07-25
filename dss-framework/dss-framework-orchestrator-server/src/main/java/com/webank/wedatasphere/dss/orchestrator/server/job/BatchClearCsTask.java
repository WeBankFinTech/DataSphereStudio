package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Description 定时清理ContextId的任务
 */
@Component
@EnableScheduling
public class BatchClearCsTask {

    @Autowired
    private OrchestratorService  orchestratorService;

    @Scheduled(cron = "#{@getBatchClearCsTaskCron}")
    public void batchClearCsTask(){
        orchestratorService.batchClearContextId();

    }
}
