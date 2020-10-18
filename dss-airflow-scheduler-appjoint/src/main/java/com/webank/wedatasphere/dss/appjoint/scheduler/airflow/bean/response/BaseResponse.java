package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import lombok.Data;

import java.util.Date;

@Data
public class BaseResponse {

    private String resultCode;
    private String displayMsg;
    private Object data;
    private String requestId;
    private Date serverTime;

    public BaseResponse(ResultCode code) {
        this.resultCode = code.getResultCode();
    }

    public BaseResponse(ResultCode code, String message, Object data) {
        this.resultCode = code.getResultCode();
        this.displayMsg = message;
        this.data = data;
    }

    public BaseResponse(ResultCode code, String message) {
        this.resultCode = code.getResultCode();
        this.displayMsg = message;
    }
}
