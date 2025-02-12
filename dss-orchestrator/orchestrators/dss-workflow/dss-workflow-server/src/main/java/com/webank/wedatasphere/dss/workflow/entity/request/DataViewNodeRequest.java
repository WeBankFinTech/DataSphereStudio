package com.webank.wedatasphere.dss.workflow.entity.request;

import java.util.List;

public class DataViewNodeRequest {


    private Integer pageNow;

    private Integer pageSize;

    private List<String> nodeTypeNameList;

    private List<String> nodeNameList;

    private String orchestratorName;

    private List<String> projectNameList;

    private String viewId;


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

    public List<String> getNodeTypeNameList() {
        return nodeTypeNameList;
    }

    public void setNodeTypeNameList(List<String> nodeTypeNameList) {
        this.nodeTypeNameList = nodeTypeNameList;
    }

    public List<String> getNodeNameList() {
        return nodeNameList;
    }

    public void setNodeNameList(List<String> nodeNameList) {
        this.nodeNameList = nodeNameList;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public List<String> getProjectNameList() {
        return projectNameList;
    }

    public void setProjectNameList(List<String> projectNameList) {
        this.projectNameList = projectNameList;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }
}
