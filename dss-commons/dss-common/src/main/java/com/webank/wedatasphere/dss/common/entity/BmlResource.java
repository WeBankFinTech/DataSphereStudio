package com.webank.wedatasphere.dss.common.entity;

public class BmlResource {

    private String resourceId;

    private String version;

    public BmlResource(String resourceId, String version) {
        this.resourceId = resourceId;
        this.version = version;
    }

    public BmlResource() {
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
