package com.webank.wedatasphere.dss.workflow.dto;


public class NodeContentUIDO {
    private Long contentId;
    private String nodeUIKey;
    private String nodeUIValue;
    private String nodeType;
    private String nodeContentType;

    public NodeContentUIDO() {
    }

    public NodeContentUIDO(Long contentId, String nodeUIKey, String nodeUIValue) {
        this.contentId = contentId;
        this.nodeUIKey = nodeUIKey;
        this.nodeUIValue = nodeUIValue;
    }

    public NodeContentUIDO(Long contentId, String nodeUIKey, String nodeUIValue, String nodeType, String nodeContentType) {
        this.contentId = contentId;
        this.nodeUIKey = nodeUIKey;
        this.nodeUIValue = nodeUIValue;
        this.nodeType = nodeType;
        this.nodeContentType = nodeContentType;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getNodeUIKey() {
        return nodeUIKey;
    }

    public void setNodeUIKey(String nodeUIKey) {
        this.nodeUIKey = nodeUIKey;
    }

    public String getNodeUIValue() {
        return nodeUIValue;
    }

    public void setNodeUIValue(String nodeUIValue) {
        this.nodeUIValue = nodeUIValue;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeContentType() {
        return nodeContentType;
    }

    public void setNodeContentType(String nodeContentType) {
        this.nodeContentType = nodeContentType;
    }
}
