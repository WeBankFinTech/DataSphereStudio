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

package com.webank.wedatasphere.dss.apiservice.core.execute;

import com.webank.wedatasphere.dss.apiservice.core.config.ApiServiceConfiguration;
import org.apache.linkis.common.conf.Configuration;
import org.apache.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.UJESClientImpl;
import org.apache.linkis.ujes.client.request.JobExecuteAction;
import org.apache.linkis.ujes.client.response.JobExecuteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LinkisJobSubmit {

    private static final Map<String, UJESClient> ujesClientMap = new HashMap<>();

    public static UJESClient getClient() {
        return getClient(new HashMap<>(0));
    }

    public static UJESClient getClient(Map<String, String> params) {
        return getUJESClient(
                Configuration.GATEWAY_URL().getValue(params),
                ApiServiceConfiguration.LINKIS_ADMIN_USER.getValue(params),
                ApiServiceConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(params),
                params);
    }


    public static UJESClient getUJESClient(String url, String user, String token, Map<String, String> jobProps){
        String key = url + user + token;
        if(ujesClientMap.containsKey(key)) {
            return ujesClientMap.get(key);
        }
        synchronized (LinkisJobSubmit.class) {
            if(!ujesClientMap.containsKey(key)) {
                ujesClientMap.put(key, new UJESClientImpl(getClientConfig(url,user,token, jobProps)));
            }
        }
        return ujesClientMap.get(key);
    }

    public static DWSClientConfig getClientConfig(String url, String user, String token, Map<String, String> jobProps){
        return ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder()
                .addServerUrl(url)
                .connectionTimeout(ApiServiceConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .discoveryEnabled(false).discoveryFrequency(1, TimeUnit.MINUTES)
                .loadbalancerEnabled(true)
                .maxConnectionSize(5)
                .retryEnabled(false).readTimeout(ApiServiceConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .setAuthenticationStrategy(new TokenAuthenticationStrategy())
                .setAuthTokenKey(user).setAuthTokenValue(token)))
                .setDWSVersion(ApiServiceConfiguration.LINKIS_API_VERSION.getValue(jobProps)).build();
    }



    public static JobExecuteResult execute(ApiServiceExecuteJob job,UJESClient client) {
        Map<String, Object> source = new HashMap<>();
        source.put("DSS-ApiService",job.getScriptPath());
        JobExecuteAction.Builder builder = JobExecuteAction.builder().setCreator("IDE")
                .addExecuteCode(job.getCode())
                .setEngineTypeStr(job.getEngineType())
                .setRunTypeStr(job.getRunType())
                .setUser(job.getUser())
                .setParams(job.getParams())
                .setVariableMap(job.getRuntimeParams())
                .setSource(source);
        JobExecuteAction jobAction = builder.build();
        JobExecuteResult res = client.execute(jobAction);
        return res;
    }
    public static JobExecuteResult execute(ApiServiceExecuteJob job,UJESClient client, String creator) {
        Map<String, Object> source = new HashMap<>();
        source.put("DSS-ApiService",job.getScriptPath());
        JobExecuteAction.Builder builder = JobExecuteAction.builder().setCreator(creator)
                .addExecuteCode(job.getCode())
                .setEngineTypeStr(job.getEngineType())
                .setRunTypeStr(job.getRunType())
                .setUser(job.getUser())
                .setParams(job.getParams())
                .setVariableMap(job.getRuntimeParams())
                .setSource(source);
        JobExecuteAction jobAction = builder.build();
        JobExecuteResult res = client.execute(jobAction);
        return res;
    }
}
