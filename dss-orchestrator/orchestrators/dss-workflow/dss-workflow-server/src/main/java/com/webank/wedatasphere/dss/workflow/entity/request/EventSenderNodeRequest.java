package com.webank.wedatasphere.dss.workflow.entity.request;


import java.util.ArrayList;
import java.util.List;

public class EventSenderNodeRequest {

    private Integer pageNow;

    private Integer pageSize;

    // 工作流名称
    private String orchestratorName;

    // 项目名称
    private List<String> projectNameList;

    // 节点名称
    private List<String> nodeNameList;


    private String msgSender;

    private String msgBody;

    private String msgTopic;

    private String msgName;

    public Integer getPageNow() {
        return pageNow;
    }

    public void setPageNow(Integer pageNow) {
        this.pageNow = pageNow;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public List<String> getProjectNameList() {
        return projectNameList;
    }

    public void setProjectNameList(List<String> projectNameList) {
        this.projectNameList = projectNameList;
    }

    public List<String> getNodeNameList() {
        return nodeNameList;
    }

    public void setNodeNameList(List<String> nodeNameList) {
        this.nodeNameList = nodeNameList;
    }

    public String getMsgSender() {
        return msgSender;
    }

    public void setMsgSender(String msgSender) {
        this.msgSender = msgSender;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
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
}
