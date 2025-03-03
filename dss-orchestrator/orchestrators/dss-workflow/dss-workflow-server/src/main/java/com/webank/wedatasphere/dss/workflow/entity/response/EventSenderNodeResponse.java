package com.webank.wedatasphere.dss.workflow.entity.response;

import com.webank.wedatasphere.dss.workflow.entity.EventSenderNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class EventSenderNodeResponse {


    private Long total = 0L;

    private List<EventSenderNodeInfo> eventSenderNodeInfoList = new ArrayList<>();


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<EventSenderNodeInfo> getEventSenderNodeInfoList() {
        return eventSenderNodeInfoList;
    }

    public void setEventSenderNodeInfoList(List<EventSenderNodeInfo> eventSenderNodeInfoList) {
        this.eventSenderNodeInfoList = eventSenderNodeInfoList;
    }
}
