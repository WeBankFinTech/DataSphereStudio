package com.webank.wedatasphere.dss.orchestrator.server.entity.request;


import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

import java.util.List;

public class OrchestratorBatchSubmitRequest {
    private List<OrchestratorSubmitRequest> submitRequestList;
    private LabelRouteVO labels;
    private String comment;

    public OrchestratorBatchSubmitRequest(List<OrchestratorSubmitRequest> submitRequestList) {
        this.submitRequestList = submitRequestList;
    }

    public OrchestratorBatchSubmitRequest() {
    }

    public OrchestratorBatchSubmitRequest(List<OrchestratorSubmitRequest> submitRequestList, LabelRouteVO labels, String comment) {
        this.submitRequestList = submitRequestList;
        this.labels = labels;
        this.comment = comment;
    }

    public List<OrchestratorSubmitRequest> getSubmitRequestList() {
        return submitRequestList;
    }

    public void setSubmitRequestList(List<OrchestratorSubmitRequest> submitRequestList) {
        this.submitRequestList = submitRequestList;
    }

    public LabelRouteVO getLabels() {
        return labels;
    }

    public void setLabels(LabelRouteVO labels) {
        this.labels = labels;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
