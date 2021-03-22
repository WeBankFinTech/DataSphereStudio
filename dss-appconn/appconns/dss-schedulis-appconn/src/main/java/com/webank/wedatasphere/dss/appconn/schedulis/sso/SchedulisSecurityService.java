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

package com.webank.wedatasphere.dss.appconn.schedulis.sso;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.webank.wedatasphere.dss.appconn.schedulis.conf.AzkabanConf;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by v_wbjftang on 2019/9/16.
 */
public final class SchedulisSecurityService {

    private static Logger LOGGER = LoggerFactory.getLogger(SchedulisSecurityService.class);


    private String securityUrl = "";
    private static final String USER_NAME_KEY = "username";
    private static final String USER_TOKEN_KEY = AzkabanConf.AZKABAN_LOGIN_PWD.getValue();
    private static final String SESSION_ID_KEY = "azkaban.browser.session.id";
    private static Properties userToken;
    private static final String USER_STR = "username";

    private static final String USER_RSA = AzkabanConf.AZKABAN_RSA.getValue();
    private static final String USER_SECRET = "dss_secret";

    private static final String CIPHER_STR = "userpwd";


    private static final String SUPER_USER = "dws-wtss";
    private static final String SUPER_USER_CIPHER = "WeBankBDPWTSS&DWS@2019";

    private static final String SUPER_USER_STR = "superUser";
    private static final String SUPER_USER_CIPHER_STR = "superUserPwd";


    private Cache<String, Cookie> cookieCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30 * 60, TimeUnit.SECONDS)
            .build();

    private static SchedulisSecurityService instance;

    private SchedulisSecurityService(String baseUrl) {
        this.securityUrl = baseUrl.endsWith("/") ? baseUrl + "checkin" : baseUrl + "/checkin";
    }

    public static SchedulisSecurityService getInstance(String baseUrl) {
        if (null == instance) {
            synchronized (SchedulisSecurityService.class) {
                if (null == instance) {
                    instance = new SchedulisSecurityService(baseUrl);
                }
            }
        }
        return instance;
    }

    static {
        Utils.defaultScheduler().scheduleAtFixedRate(()->{
            LOGGER.info("开始读取用户token文件");
            Properties properties = new Properties();
            try {
                properties.load(SchedulisSecurityService.class.getClassLoader().getResourceAsStream("token.properties"));
                userToken = properties;
            } catch (IOException e) {
                LOGGER.error("读取文件失败:",e);
            }
        },0,10, TimeUnit.MINUTES);
    }


    public Cookie login(String user) throws Exception {
        synchronized (user.intern()) {
            Cookie session = cookieCache.getIfPresent(user);
            if (session != null) {
                return session;
            }
            Cookie newCookie = getCookie(user, getUserToken(user));
            cookieCache.put(user, newCookie);
            return newCookie;
        }
    }

    private String getUserToken(String user) {
        //直接从配置文件中读取，有需求可以自己实现
        Object token = userToken.get(user);
        if (token == null) {
            return "";
        }
        return token.toString();
    }


    private Cookie getCookie(String user, String token) throws Exception {
        HttpPost httpPost = new HttpPost(securityUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(USER_NAME_KEY, user));
        params.add(new BasicNameValuePair(USER_TOKEN_KEY, token));
        params.add(new BasicNameValuePair(USER_STR, user));
        params.add(new BasicNameValuePair(CIPHER_STR, token));
//        params.add(new BasicNameValuePair(SUPER_USER_STR, SUPER_USER));
//        params.add(new BasicNameValuePair(SUPER_USER_CIPHER_STR, SUPER_USER_CIPHER));
//        params.add(new BasicNameValuePair(USER_SECRET, USER_RSA));
        params.add(new BasicNameValuePair("action", "login"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpClientContext context;
        String responseContent;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            context = HttpClientContext.create();
            response = httpClient.execute(httpPost, context);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "utf-8");
            LOGGER.info("Get azkaban response code is " + response.getStatusLine().getStatusCode() + ",response: " + responseContent);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new ErrorException(90041, responseContent);
            }

        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        List<Cookie> cookies = context.getCookieStore().getCookies();
        Optional<Cookie> cookie = cookies.stream().filter(this::findSessionId).findAny();
        return cookie.orElseThrow(() -> new ErrorException(90041, "Get azkaban session is null : " + responseContent));
    }

    private boolean findSessionId(Cookie cookie) {
        return SESSION_ID_KEY.equals(cookie.getName());
    }


}
