package com.webank.wedatasphere.dss.workflow.entity.request;

import java.util.Map;

public class EditNodeContentRequest {
    private Long workspaceId;

    private Long projectId;

    private Long orchestratorId;

    private String nodeName;

    private String nodeContent;

    private Map<String,Object> nodeMetadata;

    private String username;


    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeContent() {
        return nodeContent;
    }

    public void setNodeContent(String nodeContent) {
        this.nodeContent = nodeContent;
    }

    public Map<String, Object> getNodeMetadata() {
        return nodeMetadata;
    }

    public void setNodeMetadata(Map<String, Object> nodeMetadata) {
        this.nodeMetadata = nodeMetadata;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
