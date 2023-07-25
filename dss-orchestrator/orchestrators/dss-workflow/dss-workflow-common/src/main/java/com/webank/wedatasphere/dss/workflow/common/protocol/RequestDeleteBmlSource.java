package com.webank.wedatasphere.dss.workflow.common.protocol;

import java.util.List;

public class RequestDeleteBmlSource {
    private List<Long> flowIdList;

    public RequestDeleteBmlSource(List<Long> flowIdList) {
        this.flowIdList = flowIdList;
    }

    public List<Long> getFlowIdList() {
        return flowIdList;
    }

    public void setFlowIdList(List<Long> flowIdList) {
        this.flowIdList = flowIdList;
    }
}
