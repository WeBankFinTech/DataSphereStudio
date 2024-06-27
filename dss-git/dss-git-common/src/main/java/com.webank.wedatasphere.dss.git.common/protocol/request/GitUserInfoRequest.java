package com.webank.wedatasphere.dss.git.common.protocol.request;


public class GitUserInfoRequest {
    private Long workspaceId;
    private String type;
    /**
     * 密码 token 是否解密 true-解密
     */
    private Boolean decrypt;

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

    public Boolean getDecrypt() {
        return decrypt;
    }

    public void setDecrypt(Boolean decrypt) {
        this.decrypt = decrypt;
    }
}
