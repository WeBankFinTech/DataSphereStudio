package com.webank.wedatasphere.dss.git.common.protocol.request;

import java.util.List;

public class GitRemoveRequest extends GitBaseRequest{
    private List<String> path;

    public GitRemoveRequest(Long workspaceId, String projectName, List<String> path) {
        super(workspaceId, projectName);
        this.path = path;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }
}
