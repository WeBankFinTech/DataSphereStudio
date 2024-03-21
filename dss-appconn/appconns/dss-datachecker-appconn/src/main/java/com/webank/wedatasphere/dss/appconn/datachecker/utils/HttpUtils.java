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

package com.webank.wedatasphere.dss.appconn.datachecker.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.webank.wedatasphere.dss.appconn.datachecker.DataChecker;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

  private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

  public static Response httpClientHandleBase(String actionUrl, RequestBody requestBody, Map<String, String> urlMap) throws IOException {
    String maskUrl = actionUrl + "appid=" + urlMap.get("appid") + "&&nonce=" + urlMap.get("nonce")
            +  "&&timestamp=" + urlMap.get("timestamp") + "&&signature=" + urlMap.get("signature");
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    logger.info("access mask URL is:"+maskUrl);
    Request request = new Request.Builder()
            .url(maskUrl)
            .post(requestBody)
            .build();
    Call call = okHttpClient.newCall(request);
    Response response = call.execute();
    logger.info("mask interface response code：" + response.code());
    return response;
  }

  public static String httpClientHandle(String actionUrl, RequestBody requestBody, Map<String, String> urlMap) throws Exception{
    String returnData = "";
    try {
      Response response = httpClientHandleBase(actionUrl, requestBody, urlMap);
      returnData = response.body().string();
      logger.info("mask interface return message：" + returnData);
    } catch (IOException e) {
      logger.error("invoke failed",e);
    }
    return returnData;
  }

  public static String httpClientHandle(String actionUrl) throws Exception{
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build();
    Request request = new Request.Builder()
        .url(actionUrl)
        .build();
    Call call = okHttpClient.newCall(request);
    String returnData = "";
    try {
      Response response = call.execute();
      returnData = response.body().string();
      logger.info("interface return message：" + returnData);
    } catch (IOException e) {
      logger.error("invoke failed",e);
    }
    return returnData;
  }

  public static Map<String, String> getReturnMap(String dataStr){
    Map<String, String> dataMap = new HashMap<>();
    GsonBuilder gb = new GsonBuilder();
    Gson g = gb.create();
    dataMap = g.fromJson(dataStr, new TypeToken<Map<String, String>>(){}.getType());
    return dataMap;
  }

  public static Map<String, String> initSelectParams(Properties props){
    String appid = props.getProperty(DataChecker.MASK_APP_ID);
    String token = props.getProperty(DataChecker.MASK_APP_TOKEN);
    String nonce = RandomStringUtils.random(5, "0123456789");
    Long cur_time = System.currentTimeMillis() / 1000;
    Map<String, String> requestProperties = new HashMap<>();
    requestProperties.put("appid", appid);
    requestProperties.put("nonce", nonce.toString());
    requestProperties.put("signature", getMD5(getMD5(appid + nonce.toString() + cur_time) + token));
    requestProperties.put("timestamp", cur_time.toString());
    return requestProperties;
  }

  public static String getMD5(String str){
    return DigestUtils.md5Hex(str.getBytes());
  }

}
