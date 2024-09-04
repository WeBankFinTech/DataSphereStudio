package com.webank.wedatasphere.dss.workflow.entity.request;

import java.util.List;

public class BatchEditFlowRequest {
    private List<EditFlowRequest> editFlowRequestList;


    public List<EditFlowRequest> getEditFlowRequestList() {
        return editFlowRequestList;
    }

    public void setEditFlowRequestList(List<EditFlowRequest> editFlowRequestList) {
        this.editFlowRequestList = editFlowRequestList;
    }
}
