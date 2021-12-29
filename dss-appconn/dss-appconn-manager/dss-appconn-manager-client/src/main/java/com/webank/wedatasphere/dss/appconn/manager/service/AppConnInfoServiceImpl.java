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

package com.webank.wedatasphere.dss.appconn.manager.service;

import com.webank.wedatasphere.dss.appconn.manager.action.GetAppConnInfoAction;
import com.webank.wedatasphere.dss.appconn.manager.action.GetAppConnInfosAction;
import com.webank.wedatasphere.dss.appconn.manager.action.GetAppInstancesAction;
import com.webank.wedatasphere.dss.appconn.manager.conf.AppConnManagerClientConfiguration;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppInstanceInfo;
import com.webank.wedatasphere.dss.appconn.manager.result.GetAppConnInfoResult;
import com.webank.wedatasphere.dss.appconn.manager.result.GetAppConnInfosResult;
import com.webank.wedatasphere.dss.appconn.manager.result.GetAppInstancesResult;
import org.apache.linkis.common.conf.Configuration;
import org.apache.linkis.httpclient.config.ClientConfig;
import org.apache.linkis.httpclient.dws.DWSHttpClient;
import org.apache.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import java.util.List;

import static com.webank.wedatasphere.dss.appconn.manager.conf.AppConnManagerClientConfiguration.LINKIS_ADMIN_USER;

public class AppConnInfoServiceImpl implements AppConnInfoService {

    private ClientConfig clientConfig = DWSClientConfigBuilder.newBuilder().setDWSVersion("v1").addServerUrl(Configuration.getGateWayURL())
        .connectionTimeout(300000).discoveryEnabled(false).setAuthenticationStrategy(new TokenAuthenticationStrategy()).setAuthTokenKey(LINKIS_ADMIN_USER.getValue())
        .setAuthTokenValue(AppConnManagerClientConfiguration.DSS_APPCONN_CLIENT_TOKEN.getValue()).maxConnectionSize(50).readTimeout(300000).build();
    private DWSHttpClient client = new DWSHttpClient((DWSClientConfig) clientConfig, "AppConn-Client-");

    @Override
    public List<? extends AppConnInfo> getAppConnInfos() {
        GetAppConnInfosAction getAppConnInfosAction =new GetAppConnInfosAction();
        getAppConnInfosAction.setUser(LINKIS_ADMIN_USER.getValue());
        GetAppConnInfosResult result = (GetAppConnInfosResult) client.execute(getAppConnInfosAction);
        return result.getAppConnInfos();
    }

    @Override
    public AppConnInfo getAppConnInfo(String appConnName) {
        GetAppConnInfoAction action = new GetAppConnInfoAction();
        action.setAppConnName(appConnName);
        action.setUser(LINKIS_ADMIN_USER.getValue());
        GetAppConnInfoResult result = (GetAppConnInfoResult) client.execute(action);
        return result.getAppConnInfo();
    }

    @Override
    public List<? extends AppInstanceInfo> getAppInstancesByAppConnInfo(AppConnInfo appConnInfo) {
        return getAppInstancesByAppConnName(appConnInfo.getAppConnName());
    }

    @Override
    public List<? extends AppInstanceInfo> getAppInstancesByAppConnName(String appConnName) {
        GetAppInstancesAction action = new GetAppInstancesAction();
        action.setAppConnName(appConnName);
        action.setUser(LINKIS_ADMIN_USER.getValue());
        GetAppInstancesResult result = (GetAppInstancesResult) client.execute(action);
        return result.getAppInstanceInfos();
    }
}
