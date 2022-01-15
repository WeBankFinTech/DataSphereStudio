package com.webank.wedatasphere.dss.workflow.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

public class AddFlowRequest {

    private String name;
    private String workspaceName;
    private String projectName;
    private String version;
    private String description ;
    private Long parentFlowID;
    private String uses;
    private LabelRouteVO labels;
    private String userName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getParentFlowID() {
        return parentFlowID;
    }

    public void setParentFlowID(Long parentFlowID) {
        this.parentFlowID = parentFlowID;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public LabelRouteVO getLabels() {
        return labels;
    }

    public void setLabels(LabelRouteVO labels) {
        this.labels = labels;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
