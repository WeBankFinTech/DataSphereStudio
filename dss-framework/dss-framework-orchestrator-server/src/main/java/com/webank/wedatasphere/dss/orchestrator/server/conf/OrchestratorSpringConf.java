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

import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
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
    public ScheduledThreadPoolExecutor scheduledExecutorService() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);
        return executor;
    }

    @Bean
    public String getCron() {
        return OrchestratorConf.DSS_CS_CLEAR_CRON.getValue();
    }


}
