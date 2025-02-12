package com.webank.wedatasphere.dss.orchestrator.server.service.impl;

import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Description
 */
@Component
@EnableScheduling
@PropertySource("classpath:dss-framework-orchestrator-server.properties")
public class BatchClearCsTask {
    @Autowired
    OrchestratorService  orchestratorService;

    @Scheduled(cron = "${wds.dss.server.scheduling.clear.cs.cron}")
    public void batchClearCsTask(){
        orchestratorService.batchClearContextId();
    }
}
