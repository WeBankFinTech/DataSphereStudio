package com.webank.wedatasphere.dss.common.entity;

public interface ExceptionAlterSender {
    /**
     * send alter to user when server exception
     */
    void sendAlter(Alter alter);
}
