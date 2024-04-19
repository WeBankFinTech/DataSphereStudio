package com.webank.wedatasphere.dss.orchestrator.server.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

public class OrchestratorSubmitRequest {
    private Long flowId;
    private String dssLabel;
    private LabelRouteVO labels;
    private String projectName;
    private String comment;
    private Long orchestratorId;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getDssLabel() {
        return dssLabel;
    }

    public void setDssLabel(String dssLabel) {
        this.dssLabel = dssLabel;
    }

    public LabelRouteVO getLabels() {
        return labels;
    }

    public void setLabels(LabelRouteVO labels) {
        this.labels = labels;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }
}
