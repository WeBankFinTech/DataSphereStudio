package com.webank.wedatasphere.dss.appconn.eventchecker.entity;

/**
 * Author: xlinliu
 * Date: 2024/8/6
 */
public class HttpMsgSendResponse {
    private int retCode;
    private String message;

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
}
