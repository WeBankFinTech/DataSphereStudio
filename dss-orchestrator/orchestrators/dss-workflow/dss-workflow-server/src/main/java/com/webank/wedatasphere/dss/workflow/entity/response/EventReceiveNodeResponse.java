package com.webank.wedatasphere.dss.workflow.entity.response;


import com.webank.wedatasphere.dss.workflow.entity.EventReceiverNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class EventReceiveNodeResponse {


    private Long total = 0L;

    private List<EventReceiverNodeInfo> eventReceiverNodeInfoList = new ArrayList<>();


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<EventReceiverNodeInfo> getEventReceiverNodeInfoList() {
        return eventReceiverNodeInfoList;
    }

    public void setEventReceiverNodeInfoList(List<EventReceiverNodeInfo> eventReceiverNodeInfoList) {
        this.eventReceiverNodeInfoList = eventReceiverNodeInfoList;
    }
}
