package com.webank.wedatasphere.dss.workflow.entity.request;

public class QueryNodeInfoByPathRequest {
    /**
     * 节点所在项目id
     */
    private Long projectId;


    // 节点在git中的路径
    private String path;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
