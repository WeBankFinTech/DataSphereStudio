package com.webank.wedatasphere.dss.common.protocol.project;

import java.util.List;

public class ProjectInfoListRequest {
    private List<String> projectNames;

    public List<String> getProjectNames() {
        return projectNames;
    }

    public void setProjectNames(List<String> projectNames) {
        this.projectNames = projectNames;
    }
}
