package com.webank.wedatasphere.dss.common.protocol.project;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;

import java.util.ArrayList;
import java.util.List;

public class ProjectListQueryResponse {

    private List<DSSProject> projectList;


    // ！！！ 新增字段 也不要更改这个构造函数
    public ProjectListQueryResponse(List<DSSProject> projectList) {
        this.projectList = projectList;
    }

    public ProjectListQueryResponse() {
    }

    public List<DSSProject> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<DSSProject> projectList) {
        this.projectList = projectList;
    }
}
