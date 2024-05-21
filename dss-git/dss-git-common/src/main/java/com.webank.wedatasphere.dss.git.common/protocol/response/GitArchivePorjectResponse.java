package com.webank.wedatasphere.dss.git.common.protocol.response;


public class GitArchivePorjectResponse{
    private String projectName;

    public GitArchivePorjectResponse(String projectName) {
        this.projectName = projectName;
    }

    public GitArchivePorjectResponse() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
