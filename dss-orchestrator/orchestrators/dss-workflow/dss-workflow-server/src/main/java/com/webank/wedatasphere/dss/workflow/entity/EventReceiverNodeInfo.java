package com.webank.wedatasphere.dss.workflow.entity;

import java.util.Date;
import java.util.Map;

public class EventReceiverNodeInfo {

    // 项目名称
    private String projectName;

    private Long projectId;

    // 工作流名称
    private String orchestratorName;

    // 工作流ID

    private Long orchestratorId;

    // 节点名称
    private String nodeName;

    // 节点类型
    private String nodeType;

    // 节点小类
    private String nodeTypeName;

    // 节点ID
    private String nodeId;

    // 节点描述
    private String nodeDesc;

    private Date updateTime;

    private Date createTime;

    private String msgType;

    private String msgReceiver;

    private String msgTopic;

    private String msgName;

    private String queryFrequency;

    private String maxReceiveHours;

    private String msgSaveKey;

    private Boolean onlyReceiveToday;

    private Boolean msgReceiveUseRunDate;


    private String nodeKey;

    private Map<String,String> nodeContent;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }


    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeTypeName() {
        return nodeTypeName;
    }

    public void setNodeTypeName(String nodeTypeName) {
        this.nodeTypeName = nodeTypeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public Map<String, String> getNodeContent() {
        return nodeContent;
    }

    public void setNodeContent(Map<String, String> nodeContent) {
        this.nodeContent = nodeContent;
    }
}
