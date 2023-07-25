package com.webank.wedatasphere.dss.orchestrator.server.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

public class OpenOrchestratorRequest {

    private String workspaceName;
    private Long orchestratorId;

    private LabelRouteVO labels;

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public LabelRouteVO getLabels() {
        return labels;
    }

    public void setLabels(LabelRouteVO labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "OpenOrchestratorRequest{" +
                "workspaceName='" + workspaceName + '\'' +
                ", orchestratorId=" + orchestratorId +
                ", labels=" + labels +
                '}';
    }
}
