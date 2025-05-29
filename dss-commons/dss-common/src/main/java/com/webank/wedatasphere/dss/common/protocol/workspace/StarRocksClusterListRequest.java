package com.webank.wedatasphere.dss.common.protocol.workspace;

public class StarRocksClusterListRequest {

    private Long workspaceId;

    public StarRocksClusterListRequest(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }



}
