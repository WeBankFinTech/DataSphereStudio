package com.webank.wedatasphere.dss.appconn.eventchecker.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: xlinliu
 * Date: 2024/8/2
 */
public class HttpMsgReceiveRequest {
    private String receiver;
    private String topic;
    private String msgName;
    private String runDate;
    private boolean onlyReceiveToday;
    private boolean receiveUseRunDate;


    public HttpMsgReceiveRequest(String receiver, String topic, String msgName, String runDate, boolean onlyReceiveToday, boolean receiveUseRunDate) {
        this.receiver = receiver;
        this.topic = topic;
        this.msgName = msgName;
        this.runDate = runDate;
        this.onlyReceiveToday = onlyReceiveToday;
        this.receiveUseRunDate = receiveUseRunDate;
    }

    public HttpMsgReceiveRequest() {

    }

    public HttpMsgReceiveRequest setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public HttpMsgReceiveRequest setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public HttpMsgReceiveRequest setMsgName(String msgName) {
        this.msgName = msgName;
        return this;
    }

    public HttpMsgReceiveRequest setRunDate(String runDate) {
        this.runDate = runDate;
        return this;
    }

    public HttpMsgReceiveRequest setOnlyReceiveToday(boolean onlyReceiveToday) {
        this.onlyReceiveToday = onlyReceiveToday;
        return this;
    }

    public HttpMsgReceiveRequest setReceiveUseRunDate(boolean receiveUseRunDate) {
        this.receiveUseRunDate = receiveUseRunDate;
        return this;
    }


    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
