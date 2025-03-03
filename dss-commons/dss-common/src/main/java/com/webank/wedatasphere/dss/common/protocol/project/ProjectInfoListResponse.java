package com.webank.wedatasphere.dss.common.protocol.project;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;

import java.util.List;

public class ProjectInfoListResponse {
    private List<DSSProject> dssProjects;

    public ProjectInfoListResponse(List<DSSProject> dssProjects) {
        this.dssProjects = dssProjects;
    }

    public ProjectInfoListResponse() {
    }

    public List<DSSProject> getDssProjects() {
        return dssProjects;
    }

    public void setDssProjects(List<DSSProject> dssProjects) {
        this.dssProjects = dssProjects;
    }
}
