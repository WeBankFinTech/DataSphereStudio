package com.webank.wedatasphere.dss.orchestrator.publish.configuration;

import com.webank.wedatasphere.dss.orchestrator.db.hook.AddOrchestratorVersionHook;
import com.webank.wedatasphere.dss.orchestrator.db.hook.AddOrchestratorVersionHookDefaultImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: xlinliu
 * Date: 2023/8/1
 */
@Configuration
public class DssOrchstratorPublishSpringConfiguration {
    @Bean
    @ConditionalOnMissingBean
    AddOrchestratorVersionHook getAddOrchestratorVersionHook(){
        return new AddOrchestratorVersionHookDefaultImpl();
    }
}
