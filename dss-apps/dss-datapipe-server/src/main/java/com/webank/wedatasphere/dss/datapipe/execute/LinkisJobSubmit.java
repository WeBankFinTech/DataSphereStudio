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

package com.webank.wedatasphere.dss.datapipe.execute;

import com.webank.wedatasphere.dss.datapipe.config.DataPipeServiceConfiguration;
import com.webank.wedatasphere.linkis.common.conf.Configuration;
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import com.webank.wedatasphere.linkis.server.socket.controller.ServerEvent;
import com.webank.wedatasphere.linkis.ujes.client.UJESClient;
import com.webank.wedatasphere.linkis.ujes.client.UJESClientImpl;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LinkisJobSubmit {

    public static UJESClient getClient(Map<String, String> props) {
        UJESClient client = getUJESClient(
                Configuration.GATEWAY_URL().getValue(props),
                DataPipeServiceConfiguration.LINKIS_ADMIN_USER.getValue(props),
                DataPipeServiceConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(props),
                props);

        return client;
    }

    public static UJESClient getUJESClient(String url, String user, String token, Map<String, String> jobProps){
        return new UJESClientImpl(getClientConfig(url,user,token, jobProps));
    }

    public static DWSClientConfig getClientConfig(String url, String user, String token, Map<String, String> jobProps){
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder()
                .addServerUrl(url)
                .connectionTimeout(DataPipeServiceConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .discoveryEnabled(false).discoveryFrequency(1, TimeUnit.MINUTES)
                .loadbalancerEnabled(true)
                .maxConnectionSize(5)
                .retryEnabled(false).readTimeout(DataPipeServiceConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue(jobProps))
                .setAuthenticationStrategy(new TokenAuthenticationStrategy())
                .setAuthTokenKey(user).setAuthTokenValue(token)))
                .setDWSVersion(DataPipeServiceConfiguration.LINKIS_API_VERSION.getValue(jobProps)).build();
        return clientConfig;
    }

    /**
     * @param operation
     * @param client
     * @return
     */
    public static JobExecuteResult execute(ServerEvent operation, UJESClient client) {
        Map<String,Object> dataMap = operation.getData();
        String runType = (String)dataMap.get("runType");
        String background = (String)dataMap.get("background");
        String executionCode = (String)dataMap.get("executionCode");
        String scriptPath = "default.scala";
        Map<String,String> sourceMap = (Map<String,String>)dataMap.get("source");
        scriptPath = sourceMap!=null && sourceMap.containsKey("scriptPath") ? sourceMap.get("scriptPath") : scriptPath;

        String executeApplicationName = (String)dataMap.get("executeApplicationName");
        String umUser = (String)dataMap.get("umUser");
        Map<String,Object> paramsMap = (Map<String,Object>)dataMap.get("params");

        Map<String, Object> source = new HashMap<>();
        source.put("DSS-DataPipe",scriptPath);
        JobExecuteAction.Builder builder = JobExecuteAction.builder().setCreator("IDE")
                .addExecuteCode(executionCode)
                .setEngineTypeStr(executeApplicationName)
                .setRunTypeStr(runType)
                .setUser(umUser)
                .setParams(paramsMap)
                .setVariableMap(new HashMap<>())
                .setSource(source);
        JobExecuteAction jobAction = builder.build();
        JobExecuteResult res = client.execute(jobAction);
        return res;
    }

}
