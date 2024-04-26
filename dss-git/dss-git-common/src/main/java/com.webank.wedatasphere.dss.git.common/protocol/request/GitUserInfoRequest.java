package com.webank.wedatasphere.dss.git.common.protocol.request;

/**
 * @author zhaobincai
 * @date 2024/4/26 15:17
 */
public class GitUserInfoRequest {
    private Long workspaceId;
    private String type;

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
