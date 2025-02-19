package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitAddMemberRequest extends GitBaseRequest{
    private String username;
    private String flowNodeName;

    public GitAddMemberRequest(String username, String flowNodeName) {
        this.username = username;
        this.flowNodeName = flowNodeName;
    }

    public GitAddMemberRequest(Long workspaceId, String projectName, String username, String flowNodeName) {
        super(workspaceId, projectName);
        this.username = username;
        this.flowNodeName = flowNodeName;
    }

    public GitAddMemberRequest() {
    }

    public GitAddMemberRequest(Long workspaceId, String projectName) {
        super(workspaceId, projectName);
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFlowNodeName() {
        return flowNodeName;
    }

    public void setFlowNodeName(String flowNodeName) {
        this.flowNodeName = flowNodeName;
    }
}
