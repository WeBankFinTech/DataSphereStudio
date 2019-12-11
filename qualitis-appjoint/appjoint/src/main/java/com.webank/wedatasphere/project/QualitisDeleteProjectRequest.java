package com.webank.wedatasphere.project;

import com.google.gson.annotations.SerializedName;

/**
 * @author howeye
 */
public class QualitisDeleteProjectRequest {

    @SerializedName("project_id")
    private Long projectId;
    private String username;

    public QualitisDeleteProjectRequest() {
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
