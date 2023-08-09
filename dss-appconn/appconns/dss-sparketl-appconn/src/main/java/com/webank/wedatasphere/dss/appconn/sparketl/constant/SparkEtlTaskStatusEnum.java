package com.webank.wedatasphere.dss.appconn.sparketl.constant;

public enum SparkEtlTaskStatusEnum {

    /**
     * Task status enum
     */
    SUBMITTED(1, "已提交", "SUBMITTED"),
    INITED(2, "初始化", "INITED"),
    RUNNING(3, "运行中", "RUNNING"),
    SUCCEED(4, "成功", "SUCCEED"),
    PASS_CHECKOUT(5, "通过校验", "PASS_CHECKOUT"),
    FAIL_CHECKOUT(6, "未通过校验", "FAIL_CHECKOUT"),
    FAILED(7, "失败", "FAILED"),
    TASK_NOT_EXIST(8, "Task不存在", "TASK_NOT_EXIST"),
    CANCELLED(9, "取消", "CANCELLED"),
    TIMEOUT(10, "超时", "TIMEOUT"),
    SCHEDULED(11, "调度中", "SCHEDULED"),
    SUBMIT_PENDING(12, "提交阻塞", "SUBMIT_PENDING");

    private Integer code;
    private String message;
    private String state;

    SparkEtlTaskStatusEnum(Integer code, String message, String state) {
        this.code = code;
        this.message = message;
        this.state = state;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getState() {
        return state;
    }

}
