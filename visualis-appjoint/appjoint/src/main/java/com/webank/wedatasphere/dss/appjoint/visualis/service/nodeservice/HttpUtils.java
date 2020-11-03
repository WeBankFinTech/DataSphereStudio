/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.visualis.service.nodeservice;

import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.dss.appjoint.visualis.service.VisualisSession;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by shanhuang on 2019/10/12.
 */
public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    public static String sendPostReq(Session session, String url, String params,
                                     String user) throws AppJointErrorException {
        String resultString = "{}";
        VisualisSession visualisSession = (VisualisSession)session;
        String token = visualisSession.getParameters().get("Token-Code");
        logger.info("sendPostReq url is: "+url+",session:"+token );
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
        httpPost.addHeader("Token-User",user);
        httpPost.addHeader("Token-Code",token);
        CookieStore cookieStore = new BasicCookieStore();
//        cookieStore.addCookie(session.getCookies()[0]);
        logger.info("Http request params is :"+params);
        StringEntity entity = null;
        try {
            entity = new StringEntity(params);
        } catch (UnsupportedEncodingException e) {
            throw new AppJointErrorException(42000, "create StringEntity failed!", e);
        }
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");//发送json数据需要设置contentType
        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = createSSLClientDefault(url, cookieStore);
            response = httpClient.execute(httpPost);
            HttpEntity ent = response.getEntity();
            resultString = IOUtils.toString(ent.getContent(), "utf-8");
            logger.info("Send Http Request Success", resultString);
        } catch (Exception e) {
            logger.error("Send Http Request Failed", e);
            throw new AppJointErrorException(42000, e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        return resultString;
    }

    public static String sendHttpDelete(Session session,String url,String user) throws AppJointErrorException {
        String resultString = "{}";
        HttpDelete httpdelete = new HttpDelete(url);
        //设置header
        VisualisSession visualisSession = (VisualisSession)session;
        String token = visualisSession.getParameters().get("Token-Code");
        logger.info("sendDeleteReq url is: "+url+",session:"+token );
        httpdelete.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
        httpdelete.addHeader("Token-User",user);
        httpdelete.addHeader("Token-Code",token);
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = createSSLClientDefault(url, cookieStore);
            response = httpClient.execute(httpdelete);
            HttpEntity ent = response.getEntity();
            resultString = IOUtils.toString(ent.getContent(), "utf-8");
            logger.info("Send Http Delete Request Success", resultString);
        } catch (Exception e) {
            logger.error("Send Http Delete Request Failed", e);
            throw new AppJointErrorException(42001, e.getMessage(), e);
        }finally{
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        return resultString;
    }

    public static String sendHttpPut(Session session, String url, String params,
                                     String user) throws AppJointErrorException {
        String resultString = "{}";
        VisualisSession visualisSession = (VisualisSession)session;
        String token = visualisSession.getParameters().get("Token-Code");
        logger.info("sendPostReq url is: "+url+",session:"+token );
        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
        httpPut.addHeader("Token-User",user);
        httpPut.addHeader("Token-Code",token);
        CookieStore cookieStore = new BasicCookieStore();
//        cookieStore.addCookie(session.getCookies()[0]);
        logger.info("Http put params is :"+params);
        StringEntity entity = null;
        try {
            entity = new StringEntity(params);
        } catch (UnsupportedEncodingException e) {
            throw new AppJointErrorException(42000, "create StringEntity failed!", e);
        }
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");//发送json数据需要设置contentType
        httpPut.setEntity(entity);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = createSSLClientDefault(url, cookieStore);
            response = httpClient.execute(httpPut);
            HttpEntity ent = response.getEntity();
            resultString = IOUtils.toString(ent.getContent(), "utf-8");
            logger.info("Send Http Put Success", resultString);
        } catch (Exception e) {
            logger.error("Send Http Put Failed", e);
            throw new AppJointErrorException(42000, e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        return resultString;
    }

    private static CloseableHttpClient getHttpClient(String url, CookieStore cookieStore){
        TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultCookieStore(cookieStore);
            if(url.startsWith("https://")){
                httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
            }
            CloseableHttpClient httpClient = httpClientBuilder.build();
            return  httpClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    public static CloseableHttpClient createSSLClientDefault(String url, CookieStore cookieStore) {
        try {
            //使用 loadTrustMaterial() 方法实现一个信任策略，信任所有证书
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            //NoopHostnameVerifier类:  作为主机名验证工具，实质上关闭了主机名验证，它接受任何
            //有效的SSL会话并匹配到目标主机。
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultCookieStore(cookieStore);
            if(url.startsWith("https://")){
                httpClientBuilder.setSSLSocketFactory(sslsf);
            }
            CloseableHttpClient httpClient = httpClientBuilder.build();
            return httpClient;
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();

    }

}
