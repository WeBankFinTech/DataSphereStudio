package com.webank.wedatasphere.dss.workflow.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

import java.util.List;

public class BatchPublishWorkflowRequest {
    private List<Long> orchestratorList;
    private String comment;
    private LabelRouteVO labels;

    private String dssLabel;

    public BatchPublishWorkflowRequest(String comment, LabelRouteVO labels, String dssLabel) {
        this.comment = comment;
        this.labels = labels;
        this.dssLabel = dssLabel;
    }

    public BatchPublishWorkflowRequest() {
    }

    public BatchPublishWorkflowRequest(List<Long> orchestratorList, String comment, LabelRouteVO labels, String dssLabel) {
        this.orchestratorList = orchestratorList;
        this.comment = comment;
        this.labels = labels;
        this.dssLabel = dssLabel;
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

    public List<Long> getOrchestratorList() {
        return orchestratorList;
    }

    public void setOrchestratorList(List<Long> orchestratorList) {
        this.orchestratorList = orchestratorList;
    }
}
