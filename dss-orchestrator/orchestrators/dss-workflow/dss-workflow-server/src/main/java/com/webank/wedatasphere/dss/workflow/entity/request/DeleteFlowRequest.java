package com.webank.wedatasphere.dss.workflow.entity.request;

public class DeleteFlowRequest {

    private Long id;
    private Boolean sure;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSure() {
        return sure;
    }

    public void setSure(Boolean sure) {
        this.sure = sure;
    }
}
