/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.orchestrator.server.conf;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorOperateService;
import com.webank.wedatasphere.dss.orchestrator.server.service.impl.OrchestratorOperateServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledThreadPoolExecutor;


@Configuration
public class OrchestratorSpringConf {

    @Bean(name = "contextService")
    public ContextService createContextService() {
        return ContextServiceImpl.getInstance();
    }

    @Bean(name = "orchestratorBmlService")
    public BMLService createBmlService() {
        return BMLService.getInstance();
    }

    @Bean
    public ScheduledThreadPoolExecutor dssDefaultScheduledExecutor() {
        return new ScheduledThreadPoolExecutor(30,
                new ThreadFactoryBuilder().setNameFormat("Dss-Default-Spring-Scheduler-Thread-%d").build());
    }

    @Bean
    public String getBatchClearCsTaskCron() {
        return OrchestratorConf.DSS_CS_CLEAR_CRON.getValue();
    }

    @Bean
    @ConditionalOnMissingBean
    public OrchestratorOperateService createOrchestratorOperateService(){
        return new OrchestratorOperateServiceImpl();
    }

}
