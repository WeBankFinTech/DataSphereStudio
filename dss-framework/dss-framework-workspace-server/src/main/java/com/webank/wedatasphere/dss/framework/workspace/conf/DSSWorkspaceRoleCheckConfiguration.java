package com.webank.wedatasphere.dss.framework.workspace.conf;

import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceAddUserHook;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleCheckService;
import com.webank.wedatasphere.dss.framework.workspace.service.impl.DSSWorkspaceAddUserHookImpl;
import com.webank.wedatasphere.dss.framework.workspace.service.impl.DSSWorkspaceRoleCheckServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DSSWorkspaceRoleCheckConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DSSWorkspaceRoleCheckService createDSSWorkspaceRoleCheckService(){
        return new DSSWorkspaceRoleCheckServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public DSSWorkspaceAddUserHook createDSSWorkspaceAddUserHook(){
        return new DSSWorkspaceAddUserHookImpl();
    }

}
