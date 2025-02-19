package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitRenameRequest extends GitBaseRequest{
    private String oldName;

    private String name;

    private String username;

    public GitRenameRequest(String oldName, String name, String username) {
        this.oldName = oldName;
        this.name = name;
        this.username = username;
    }

    public GitRenameRequest(Long workspaceId, String projectName, String oldName, String name, String username) {
        super(workspaceId, projectName);
        this.oldName = oldName;
        this.name = name;
        this.username = username;
    }

    public GitRenameRequest() {
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
