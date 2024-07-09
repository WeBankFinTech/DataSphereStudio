package com.webank.wedatasphere.dss.workflow.dto;


public class NodeContentUIDO {
    private Long id;
    private Long contentId;
    private String key;
    private String value;

    public NodeContentUIDO() {
    }

    public NodeContentUIDO(Long id, Long contentId, String key, String value) {
        this.id = id;
        this.contentId = contentId;
        this.key = key;
        this.value = value;
    }

    public NodeContentUIDO(Long contentId, String key, String value) {
        this.contentId = contentId;
        this.key = key;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
