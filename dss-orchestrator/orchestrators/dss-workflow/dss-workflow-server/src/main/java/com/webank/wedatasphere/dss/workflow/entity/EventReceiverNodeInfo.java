package com.webank.wedatasphere.dss.workflow.entity;

import java.util.Date;
import java.util.Map;

public class EventReceiverNodeInfo extends  NodeBaseInfo {


    // 节点描述
    private String nodeDesc;

    private String msgType;

    private String msgReceiver;

    private String msgTopic;

    private String msgName;

    private String queryFrequency;

    private String maxReceiveHours;

    private String msgSaveKey;

    private Boolean onlyReceiveToday;

    private Boolean msgReceiveUseRunDate;

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgReceiver() {
        return msgReceiver;
    }

    public void setMsgReceiver(String msgReceiver) {
        this.msgReceiver = msgReceiver;
    }

    public String getMsgTopic() {
        return msgTopic;
    }

    public void setMsgTopic(String msgTopic) {
        this.msgTopic = msgTopic;
    }

    public String getMsgName() {
        return msgName;
    }

    public void setMsgName(String msgName) {
        this.msgName = msgName;
    }

    public String getQueryFrequency() {
        return queryFrequency;
    }

    public void setQueryFrequency(String queryFrequency) {
        this.queryFrequency = queryFrequency;
    }

    public String getMaxReceiveHours() {
        return maxReceiveHours;
    }

    public void setMaxReceiveHours(String maxReceiveHours) {
        this.maxReceiveHours = maxReceiveHours;
    }

    public String getMsgSaveKey() {
        return msgSaveKey;
    }

    public void setMsgSaveKey(String msgSaveKey) {
        this.msgSaveKey = msgSaveKey;
    }

    public Boolean getOnlyReceiveToday() {
        return onlyReceiveToday;
    }

    public void setOnlyReceiveToday(Boolean onlyReceiveToday) {
        this.onlyReceiveToday = onlyReceiveToday;
    }

    public Boolean getMsgReceiveUseRunDate() {
        return msgReceiveUseRunDate;
    }

    public void setMsgReceiveUseRunDate(Boolean msgReceiveUseRunDate) {
        this.msgReceiveUseRunDate = msgReceiveUseRunDate;
    }
}
