package com.webank.wedatasphere.dss.appconn.eventchecker.entity;

import java.util.Map;

/**
 * Author: xlinliu
 * Date: 2024/8/6
 */
public class HttpMsgReceiveResponse {
    private int retCode;
    private String message;
    private long msgId;
    private Map<String, Object> msgBody;
    /**
     * success成功
     * fail失败
     * running运行中
     */
    private String status;

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public Map<String, Object> getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(Map<String, Object> msgBody) {
        this.msgBody = msgBody;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
