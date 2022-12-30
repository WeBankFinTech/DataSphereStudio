package com.webank.wedatasphere.dss.datamodel.center.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.dss.datamodel.center.common.filter.AuthFilter;
import com.webank.wedatasphere.dss.framework.workspace.client.impl.LinkisWorkSpaceRemoteClient;
import org.apache.linkis.httpclient.authentication.AuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.UJESClientImpl;
import com.webank.wedatasphere.warehouse.client.GovernanceDwRemoteClient;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


@Configuration
public class CommonConfig {

    @Bean("myMetaObjectHandler")
    public MetaObjectHandler myMetaObjectHandler() {

        //TODO 无法自动组装生效
        return new MyMetaObjectHandler();
    }

    @Bean
    public GovernanceDwRemoteClient getGovernanceDwRemoteClient() throws Exception {
        System.out.println("url: " + DataWarehouseGovernanceConfig.SERVER_URL.getValue());
        AuthenticationStrategy authenticationStrategy = (AuthenticationStrategy) Class.forName(DataWarehouseGovernanceConfig.AUTHENTICATION_STRATEGY.getValue()).newInstance();
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) DWSClientConfigBuilder.newBuilder()
                .addServerUrl(DataWarehouseGovernanceConfig.SERVER_URL.getValue())
                .connectionTimeout(DataWarehouseGovernanceConfig.CONNECTION_TIMEOUT.getValue())
                .discoveryEnabled(DataWarehouseGovernanceConfig.DISCOVERY_ENABLED.getValue())
                .discoveryFrequency(DataWarehouseGovernanceConfig.DISCOVERY_FREQUENCY_PERIOD.getValue(), TimeUnit.MINUTES)
                .loadbalancerEnabled(DataWarehouseGovernanceConfig.LOAD_BALANCER_ENABLED.getValue())
                .maxConnectionSize(DataWarehouseGovernanceConfig.MAX_CONNECTION_SIZE.getValue())
                .retryEnabled(DataWarehouseGovernanceConfig.RETRY_ENABLED.getValue())
                .readTimeout(DataWarehouseGovernanceConfig.READ_TIMEOUT.getValue())
                .setAuthenticationStrategy(authenticationStrategy)
                .setAuthTokenKey(DataWarehouseGovernanceConfig.AUTHTOKEN_KEY.getValue())
                .setAuthTokenValue(DataWarehouseGovernanceConfig.AUTHTOKEN_VALUE.getValue())
        ).setDWSVersion(DataWarehouseGovernanceConfig.DWS_VERSION.getValue())
                .build();
        return new GovernanceDwRemoteClient(clientConfig);
    }

    @Bean
    public LinkisDataAssetsRemoteClient linkisDataAssetsRemoteClient() throws Exception {
        AuthenticationStrategy authenticationStrategy = (AuthenticationStrategy) Class.forName(DataWarehouseAssetsRemoteConfig.AUTHENTICATION_STRATEGY.getValue()).newInstance();
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) DWSClientConfigBuilder.newBuilder()
                .addServerUrl(DataWarehouseAssetsRemoteConfig.SERVER_URL.getValue())
                .connectionTimeout(DataWarehouseAssetsRemoteConfig.CONNECTION_TIMEOUT.getValue())
                .discoveryEnabled(DataWarehouseAssetsRemoteConfig.DISCOVERY_ENABLED.getValue())
                .discoveryFrequency(DataWarehouseAssetsRemoteConfig.DISCOVERY_FREQUENCY_PERIOD.getValue(), TimeUnit.MINUTES)
                .loadbalancerEnabled(DataWarehouseAssetsRemoteConfig.LOAD_BALANCER_ENABLED.getValue())
                .maxConnectionSize(DataWarehouseAssetsRemoteConfig.MAX_CONNECTION_SIZE.getValue())
                .retryEnabled(DataWarehouseAssetsRemoteConfig.RETRY_ENABLED.getValue())
                .readTimeout(DataWarehouseAssetsRemoteConfig.READ_TIMEOUT.getValue())
                .setAuthenticationStrategy(authenticationStrategy)
                .setAuthTokenKey(DataWarehouseAssetsRemoteConfig.AUTHTOKEN_KEY.getValue())
                .setAuthTokenValue(DataWarehouseAssetsRemoteConfig.AUTHTOKEN_VALUE.getValue())
        ).setDWSVersion(DataWarehouseAssetsRemoteConfig.DWS_VERSION.getValue())
                .build();
        return new LinkisDataAssetsRemoteClient(clientConfig);
    }

    @Bean
    public LinkisWorkSpaceRemoteClient linkisWorkSpaceRemoteClient() throws Exception {
        AuthenticationStrategy authenticationStrategy = (AuthenticationStrategy) Class.forName(DataWorkspaceRemoteConfig.AUTHENTICATION_STRATEGY.getValue()).newInstance();
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) DWSClientConfigBuilder.newBuilder()
                .addServerUrl(DataWorkspaceRemoteConfig.SERVER_URL.getValue())
                .connectionTimeout(DataWorkspaceRemoteConfig.CONNECTION_TIMEOUT.getValue())
                .discoveryEnabled(DataWorkspaceRemoteConfig.DISCOVERY_ENABLED.getValue())
                .discoveryFrequency(DataWorkspaceRemoteConfig.DISCOVERY_FREQUENCY_PERIOD.getValue(), TimeUnit.MINUTES)
                .loadbalancerEnabled(DataWorkspaceRemoteConfig.LOAD_BALANCER_ENABLED.getValue())
                .maxConnectionSize(DataWorkspaceRemoteConfig.MAX_CONNECTION_SIZE.getValue())
                .retryEnabled(DataWorkspaceRemoteConfig.RETRY_ENABLED.getValue())
                .readTimeout(DataWorkspaceRemoteConfig.READ_TIMEOUT.getValue())
                .setAuthenticationStrategy(authenticationStrategy)
                .setAuthTokenKey(DataWorkspaceRemoteConfig.AUTHTOKEN_KEY.getValue())
                .setAuthTokenValue(DataWorkspaceRemoteConfig.AUTHTOKEN_VALUE.getValue())
        ).setDWSVersion(DataWorkspaceRemoteConfig.DWS_VERSION.getValue())
                .build();
        return new LinkisWorkSpaceRemoteClient(clientConfig);
    }



    @Bean
    public UJESClient ujesClient()throws Exception {
        AuthenticationStrategy authenticationStrategy = (AuthenticationStrategy) Class.forName(LinkisJobConfiguration.AUTHENTICATION_STRATEGY.getValue()).newInstance();
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder().addServerUrl(LinkisJobConfiguration.LINKIS_SERVER_URL.getValue())
                .connectionTimeout(LinkisJobConfiguration.CONNECTION_TIMEOUT.getValue())
                .discoveryEnabled(LinkisJobConfiguration.DISCOVERY_ENABLED.getValue())
                .discoveryFrequency(LinkisJobConfiguration.DISCOVERY_FREQUENCY_PERIOD.getValue(), TimeUnit.MINUTES)
                .loadbalancerEnabled(LinkisJobConfiguration.LOAD_BALANCER_ENABLED.getValue())
                .maxConnectionSize(LinkisJobConfiguration.MAX_CONNECTION_SIZE.getValue())
                .retryEnabled(LinkisJobConfiguration.RETRY_ENABLED.getValue())
                .readTimeout(LinkisJobConfiguration.READ_TIMEOUT.getValue())
                .setAuthenticationStrategy(authenticationStrategy)
                .setAuthTokenKey(LinkisJobConfiguration.AUTHTOKEN_KEY.getValue())
                .setAuthTokenValue(LinkisJobConfiguration.AUTHTOKEN_VALUE.getValue())))
                .setDWSVersion(LinkisJobConfiguration.DWS_VERSION.getValue()).build();
        return new UJESClientImpl(clientConfig);
    }

    @Resource
    private AuthFilter authFilter;

    @Bean
    public FilterRegistrationBean<AuthFilter> registrationBean(){
        //通过FilterRegistrationBean实例设置优先级可以生效
        //通过@WebFilter无效
        FilterRegistrationBean<AuthFilter> bean = new FilterRegistrationBean<AuthFilter>();
        bean.setFilter(authFilter);//注册自定义过滤器
        bean.setName("authFilter1");//过滤器名称
        bean.addUrlPatterns("/*");//过滤所有路径
        bean.setOrder(1);//优先级，最顶级
        return bean;
    }

}
