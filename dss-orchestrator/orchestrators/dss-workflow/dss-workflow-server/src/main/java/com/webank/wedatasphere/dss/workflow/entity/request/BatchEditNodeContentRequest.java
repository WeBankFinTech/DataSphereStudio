package com.webank.wedatasphere.dss.workflow.entity.request;

import java.util.List;

public class BatchEditNodeContentRequest {


    private List<EditNodeContentRequest> editNodeContentRequestList;

    public List<EditNodeContentRequest> getEditNodeContentRequestList() {
        return editNodeContentRequestList;
    }

    public void setEditNodeContentRequestList(List<EditNodeContentRequest> editNodeContentRequestList) {
        this.editNodeContentRequestList = editNodeContentRequestList;
    }
}
