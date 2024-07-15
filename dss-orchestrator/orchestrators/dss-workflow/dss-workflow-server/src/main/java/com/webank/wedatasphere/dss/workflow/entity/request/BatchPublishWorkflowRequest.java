package com.webank.wedatasphere.dss.workflow.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

import java.util.List;

public class BatchPublishWorkflowRequest {
    private List<Long> workflowIdList;
    private String comment;
    private LabelRouteVO labels;

    private String dssLabel;

    public BatchPublishWorkflowRequest(List<Long> workflowIdList, String comment, LabelRouteVO labels, String dssLabel) {
        this.workflowIdList = workflowIdList;
        this.comment = comment;
        this.labels = labels;
        this.dssLabel = dssLabel;
    }

    public BatchPublishWorkflowRequest() {
    }

    public List<Long> getWorkflowIdList() {
        return workflowIdList;
    }

    public void setWorkflowIdList(List<Long> workflowIdList) {
        this.workflowIdList = workflowIdList;
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

    public String getDssLabel() {
        return dssLabel;
    }

    public void setDssLabel(String dssLabel) {
        this.dssLabel = dssLabel;
    }
}
