package com.webank.wedatasphere.dss.git.dto;

public class GitProjectGitInfo {
    private Long id;
    private Long workspaceId;
    private String gitProjectId;
    private String projectName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getGitProjectId() {
        return gitProjectId;
    }

    public void setGitProjectId(String gitProjectId) {
        this.gitProjectId = gitProjectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
