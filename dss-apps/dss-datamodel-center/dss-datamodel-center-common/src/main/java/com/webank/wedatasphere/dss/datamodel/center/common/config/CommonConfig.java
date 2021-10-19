package com.webank.wedatasphere.dss.datamodel.center.common.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.linkis.httpclient.config.ClientConfig;
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy;
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import com.webank.wedatasphere.warehouse.client.*;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Configuration
public class CommonConfig {

    @Bean("myMetaObjectHandler")
    public MetaObjectHandler myMetaObjectHandler() {

        //TODO 无法自动组装生效
        return new MyMetaObjectHandler();
    }

    @Bean
    public GovernanceDwRemoteClient getGovernanceDwRemoteClient() {
        System.out.println("url: " + DataWarehouseGovernanceConfig.SERVER_URL.getValue());
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) DWSClientConfigBuilder.newBuilder()
                .addServerUrl(DataWarehouseGovernanceConfig.SERVER_URL.getValue())
                .connectionTimeout(DataWarehouseGovernanceConfig.CONNECTION_TIMEOUT.getValue())
                .discoveryEnabled(DataWarehouseGovernanceConfig.DISCOVERY_ENABLED.getValue())
                .discoveryFrequency(DataWarehouseGovernanceConfig.DISCOVERY_FREQUENCY_PERIOD.getValue(), TimeUnit.MINUTES)
                .loadbalancerEnabled(DataWarehouseGovernanceConfig.LOAD_BALANCER_ENABLED.getValue())
                .maxConnectionSize(DataWarehouseGovernanceConfig.MAX_CONNECTION_SIZE.getValue())
                .retryEnabled(DataWarehouseGovernanceConfig.RETRY_ENABLED.getValue())
                .readTimeout(DataWarehouseGovernanceConfig.READ_TIMEOUT.getValue())
                .setAuthenticationStrategy(new StaticAuthenticationStrategy())
                .setAuthTokenKey(DataWarehouseGovernanceConfig.AUTHTOKEN_KEY.getValue())
                .setAuthTokenValue(DataWarehouseGovernanceConfig.AUTHTOKEN_VALUE.getValue())
        ).setDWSVersion(DataWarehouseGovernanceConfig.DWS_VERSION.getValue())
                .build();
        return new GovernanceDwRemoteClient(clientConfig);
    }

    @Bean
    public LinkisDataAssetsRemoteClient linkisDataAssetsRemoteClient(){
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) DWSClientConfigBuilder.newBuilder()
                .addServerUrl(DataWarehouseAssetsRemoteConfig.SERVER_URL.getValue())
                .connectionTimeout(DataWarehouseAssetsRemoteConfig.CONNECTION_TIMEOUT.getValue())
                .discoveryEnabled(DataWarehouseAssetsRemoteConfig.DISCOVERY_ENABLED.getValue())
                .discoveryFrequency(DataWarehouseAssetsRemoteConfig.DISCOVERY_FREQUENCY_PERIOD.getValue(), TimeUnit.MINUTES)
                .loadbalancerEnabled(DataWarehouseAssetsRemoteConfig.LOAD_BALANCER_ENABLED.getValue())
                .maxConnectionSize(DataWarehouseAssetsRemoteConfig.MAX_CONNECTION_SIZE.getValue())
                .retryEnabled(DataWarehouseAssetsRemoteConfig.RETRY_ENABLED.getValue())
                .readTimeout(DataWarehouseAssetsRemoteConfig.READ_TIMEOUT.getValue())
                .setAuthenticationStrategy(new StaticAuthenticationStrategy())
                .setAuthTokenKey(DataWarehouseAssetsRemoteConfig.AUTHTOKEN_KEY.getValue())
                .setAuthTokenValue(DataWarehouseAssetsRemoteConfig.AUTHTOKEN_VALUE.getValue())
        ).setDWSVersion(DataWarehouseAssetsRemoteConfig.DWS_VERSION.getValue())
                .build();
        return  new LinkisDataAssetsRemoteClient(clientConfig);
    }

}
