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

package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerSecurityService;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * created by cooperyang on 2021/1/26
 * Description:
 */
public class SchedulisScheduleInfoGetOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisScheduleInfoGetOperation.class);

    private AppDesc appDesc;

    private List<DSSLabel> dssLabels;

    private DolphinSchedulerSecurityService schedulisSecurityService;

    private String scheduleInfoUrl;

    private String projectUrl;

    private String baseUrl;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    public SchedulisScheduleInfoGetOperation(AppDesc appDesc) {
        this.appDesc = appDesc;
        init();
    }

    private void init() {
        //todo 先从
        AppInstance appInstance = this.appDesc.getAppInstances().get(0);
        scheduleInfoUrl = appInstance.getBaseUrl().endsWith("/") ? appInstance.getBaseUrl() + "manager" :
            appInstance.getBaseUrl() + "/manager";
        this.projectUrl = appInstance.getBaseUrl().endsWith("/") ? appInstance.getBaseUrl() : ":";
        this.baseUrl = appInstance.getBaseUrl();
        this.schedulisSecurityService = DolphinSchedulerSecurityService.getInstance(appInstance.getBaseUrl());
    }

    //    public String getSchedulerInfo(String username, String projectName, String flowName) {
    //        try {
    //            CookieStore cookieStore = new BasicCookieStore();
    //            Cookie cookie = this.schedulisSecurityService.login(username);
    //            cookieStore.addCookie(cookie);
    //            HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    //            List<NameValuePair> params = new ArrayList<>();
    //            params.add(new BasicNameValuePair("project", projectName));
    //            params.add(new BasicNameValuePair("flow", flowName));
    //            params.add(new BasicNameValuePair("ajax", "fetchFlowExecutions"));
    //            params.add(new BasicNameValuePair("start", "0"));
    //            params.add(new BasicNameValuePair("length", "20"));
    //            params.add(new BasicNameValuePair("page", "0"));
    //            String finalUrl = this.scheduleInfoUrl + "?" + EntityUtils.toString(new UrlEncodedFormEntity(params));
    //            HttpGet httpGet = new HttpGet(finalUrl);
    //            HttpResponse httpResponse = httpClient.execute(httpGet);
    //            InputStream inputStream = httpResponse.getEntity().getContent();
    //            String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    //            ObjectMapper objectMapper = new ObjectMapper();
    //            JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
    //            JsonNode executions = jsonNode.get("executions");
    //            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //            String retStr = "";
    //            if (executions.size() > 0) {
    //                JsonNode execution = executions.get(0);
    //                long submitTime = execution.get("submitTime").getLongValue();
    //                String submitTimeStr = simpleDateFormat.format(new Date(submitTime));
    //                String status = execution.get("status").getTextValue();
    //                retStr = submitTimeStr + " " + status;
    //            }
    //            return retStr;
    //        } catch (final Throwable t) {
    //            LOGGER.error("failed to get status for projectName : {}  flowName : {}", projectName, flowName, t);
    //            return "";
    //        }
    //    }
    //
    //    public void setProjectAndFlowPriv(String projectOwner, String projectName, List<String> accessUsers)
    //        throws Exception {
    //        String url = this.baseUrl + "?";
    //        AzkabanUserService.setBaseUrl(this.baseUrl);
    //        CookieStore cookieStore = new BasicCookieStore();
    //        CloseableHttpClient httpClient = null;
    //        CloseableHttpResponse response = null;
    //        Cookie cookie = this.schedulisSecurityService.login(projectOwner);
    //        cookieStore.addCookie(cookie);
    //        try {
    //            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    //            HttpClientContext context = HttpClientContext.create();
    //            for (String accessUser : accessUsers) {
    //                String userId = AzkabanUserService.getUserIdByName(accessUser);
    //                List<NameValuePair> params = new ArrayList<>();
    //                params.add(new BasicNameValuePair("project", projectName));
    //                params.add(new BasicNameValuePair("userId", userId));
    //                params.add(new BasicNameValuePair("ajax", "ajaxAddProjectAdmin"));
    //                params.add(new BasicNameValuePair("permissions[admin]", "true"));
    //                params.add(new BasicNameValuePair("permissions[read]", "true"));
    //                params.add(new BasicNameValuePair("permissions[write]", "true"));
    //                params.add(new BasicNameValuePair("permissions[execute]", "true"));
    //                params.add(new BasicNameValuePair("permissions[schedule]", "true"));
    //                String finalUrl = url + EntityUtils.toString(new UrlEncodedFormEntity(params));
    //                HttpGet httpGet = new HttpGet(finalUrl);
    //                response = httpClient.execute(httpGet, context);
    //                InputStream inputStream = response.getEntity().getContent();
    //                String responseContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    //                LOGGER.info("for project {} add a user {} response is {} content is {}", projectName, accessUser,
    //                    response.getStatusLine().getStatusCode(), responseContent);
    //            }
    //        } finally {
    //            IOUtils.closeQuietly(response);
    //            IOUtils.closeQuietly(httpClient);
    //        }
    //    }

}
