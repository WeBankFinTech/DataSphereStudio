package com.webank.wedatasphere.dss.application.entity;

public class WorkSpacePath {
    private String workspaceRootPath;
    private String hdfsRootPath;
    private String resultRootPath;
    private String schedulerPath;
    private String userPath;

    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }



    public String getWorkspaceRootPath() {
        return workspaceRootPath;
    }

    public void setWorkspaceRootPath(String workspaceRootPath) {
        this.workspaceRootPath = workspaceRootPath;
    }

    public String getHdfsRootPath() {
        return hdfsRootPath;
    }

    public void setHdfsRootPath(String hdfsRootPath) {
        this.hdfsRootPath = hdfsRootPath;
    }

    public String getResultRootPath() {
        return resultRootPath;
    }

    public void setResultRootPath(String resultRootPath) {
        this.resultRootPath = resultRootPath;
    }

    public String getSchedulerPath() {
        return schedulerPath;
    }

    public void setSchedulerPath(String schedulerPath) {
        this.schedulerPath = schedulerPath;
    }








}
