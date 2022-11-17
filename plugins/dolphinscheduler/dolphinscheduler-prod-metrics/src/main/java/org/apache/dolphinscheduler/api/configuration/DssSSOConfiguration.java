package org.apache.dolphinscheduler.api.configuration;

import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.SSOPluginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author enjoyyin
 * @date 2022-11-16
 * @since 1.1.1
 */
@Configuration
public class DssSSOConfiguration {

    @Bean
    public FilterRegistrationBean<SSOPluginFilter> dssSSOInterceptor(@Autowired SSOPluginFilter ssoPluginFilter) {
        FilterRegistrationBean<SSOPluginFilter> filter = new FilterRegistrationBean<>();
        filter.setName("dssSSOFilter");
        filter.setFilter(ssoPluginFilter);
        filter.setOrder(-1);
        return filter;
    }
}
