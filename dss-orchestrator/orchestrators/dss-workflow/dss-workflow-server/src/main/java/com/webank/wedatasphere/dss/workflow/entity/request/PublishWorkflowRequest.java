package com.webank.wedatasphere.dss.workflow.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

public class PublishWorkflowRequest {

    private Long workflowId;
    private String comment;
    private LabelRouteVO labels;

    private String dssLabel;
    private Integer orchestratorId;
    private Integer orchestratorVersionId;

    public Integer getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Integer orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public Integer getOrchestratorVersionId() {
        return orchestratorVersionId;
    }

    public void setOrchestratorVersionId(Integer orchestratorVersionId) {
        this.orchestratorVersionId = orchestratorVersionId;
    }



    public String getDssLabel() {
        return dssLabel;
    }

    public void setDssLabel(String dssLabel) {
        this.dssLabel = dssLabel;
    }



    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LabelRouteVO getLabels() {
        return labels;
    }

    public void setLabels(LabelRouteVO labels) {
        this.labels = labels;
    }
}
