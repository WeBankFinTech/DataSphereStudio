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

package com.webank.wedatasphere.dss.scriptis.execute;

import com.webank.wedatasphere.dss.scriptis.config.DSSScriptisConfiguration;
import org.apache.linkis.common.conf.Configuration;
import org.apache.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.apache.linkis.server.socket.controller.ServerEvent;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.UJESClientImpl;
import org.apache.linkis.ujes.client.request.JobExecuteAction;
import org.apache.linkis.ujes.client.response.JobExecuteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LinkisJobSubmit {

    private static volatile UJESClient ujesClient;

    public static UJESClient getClient() {
        if(ujesClient == null) {
            synchronized (LinkisJobSubmit.class) {
                if(ujesClient == null) {
                    ujesClient = getUJESClient(
                            Configuration.GATEWAY_URL().getValue(),
                            DSSScriptisConfiguration.LINKIS_ADMIN_USER.getValue(),
                            DSSScriptisConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue());
                }
            }
        }
        return ujesClient;
    }

    public static UJESClient getUJESClient(String url, String user, String token){
        return new UJESClientImpl(getClientConfig(url, user, token));
    }

    public static DWSClientConfig getClientConfig(String url, String user, String token){
        return ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder()
                .addServerUrl(url)
                .connectionTimeout(DSSScriptisConfiguration.LINKIS_CONNECTION_TIMEOUT.getValue())
                .discoveryEnabled(false).discoveryFrequency(1, TimeUnit.MINUTES)
                .loadbalancerEnabled(true)
                .maxConnectionSize(5)
                .retryEnabled(false).readTimeout(DSSScriptisConfiguration.LINKIS_READ_TIMEOUT.getValue())
                .setAuthenticationStrategy(new TokenAuthenticationStrategy())
                .setAuthTokenKey(user).setAuthTokenValue(token)))
                .setDWSVersion(ServerConfiguration.BDP_SERVER_VERSION()).build();
    }

    /**
     * @param operation
     * @return
     */
    public static JobExecuteResult execute(ServerEvent operation) {
        Map<String,Object> dataMap = operation.getData();
        String runType = (String) dataMap.get("runType");
        String background = (String) dataMap.get("background");
        String executionCode = (String) dataMap.get("executionCode");
        String scriptPath = "default.scala";
        Map<String, String> sourceMap = (Map<String,String>) dataMap.get("source");
        scriptPath = sourceMap != null && sourceMap.containsKey("scriptPath") ? sourceMap.get("scriptPath") : scriptPath;

        String executeApplicationName = (String)dataMap.get("executeApplicationName");
        String umUser = (String)dataMap.get("umUser");
        Map<String, Object> paramsMap = (Map<String,Object>) dataMap.get("params");

        Map<String, Object> source = new HashMap<>(1);
        source.put("DSS-Scriptis", scriptPath);
        JobExecuteAction.Builder builder = JobExecuteAction.builder().setCreator("IDE")
                .addExecuteCode(executionCode)
                .setEngineTypeStr(executeApplicationName)
                .setRunTypeStr(runType)
                .setUser(umUser)
                .setParams(paramsMap)
                .setSource(source);
        JobExecuteAction jobAction = builder.build();
        return getClient().execute(jobAction);
    }

}
