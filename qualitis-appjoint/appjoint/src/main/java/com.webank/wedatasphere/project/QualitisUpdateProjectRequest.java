package com.webank.wedatasphere.project;

import com.google.gson.annotations.SerializedName;

/**
 * @author howeye
 */
public class QualitisUpdateProjectRequest {

    @SerializedName("project_id")
    private Long projectId;
    @SerializedName("project_name")
    private String projectName;
    private String description;
    private String username;

    public QualitisUpdateProjectRequest() {
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
