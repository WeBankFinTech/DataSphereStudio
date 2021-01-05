package com.webank.wedatasphere.dss.application.entity;

public class WorkSpacePath {
    private String WorkspaceRootPath;
    private String HdfsRootPath;

    public String getWorkspaceRootPath() {
        return WorkspaceRootPath;
    }

    public void setWorkspaceRootPath(String workspaceRootPath) {
        WorkspaceRootPath = workspaceRootPath;
    }

    public String getHdfsRootPath() {
        return HdfsRootPath;
    }

    public void setHdfsRootPath(String hdfsRootPath) {
        HdfsRootPath = hdfsRootPath;
    }

    public String getResultRootPath() {
        return ResultRootPath;
    }

    public void setResultRootPath(String resultRootPath) {
        ResultRootPath = resultRootPath;
    }

    public String getSchedulerPath() {
        return SchedulerPath;
    }

    public void setSchedulerPath(String schedulerPath) {
        SchedulerPath = schedulerPath;
    }

    private String ResultRootPath;
    private String SchedulerPath;
}
