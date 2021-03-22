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

package com.webank.wedatasphere.dss.appconn.schedulis.service;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.schedulis.sso.SchedulisSecurityService;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * created by cooperyang on 2020/3/26
 * Description:
 */
public class AzkabanUserService {



    private static Map<String, String> schedulisUserMap = new HashMap<>();

    private static String baseUrl;

    private static SchedulisSecurityService schedulisSecurityService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AzkabanUserService.class);

    private static AtomicBoolean isStarted = new AtomicBoolean(false);

    private static final String ADMIN_USER = CommonVars.apply("wds.dss.schedulis.admin.user", "superadmin").getValue();

    private static AzkabanUserService azkabanUserService;

    public static void setBaseUrl(String baseUrl1){
        baseUrl = baseUrl1;
        if (!isStarted.get()){
            init();
            isStarted.set(true);
        }
    }

    private static void init() {
        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            @SuppressWarnings("unchecked")
            public void run() {
                try {
                    schedulisSecurityService = SchedulisSecurityService.getInstance(baseUrl);
                    Cookie cookie = schedulisSecurityService.login(ADMIN_USER);
                    CookieStore cookieStore = new BasicCookieStore();
                    cookieStore.addCookie(cookie);
                    HttpClient httpClient =  HttpClients.custom().setDefaultCookieStore(cookieStore).build();
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("page", "1"));
                    params.add(new BasicNameValuePair("pageSize", "10000"));
                    String finalUrl = baseUrl + "/system?ajax=loadSystemUserSelectData" + "&" +
                            EntityUtils.toString(new UrlEncodedFormEntity(params));
                    HttpGet httpGet = new HttpGet(finalUrl);
                    HttpResponse httpResponse =  httpClient.execute(httpGet);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    Map<String, Object> map = new Gson().fromJson(json, Map.class);
                    if (map.get("systemUserList") instanceof List){
                        ((List<Object>) map.get("systemUserList")).forEach(e -> {
                            AzkabanUserEntity entity =  new Gson().fromJson(e.toString(), AzkabanUserEntity.class);
                            schedulisUserMap.put(entity.getUsername(),entity.getId());
                        });
                    }
                } catch (Exception e) {
                    LOGGER.error("get user from wtss failed", e);
                }
            }
        },0, 1, TimeUnit.HOURS);
    }





    public static String getUserIdByName(String username){
        return schedulisUserMap.get(username);
    }

}
