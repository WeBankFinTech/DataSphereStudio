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
    private String gitUser;
    private String gitToken;

    public GitCreateProjectRequest() {
    }


    public GitCreateProjectRequest(BmlResource bmlResource, String username, String gitUser, String gitToken) {
        this.bmlResource = bmlResource;
        this.username = username;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
    }

    public GitCreateProjectRequest(Long workspaceId, String projectName, BmlResource bmlResource, String username, String gitUser, String gitToken) {
        super(workspaceId, projectName);
        this.bmlResource = bmlResource;
        this.username = username;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
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

    public String getGitUser() {
        return gitUser;
    }

    public void setGitUser(String gitUser) {
        this.gitUser = gitUser;
    }

    public String getGitToken() {
        return gitToken;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }
}
