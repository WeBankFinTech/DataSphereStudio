package com.webank.wedatasphere.dss.git.common.protocol.request;

import java.util.List;

public class GitRemoveRequest extends GitBaseRequest{
    private List<String> path;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;


    public GitRemoveRequest(List<String> path, String username) {
        this.path = path;
        this.username = username;
    }

    public GitRemoveRequest(Long workspaceId, String projectName, List<String> path, String username) {
        super(workspaceId, projectName);
        this.path = path;
        this.username = username;
    }

    public GitRemoveRequest() {
    }

    public GitRemoveRequest(Long workspaceId, String projectName) {
        super(workspaceId, projectName);
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }
}
