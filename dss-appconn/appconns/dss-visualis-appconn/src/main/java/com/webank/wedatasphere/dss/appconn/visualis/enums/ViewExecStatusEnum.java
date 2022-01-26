package com.webank.wedatasphere.dss.appconn.visualis.enums;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;

import java.util.Arrays;

/**
 * Description visualis执行view语句任务状态枚举类
 *
 * @Author elishazhang
 * @Date 2021/11/2
 */

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
