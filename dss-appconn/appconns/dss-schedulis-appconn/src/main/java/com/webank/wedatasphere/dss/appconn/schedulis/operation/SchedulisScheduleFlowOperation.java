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

package com.webank.wedatasphere.dss.appconn.schedulis.operation;

import com.webank.wedatasphere.dss.appconn.schedulis.sso.SchedulisSecurityService;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.AzkabanUtils;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * created by cooperyang on 2021/1/27
 * Description:
 */
public class SchedulisScheduleFlowOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisScheduleFlowOperation.class);


    private AppDesc appDesc;

    private String scheduleFlowUrl;

    private SchedulisSecurityService schedulisSecurityService;

    public SchedulisScheduleFlowOperation(AppDesc appDesc){
        this.appDesc = appDesc;
        init();
    }

    private void init(){
        //todo 现在默认是一个的,先拿进来
        AppInstance appInstance = appDesc.getAppInstances().get(0);
        this.scheduleFlowUrl = appInstance.getBaseUrl().endsWith("/") ? appInstance.getBaseUrl() + "schedule" :
                appInstance.getBaseUrl() + "/schedule";
        this.schedulisSecurityService = SchedulisSecurityService.getInstance(appInstance.getBaseUrl());
    }


    /**
     * 去schedulis进行调度工作流
     */
    public void scheduleFlow(String username, String projectName, String flowName,
                             String scheduleTime, String alarmEmails, String alarmLevel) throws Exception{
        LOGGER.info("{} begins to schedule project: {}, flow: {}, scheduleTime : {}, alarmEmails : {}, alarmLevel: {}",
                username, projectName, flowName, scheduleTime, alarmEmails, alarmLevel);
        try{
            String scheduleId = sendFlowScheduleParams(username, projectName, flowName, scheduleTime);
            if (scheduleId != null) {
                if (org.apache.commons.lang.StringUtils.isNotEmpty(alarmEmails) && org.apache.commons.lang.StringUtils.isNotEmpty(alarmLevel)) {
                    sendFlowScheduleAlarmParams(username, scheduleId, flowName, alarmEmails, alarmLevel);
                }
            }
        }catch(final Throwable t){
            //如果设置调度失败,是需要错误抛到前端的,不然会让用户以为是成功的
            LOGGER.error("Failed to set schedule info for projectName {} flowName {}", projectName, flowName);
            DSSExceptionUtils.dealErrorException(60053, "failed to schedule info", t, DSSErrorException.class);
        }
    }

    private String sendFlowScheduleParams(String username, String projectName, String flowName, String cronExpression) throws Exception {
        String scheduleId = null;
        List<NameValuePair> params = new ArrayList<>();
        if(cronExpression!= null) {
            params.add(new BasicNameValuePair("ajax", "scheduleCronFlow"));
            params.add(new BasicNameValuePair("projectName", projectName));
            params.add(new BasicNameValuePair("flow", flowName));
            params.add(new BasicNameValuePair("cronExpression", cronExpression));
            HttpPost httpPost = new HttpPost(this.scheduleFlowUrl);
            /*httpPost.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");*/
            CookieStore cookieStore = new BasicCookieStore();
            Cookie cookie = this.schedulisSecurityService.login(username);
            cookieStore.addCookie(cookie);
            HttpEntity entity = EntityBuilder.create().
                    setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
                    .setParameters(params).build();
            httpPost.setEntity(entity);
            CloseableHttpClient httpClient = null;
            CloseableHttpResponse response = null;
            try {
                httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
                response = httpClient.execute(httpPost);
                HttpEntity ent = response.getEntity();
                String entStr = IOUtils.toString(ent.getContent(), "utf-8");
                LOGGER.info("设置工程调度 {}, azkaban 返回的信息是 {}", projectName + "-" + flowName, entStr);
                String message = AzkabanUtils.handleAzkabanEntity(entStr);
                if (!"success".equals(message)) {
                    throw new DSSErrorException(90077, "设置调度失败, 原因:" + message);
                } else {
                    int  id = (int)Double.parseDouble(AzkabanUtils.getValueFromEntity(entStr, "scheduleId"));
                    scheduleId = Integer.toString(id);
                    LOGGER.info("设置调度成功，工作流名为：{}, 调度ID:{} ", flowName, scheduleId);
                }
            } catch (final Exception e) {
                DSSExceptionUtils.dealErrorException(61123, "failed to schedule flow", e, DSSErrorException.class);
            } finally {
                IOUtils.closeQuietly(response);
                IOUtils.closeQuietly(httpClient);
            }
        }else{
            LOGGER.info("设置工作流调度参数时间参数为空！");
        }
        return scheduleId;
    }


    private void sendFlowScheduleAlarmParams(String username, String scheduleId,
                                             String flowName, String slaEmails, String alarmLevel) throws Exception {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("ajax", "setSla"));
        params.add(new BasicNameValuePair("scheduleId", scheduleId));
        params.add(new BasicNameValuePair("slaEmails", slaEmails));
        String alarmParams = ",FAILURE EMAILS," + alarmLevel;
        params.add(new BasicNameValuePair("finishSettings[0]", alarmParams));
        HttpPost httpPost = new HttpPost(this.scheduleFlowUrl);
        Cookie cookie = this.schedulisSecurityService.login(username);
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);
        HttpEntity entity = EntityBuilder.create().
                setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
                .setParameters(params).build();
        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            response = httpClient.execute(httpPost);
            HttpEntity ent = response.getEntity();
            String entStr = IOUtils.toString(ent.getContent(), "utf-8");
            LOGGER.info("设置工作流告警{}, azkaban 返回的信息是 {}", scheduleId + "-" + flowName, entStr);
            String message = AzkabanUtils.handleAzkabanEntity(entStr);
            if (!"success".equals(message)) {
                throw new DSSErrorException(90078, "设置调度告警失败, 原因:" + message);
            } else {
                LOGGER.info("设置调度告警成功，工作流名为： " + flowName);
            }
        } catch (Exception e) {
            DSSExceptionUtils.dealErrorException(62213, "failed to set schedule info for " + flowName, e, DSSErrorException.class);
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }

    }







}
