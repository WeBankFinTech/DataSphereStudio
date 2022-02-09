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

package com.webank.wedatasphere.dss.linkis.node.execution.utils;

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import org.apache.linkis.common.exception.LinkisRetryException;
import org.apache.linkis.common.utils.RetryHandler;
import org.apache.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.UJESClientImpl;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LinkisUjesClientUtils {

    public static DWSClientConfig getClientConfig(String url, String user, String token, Map<String, String> jobProps) {
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder()
                .addServerUrl(url)
                .connectionTimeout(LinkisJobExecutionConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .discoveryEnabled(false).discoveryFrequency(1, TimeUnit.MINUTES)
                .loadbalancerEnabled(true)
                .maxConnectionSize(LinkisJobExecutionConfiguration.MAX_HTTP_CONNECTION_COUNT.getValue())
                .retryEnabled(false).readTimeout(LinkisJobExecutionConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .setAuthenticationStrategy(new TokenAuthenticationStrategy())
                .setAuthTokenKey(user).setAuthTokenValue(token)))
                .setDWSVersion(LinkisJobExecutionConfiguration.LINKIS_API_VERSION.getValue(jobProps)).build();
        return clientConfig;
    }

    public static DWSClientConfig getClientConfig1_X(String url, String user, String token, Map<String, String> jobProps) {
        //TODO This place needs Linkis to create a class,DefaultRetryHandler.
        RetryHandler retryHandler = null;
//        RetryHandler retryHandler = new DefaultRetryHandler();
        retryHandler.addRetryException(LinkisRetryException.class);

        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder()
                .addServerUrl(url)
                .connectionTimeout(LinkisJobExecutionConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .discoveryEnabled(true).discoveryFrequency(10, TimeUnit.MINUTES)
                .loadbalancerEnabled(true)
                .maxConnectionSize(LinkisJobExecutionConfiguration.MAX_HTTP_CONNECTION_COUNT.getValue())
                .retryEnabled(true)
                .setRetryHandler(retryHandler)
                .readTimeout(LinkisJobExecutionConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .setAuthenticationStrategy(new TokenAuthenticationStrategy())
                .setAuthTokenKey(user).setAuthTokenValue(token)))
                .setDWSVersion(LinkisJobExecutionConfiguration.LINKIS_API_VERSION.getValue(jobProps)).build();
        return clientConfig;
    }

    public static UJESClient getUJESClient(String url, String user, String token, Map<String, String> jobProps) {
        if (LinkisJobExecutionConfiguration.isLinkis1_X(jobProps)) {
            return new UJESClientImpl(getClientConfig1_X(url, user, token, jobProps));
        } else {
            return new UJESClientImpl(getClientConfig(url, user, token, jobProps));
        }
    }
}