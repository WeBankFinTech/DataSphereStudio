package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.io.Serializable;

public class CreateWorkspaceRequest implements Serializable {

    private String workspaceName;
    private String department;
    private String description;
    private String tags;
    private String productName;


    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "CreateWorkspaceRequest{" +
                "workspaceName='" + workspaceName + '\'' +
                ", department='" + department + '\'' +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
