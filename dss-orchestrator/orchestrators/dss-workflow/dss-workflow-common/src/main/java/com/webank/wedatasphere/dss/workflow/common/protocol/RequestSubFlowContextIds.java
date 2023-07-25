package com.webank.wedatasphere.dss.workflow.common.protocol;

import java.util.List;

/**
 * Description
 */

public class RequestSubFlowContextIds {
    private List<Long> flowIdList;

    public RequestSubFlowContextIds(List<Long> flowIdList) {
        this.flowIdList = flowIdList;
    }

    public List<Long> getFlowIdList() {
        return flowIdList;
    }

    public void setFlowIdList(List<Long> flowIdList) {
        this.flowIdList = flowIdList;
    }
}
