package com.webank.wedatasphere.dss.workflow.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

public class SaveFlowRequest {

    private Long id;
    private String json;
    private String workspaceName;
    private String projectName;
    private String flowEditLock;
    private Boolean isNotHaveLock;
    private LabelRouteVO labels;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LabelRouteVO getLabels() {
        return labels;
    }

    public void setLabels(LabelRouteVO labels) {
        this.labels = labels;
    }

    public String getFlowEditLock() {
        return flowEditLock;
    }

    public void setFlowEditLock(String flowEditLock) {
        this.flowEditLock = flowEditLock;
    }

    public Boolean getNotHaveLock() {
        return isNotHaveLock;
    }

    public void setNotHaveLock(Boolean notHaveLock) {
        isNotHaveLock = notHaveLock;
    }
}
