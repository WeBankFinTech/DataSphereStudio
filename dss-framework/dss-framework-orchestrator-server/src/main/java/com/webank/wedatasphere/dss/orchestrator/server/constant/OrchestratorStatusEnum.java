package com.webank.wedatasphere.dss.orchestrator.server.constant;

import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorStatusVo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OrchestratorStatusEnum {
    SAVE(OrchestratorRefConstant.FLOW_STATUS_SAVE, "待提交"),
    PUSH(OrchestratorRefConstant.FLOW_STATUS_PUSH, "待发布"),
    PUBLISH(OrchestratorRefConstant.FLOW_STATUS_PUBLISH, "已发布"),
    RUNNING(OrchestratorRefConstant.FLOW_STATUS_PUSHING, "提交中"),
    PUBLISHING(OrchestratorRefConstant.FLOW_STATUS_PUBLISHING, "发布中"),
    STATELESS(OrchestratorRefConstant.FLOW_STATUS_STATELESS, "--"),

    FAILED(OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED, "发布失败");


    private String status;
    private String name;

    OrchestratorStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }


    public static OrchestratorStatusEnum getEnum(String status) {
        if (status == null) {
            return STATELESS;
        }
        for (OrchestratorStatusEnum statusEnum : values()) {
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


    public static List<OrchestratorStatusVo> getOrchestratorGitStatus() {

        return Stream.of(
                new OrchestratorStatusVo(PUSH.getStatus(), PUSH.getName()),
                new OrchestratorStatusVo(SAVE.getStatus(), SAVE.getName()),
                new OrchestratorStatusVo(PUBLISHING.getStatus(), PUBLISHING.getName()),
                new OrchestratorStatusVo(RUNNING.getStatus(), RUNNING.getName()),
                new OrchestratorStatusVo(STATELESS.getStatus(), STATELESS.getName())
        ).collect(Collectors.toList());
    }

}
