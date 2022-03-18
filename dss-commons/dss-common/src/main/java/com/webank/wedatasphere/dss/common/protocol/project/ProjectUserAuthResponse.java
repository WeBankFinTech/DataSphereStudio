package com.webank.wedatasphere.dss.common.protocol.project;

import java.util.List;

public class ProjectUserAuthResponse {
    private Long projectId;
    private String userName;

    private List<Integer> privList;

    private String projectOwner;

    public ProjectUserAuthResponse(Long projectId, String userName, List<Integer> privList, String projectOwner) {
        this.projectId = projectId;
        this.userName = userName;
        this.privList = privList;
        this.projectOwner = projectOwner;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Integer> getPrivList() {
        return privList;
    }

    public void setPrivList(List<Integer> privList) {
        this.privList = privList;
    }

    public String getProjectOwner() {
        return projectOwner;
    }

    public void setProjectOwner(String projectOwner) {
        this.projectOwner = projectOwner;
    }

}
