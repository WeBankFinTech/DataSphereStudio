package com.webank.wedatasphere.dss.scriptis.config;

import com.webank.wedatasphere.dss.scriptis.service.ScriptisAuthService;
import com.webank.wedatasphere.dss.scriptis.service.impl.ScriptisAuthServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

@Configuration
public class ScriptisBeanConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ScriptisAuthService getScriptisAuthService(){
        return new ScriptisAuthServiceImpl();
    }
}
