package com.webank.wedatasphere.dss.git.common.protocol.request;


import java.util.List;

public class GitRevertRequest extends GitBaseRequest{
    private String commitId;
    private List<String> path;

    public GitRevertRequest(Long workspaceId, String projectName, String commitId, List<String> path) {
        super(workspaceId, projectName);
        this.commitId = commitId;
        this.path = path;
    }

    public GitRevertRequest() {
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }
}
