package com.webank.wedatasphere.dss.workflow.dto;

import java.io.Serializable;

public class NodeTypeDO implements Serializable {
    private String nodeType;
    private String key;

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
