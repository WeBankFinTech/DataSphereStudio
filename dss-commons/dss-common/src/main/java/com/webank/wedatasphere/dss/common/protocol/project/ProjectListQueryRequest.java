package com.webank.wedatasphere.dss.common.protocol.project;

public class ProjectListQueryRequest {

    private Long workspaceId;

    private String username;

    // ！！！ 新增字段 也不要更改这个构造函数
    public ProjectListQueryRequest(Long workspaceId, String username) {
        this.workspaceId = workspaceId;
        this.username = username;
    }



    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
