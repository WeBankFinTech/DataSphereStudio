package com.webank.wedatasphere.dss.git.common.protocol.response;


public class GitCheckProjectResponse{
    private String projectName;
    /**
     * true为重复
     */
    private Boolean repeat;

    public GitCheckProjectResponse(String projectName, Boolean repeat) {
        this.projectName = projectName;
        this.repeat = repeat;
    }

    public GitCheckProjectResponse() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Boolean getRepeat() {
        return repeat;
    }

    public void setRepeat(Boolean repeat) {
        this.repeat = repeat;
    }
}
