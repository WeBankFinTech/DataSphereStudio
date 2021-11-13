package com.webank.wedatasphere.dss.workflow.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

public class PublishWorkflowRequest {

    private Long workflowId;
    private String comment;
    private LabelRouteVO labels;

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
