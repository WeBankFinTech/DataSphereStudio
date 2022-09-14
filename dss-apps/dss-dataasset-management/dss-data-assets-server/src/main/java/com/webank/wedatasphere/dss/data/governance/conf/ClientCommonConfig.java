package com.webank.wedatasphere.dss.data.governance.conf;

import com.webank.wedatasphere.dss.framework.workspace.client.impl.LinkisWorkSpaceRemoteClient;
import org.apache.linkis.httpclient.authentication.AuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class ClientCommonConfig {

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
}
