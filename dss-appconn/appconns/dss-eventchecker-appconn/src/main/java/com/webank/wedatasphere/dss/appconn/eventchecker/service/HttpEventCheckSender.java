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

package com.webank.wedatasphere.dss.appconn.eventchecker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.webank.wedatasphere.dss.appconn.eventchecker.entity.HttpMsgSendRequest;
import com.webank.wedatasphere.dss.appconn.eventchecker.entity.HttpMsgSendResponse;
import com.webank.wedatasphere.dss.appconn.eventchecker.utils.EventCheckerHttpUtils;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HttpEventCheckSender extends AbstractEventCheck {
    private static final String HTTP_EVENT_SIGN_KEY = "msg.eventchecker.http.sign.key";
    private static final String HTTP_EVENT_KGAS_SEND_URL = "msg.eventchecker.http.kgas.send.url";

    public HttpEventCheckSender(Properties props) {
        initECParams(props);
    }

    @Override
    public boolean sendMsg(int jobId, Properties props, Logger log) {
        boolean result = false;
        String url = props.getProperty(HTTP_EVENT_KGAS_SEND_URL);
        String key = props.getProperty(HTTP_EVENT_SIGN_KEY);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = EventCheckerHttpUtils.calculateSign(key, timestamp);
        Map<String, String> header = new HashMap<>();
        header.put("sign", sign);
        header.put("timestamp", timestamp);
        Gson gson = new GsonBuilder().create();
        Map<String, Object> msgBody = new HashMap<>();
        long currentTimeMillis = System.currentTimeMillis();
        int secondsSinceEpoch = (int) (currentTimeMillis / 1000);
        String msgId = String.valueOf(secondsSinceEpoch);
        if (StringUtils.isNoneBlank(msg)) {
            try {
                msgBody = gson.fromJson(msg, Map.class);
            }catch (JsonSyntaxException jsonSyntaxException){
                throw new RuntimeException("msg.body格式有误，请输入标准的json格式", jsonSyntaxException);
            }
        }
        HttpMsgSendRequest message = new HttpMsgSendRequest(sender, topic, msgName, runDate, msgId, msgBody);
        ResponseBody responseBody = null;
        String messageJson = gson.toJson(message);
        try (Response response = EventCheckerHttpUtils.post(url, header, null, messageJson)) {
            responseBody = response.body();
            HttpMsgSendResponse msgSendResponse = gson.fromJson(responseBody.charStream(),
                    HttpMsgSendResponse.class);
            int reCode = msgSendResponse.getRetCode();
            if (reCode == 0) {
                result = true;
                log.info("send successfully.jobId:{}",jobId);
            } else {
                String requestStr = EventCheckerHttpUtils.requestToString(url, "POST", header, null, messageJson);
                log.info(requestStr);
                String errorMsg = response.body().string();
                log.error("send failed,response:{}", errorMsg);
                throw new RuntimeException(errorMsg);
            }

        } catch (IOException e) {
            String errorMsg = "";
            try {
                errorMsg = responseBody != null ? responseBody.string() : "";
            }catch (IOException ioException){
                e = ioException;
            }
            throw new RuntimeException(errorMsg,e);
        }

        return result;
    }
}
