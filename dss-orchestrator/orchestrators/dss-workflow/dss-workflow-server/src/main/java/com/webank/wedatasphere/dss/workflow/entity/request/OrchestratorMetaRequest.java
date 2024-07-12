package com.webank.wedatasphere.dss.workflow.entity.request;

import java.util.List;

public class OrchestratorMetaRequest {


    private Integer pageNow;
    private Integer pageSize;

    private List<String> projectList;

    private String workflowName;

    private String status;

    private Long  workspaceId;

    public OrchestratorMetaRequest() {
    }

    public OrchestratorMetaRequest(Integer pageNow, Integer pageSize, List<String> projectList, String workflowName, String status,Long workspaceId) {
        this.pageNow = pageNow;
        this.pageSize = pageSize;
        this.projectList = projectList;
        this.workflowName = workflowName;
        this.status = status;
        this.workspaceId = workspaceId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Integer getPageNow() {
        return pageNow;
    }

    public void setPageNow(Integer pageNow) {
        this.pageNow = pageNow;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<String> projectList) {
        this.projectList = projectList;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
