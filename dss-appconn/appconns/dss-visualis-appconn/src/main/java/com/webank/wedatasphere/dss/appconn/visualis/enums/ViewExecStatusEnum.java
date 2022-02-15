package com.webank.wedatasphere.dss.appconn.visualis.enums;

import java.util.Arrays;
public enum ViewExecStatusEnum {
    Scheduled(0, "Scheduled"),
    Running(1, "Running"),
    Succeed(2, "Succeed"),
    Failed(3, "Failed"),
    Cancelled(4, "Cancelled"),
    Inited(5, "Inited"),
    Timeout(6, "Timeout");

    private int code;
    private String status;

    private ViewExecStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public static ViewExecStatusEnum getEnum(String status) {
        return Arrays.stream(ViewExecStatusEnum.values()).filter(e -> e.getStatus().equals(status)).findFirst().orElseThrow(NullPointerException::new);
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

}
