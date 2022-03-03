package com.webank.wedatasphere.warehouse.configuration;

import com.webank.wedatasphere.warehouse.dao.interceptor.DssWorkspaceNameAutoExtractQueryInterceptor;
import com.webank.wedatasphere.warehouse.dao.interceptor.DssWorkspaceNameAutoTransformUpdateInteceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public DssWorkspaceNameAutoTransformUpdateInteceptor dssWorkspaceNameAutoTransformUpdateInteceptor(
            DataSource dataSource
    ) {
        return new DssWorkspaceNameAutoTransformUpdateInteceptor(dataSource);
    }

    @Bean
    public DssWorkspaceNameAutoExtractQueryInterceptor dssWorkspaceNameAutoExtractQueryInterceptor(
            DataSource dataSource
    ) {
        return new DssWorkspaceNameAutoExtractQueryInterceptor(dataSource);
    }

}
