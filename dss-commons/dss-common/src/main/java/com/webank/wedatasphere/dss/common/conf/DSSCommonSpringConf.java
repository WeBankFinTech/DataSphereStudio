package com.webank.wedatasphere.dss.common.conf;

import com.webank.wedatasphere.dss.common.utils.AssembleCronUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DSSCommonSpringConf {

    @Bean
    public String getCheckInstanceIsActiveCron() {
        Integer value = DSSCommonConf.DSS_CHECK_SERVER_ACTIVE_PERIOD.getValue();
        return AssembleCronUtils.getCron(value);
    }
    @Bean
    public String getECInstanceReleaseCron(){
        Integer value=DSSCommonConf.DSS_EC_KILL_PERIOD.getValue();
        return AssembleCronUtils.getCron(value);
    }

}
