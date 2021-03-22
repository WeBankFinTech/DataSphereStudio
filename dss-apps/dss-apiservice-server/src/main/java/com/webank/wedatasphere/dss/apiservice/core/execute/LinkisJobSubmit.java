/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.apiservice.core.execute;

import com.webank.wedatasphere.dss.apiservice.core.config.ApiServiceConfiguration;
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import com.webank.wedatasphere.linkis.ujes.client.UJESClient;
import com.webank.wedatasphere.linkis.ujes.client.UJESClientImpl;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/08/12 03:33 PM
 */
public class LinkisJobSubmit {



    public  static UJESClient getClient(Map<String, String> props) {

        UJESClient   client = getUJESClient(
                ApiServiceConfiguration.LINKIS_URL.getValue(props),
                ApiServiceConfiguration.LINKIS_ADMIN_USER.getValue(props),
                ApiServiceConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(props),
                props);

        return client;
    }


    public static UJESClient getUJESClient(String url, String user, String token, Map<String, String> jobProps){
        return new UJESClientImpl(getClientConfig(url,user,token, jobProps));
    }

    public static DWSClientConfig getClientConfig(String url, String user, String token, Map<String, String> jobProps){
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder()
                .addServerUrl(url)
                .connectionTimeout(ApiServiceConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .discoveryEnabled(false).discoveryFrequency(1, TimeUnit.MINUTES)
                .loadbalancerEnabled(true)
                .maxConnectionSize(5)
                .retryEnabled(false).readTimeout(ApiServiceConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .setAuthenticationStrategy(new TokenAuthenticationStrategy())
                .setAuthTokenKey(user).setAuthTokenValue(token)))
                .setDWSVersion(ApiServiceConfiguration.LINKIS_API_VERSION.getValue(jobProps)).build();
        return clientConfig;
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
