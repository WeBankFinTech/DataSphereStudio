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

package com.webank.wedatasphere.dss.appconn.eventchecker.utils;


import cn.hutool.crypto.digest.DigestUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EventCheckerHttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(EventCheckerHttpUtils.class);

    /**
     * 发送get请求
     *
     * @param url    地址
     * @param params 参数
     * @return 请求结果
     */
    public static Response get(String url, Map<String, String> headerMap, Map<String, String> params) throws IOException {
        return request("GET", url, headerMap, params,null);
    }

    /**
     * 发送post请求
     *
     * @param url    地址
     * @param params 参数
     * @return 请求结果
     */
    public static Response post(String url, Map<String, String> headerMap, Map<String, String> params, String jsonBody) throws IOException {

        return request("POST",url, headerMap, params,jsonBody);
    }

    /**
     * 发送http请求
     *
     * @param method 请求方法
     * @param url    地址
     * @param headerMap 可以为null
     * @param params 参数map，可以为null
     * @param jsonBody json body，可以为null
     * @return 请求结果
     */
    public static Response request(String method, String url,  Map<String, String> headerMap,
                                     Map<String, String> params,   String jsonBody) throws IOException {

        if (method == null) {
            throw new RuntimeException("请求方法不能为空");
        }

        if (url == null) {
            throw new RuntimeException("url不能为空");
        }

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        Headers headers = setHeaderParams(headerMap);
        RequestBody body = jsonBody == null ? null : RequestBody.Companion.create(jsonBody,
                MediaType.Companion.parse("application/json"));
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .method(method, body)
                .headers(headers)
                .build();


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Response response = client.newCall(request).execute();
        logger.info("eventChecker http request successfully,url：{},retCode:{}", url, response.code());
        return response;
    }



    /**
     * 添加headers
     *
     * @param headerParams
     * @return
     */
    private static Headers setHeaderParams(Map<String, String> headerParams) {
        Headers.Builder headersbuilder = new Headers.Builder();
        if (headerParams != null) {
            headerParams.forEach(headersbuilder::add);
        }
        return headersbuilder.build();

    }

    // 鉴权方法
    public static String calculateSign( String key,String timestamp) {

        // 计算签名
        return DigestUtil.sha256Hex(key + timestamp);
    }
    public static String requestToString(String url, String method, Map<String, String> headerMap,
                                         Map<String, String> params,
                                         String jsonBody){
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        Headers headers = setHeaderParams(headerMap);
        RequestBody body = jsonBody == null ? null : RequestBody.Companion.create(jsonBody,
                MediaType.Companion.parse("application/json"));
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .method(method, body)
                .headers(headers)
                .build();


        StringBuilder sb = new StringBuilder();
        sb.append("Request: ").append(request.method()).append(" ").append(request.url()).append("\n");
        sb.append("Headers: ").append(request.headers()).append("\n");
        sb.append("Body: ").append(jsonBody);
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        Response response = get("http://10.107.116.246:8088/api/rest_j/v1/dss/scriptis/proxy/addUserProxy", null, null);
        System.out.println(response.body().string());
    }

}
