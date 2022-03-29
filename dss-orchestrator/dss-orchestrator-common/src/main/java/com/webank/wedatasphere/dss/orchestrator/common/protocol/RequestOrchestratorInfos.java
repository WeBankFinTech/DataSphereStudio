package com.webank.wedatasphere.dss.orchestrator.common.protocol;

public class RequestOrchestratorInfos {
    private Long workspaceId;
    private Long projectId;
    private String name;
    private String orchestratorMode;


    public RequestOrchestratorInfos(Long workspaceId, Long projectId, String name, String orchestratorMode) {
        this.workspaceId = workspaceId;
        this.projectId = projectId;
        this.name = name;
        this.orchestratorMode = orchestratorMode;
    }

    public RequestOrchestratorInfos() {
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getOrchestratorMode() {
        return orchestratorMode;
    }

    public void setOrchestratorMode(String orchestratorMode) {
        this.orchestratorMode = orchestratorMode;
    }
}
