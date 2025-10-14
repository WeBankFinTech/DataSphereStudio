package com.webank.wedatasphere.dss.git.common.protocol.response;

public class GitAddMemberResponse {
    private String gitUrl;

    public GitAddMemberResponse(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public GitAddMemberResponse() {
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }
}
