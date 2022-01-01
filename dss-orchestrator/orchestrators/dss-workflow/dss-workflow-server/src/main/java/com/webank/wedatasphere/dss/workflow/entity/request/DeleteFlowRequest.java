package com.webank.wedatasphere.dss.workflow.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

public class DeleteFlowRequest {

    private Long id;
    private Boolean sure;
    private LabelRouteVO labels;

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

    public LabelRouteVO getLabels() {
        return labels;
    }

    public void setLabels(LabelRouteVO labels) {
        this.labels = labels;
    }
}
