package com.webank.wedatasphere.dss.git.common.protocol.request;


import com.webank.wedatasphere.dss.common.entity.BmlResource;

public class GitCreateProjectRequest extends GitBaseRequest{
    /**
     * DSS项目对应的BML
     */
    private BmlResource bmlResource;
    /**
     * 下载BMLReource使用的用户名
     */
    private String username;

    public GitCreateProjectRequest() {
    }

    public GitCreateProjectRequest(BmlResource bmlResource, String username) {
        this.bmlResource = bmlResource;
        this.username = username;
    }

    public GitCreateProjectRequest(Long workspaceId, String projectName, BmlResource bmlResource, String username) {
        super(workspaceId, projectName);
        this.bmlResource = bmlResource;
        this.username = username;
    }

    public BmlResource getBmlResource() {
        return bmlResource;
    }

    public void setBmlResource(BmlResource bmlResource) {
        this.bmlResource = bmlResource;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
