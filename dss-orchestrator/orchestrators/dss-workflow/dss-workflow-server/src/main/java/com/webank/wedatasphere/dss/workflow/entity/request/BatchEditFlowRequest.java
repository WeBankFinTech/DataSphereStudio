package com.webank.wedatasphere.dss.workflow.entity.request;

import java.util.List;

public class BatchEditFlowRequest {
    private List<EditFlowRequest> editNodeList;


    public List<EditFlowRequest> getEditNodeList() {
        return editNodeList;
    }

    public void setEditNodeList(List<EditFlowRequest> editNodeList) {
        this.editNodeList = editNodeList;
    }
}
