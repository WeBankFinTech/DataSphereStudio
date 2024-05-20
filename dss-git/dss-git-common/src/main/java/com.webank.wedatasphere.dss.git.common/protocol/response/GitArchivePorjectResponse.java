package com.webank.wedatasphere.dss.git.common.protocol.response;


public class GitArchivePorjectResponse extends GitBaseResponse {
    private String projectName;

    public GitArchivePorjectResponse(String projectName) {
        this.projectName = projectName;
    }

    public GitArchivePorjectResponse() {
    }

    public GitArchivePorjectResponse(String status, int index, String message) {
        super(status, index, message);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
