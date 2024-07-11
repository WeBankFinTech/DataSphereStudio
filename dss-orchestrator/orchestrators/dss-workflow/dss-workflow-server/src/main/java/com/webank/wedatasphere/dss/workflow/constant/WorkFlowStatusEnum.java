package com.webank.wedatasphere.dss.workflow.constant;

import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;

public enum WorkFlowStatusEnum {
    SAVE(OrchestratorRefConstant.FLOW_STATUS_SAVE,"待提交"),
    PUSH(OrchestratorRefConstant.FLOW_STATUS_PUSH,"待发布"),
    PUBLISH(OrchestratorRefConstant.FLOW_STATUS_PUBLISH,"已发布"),
    RUNNING(OrchestratorRefConstant.FLOW_STATUS_PUSHING,"提交中"),
    PUBLISHING(OrchestratorRefConstant.FLOW_STATUS_PUBLISHING,"发布中"),
    STATELESS(OrchestratorRefConstant.FLOW_STATUS_STATELESS,"--");


    private String status;
    private String name;

    WorkFlowStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }


    public static WorkFlowStatusEnum getEnum(String status) {
        if (status == null) {
            return STATELESS;
        }
        for (WorkFlowStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return STATELESS;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
