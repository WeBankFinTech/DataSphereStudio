package com.webank.wedatasphere.dss.appconn.eventchecker.service;


import cn.hutool.crypto.digest.DigestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webank.wedatasphere.dss.appconn.eventchecker.entity.HttpMsgReceiveRequest;
import com.webank.wedatasphere.dss.appconn.eventchecker.entity.HttpMsgReceiveResponse;
import com.webank.wedatasphere.dss.appconn.eventchecker.entity.HttpMsgSendResponse;
import com.webank.wedatasphere.dss.appconn.eventchecker.utils.EventCheckerHttpUtils;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Author: xlinliu
 * Date: 2024/8/1
 */
public class HttpEventcheckerReceiver extends AbstractEventCheckReceiver{
    private static final String HTTP_EVENT_SIGN_KEY = "msg.eventchecker.http.sign.key";
    private static final String HTTP_EVENT_KGAS_RECEIVE_URL = "msg.eventchecker.http.kgas.receive.url";



    public HttpEventcheckerReceiver(Properties props) {
        super(props);
    }

    @Override
    public String[] getMsg(Properties props, Logger log, String... params) {
        String url=props.getProperty(HTTP_EVENT_KGAS_RECEIVE_URL);
        String key=props.getProperty(HTTP_EVENT_SIGN_KEY);
        String timestamp=String.valueOf( System.currentTimeMillis());
        String sign = EventCheckerHttpUtils.calculateSign(key,timestamp);
        Map<String, String> header = new HashMap<>();
        header.put("sign", sign);
        header.put("timestamp", timestamp);
        //params = new String[]{nowStartTime, todayEndTime, lastMsgId, useRunDateFlag.toString(), runDate}
        boolean useRunDate=Boolean.parseBoolean(params[3]);
        String runDate=params[4];
        Gson gson = new GsonBuilder().create();
        boolean receiveTodayFlag = (null != receiveToday && "true".equalsIgnoreCase(receiveToday.trim()));
        HttpMsgReceiveRequest message = new HttpMsgReceiveRequest(receiver, topic, msgName, runDate, receiveTodayFlag, useRunDate);
        String[] consumedMsgInfo = null;
        try (Response response = EventCheckerHttpUtils.post(url, header, null, gson.toJson(message))) {
            HttpMsgReceiveResponse msgReceiveResponse = gson.fromJson(response.body().charStream(),
                    HttpMsgReceiveResponse.class);
            int reCode = msgReceiveResponse.getRetCode();
            if (reCode == 0 ) {
                if("success".equals(msgReceiveResponse.getStatus())) {
                    String msgBodyJson = gson.toJson(msgReceiveResponse.getMsgBody());
                    String msgId = String.valueOf(msgReceiveResponse.getMsgId());
                    //{"msg_id", "msg_name", "receiver", "msg"};
                    consumedMsgInfo = new String[]{msgId, msgName, receiver, msgBodyJson};
                }else{
                    consumedMsgInfo = null;
                }
            } else if (reCode == 9998) {
                consumedMsgInfo = null;
            } else {
                String errorMsg = response.body().string();
                log.error("receive failed,response:{}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            return consumedMsgInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
