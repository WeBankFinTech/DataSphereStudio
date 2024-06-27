package com.webank.wedatasphere.dss.git.common.protocol.response;


public class GitCreateProjectResponse{
    private String commitId;
    private String projectName;

    public GitCreateProjectResponse(String commitId, String projectName) {
        this.commitId = commitId;
        this.projectName = projectName;
    }

    public GitCreateProjectResponse() {
    }


    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
