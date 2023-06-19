package com.webank.wedatasphere.dss.orchestrator.server.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

public class RollbackOrchestratorRequest {
    private Long orchestratorId;
    private String version;
    private Long projectId;
    private String projectName;

    private LabelRouteVO labels;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "RollbackOrchestratorRequest{" +
                "orchestratorId=" + orchestratorId +
                ", version='" + version + '\'' +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", labels=" + labels +
                '}';
    }
}
