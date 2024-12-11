package com.webank.wedatasphere.dss.orchestrator.common.protocol;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

import java.util.Map;

public class RequestOrcDelete {

    private String userName;
    private Long workspaceId;
    private Long projectId;
    private Long orchestratorId;

    protected String workspaceName;
    protected Map<String, String> cookies;

    public RequestOrcDelete(String userName, Long workspaceId, Long projectId, Long orchestratorId, String workspaceName, Map<String, String> cookies) {
        this.userName = userName;
        this.workspaceId = workspaceId;
        this.projectId = projectId;
        this.orchestratorId = orchestratorId;
        this.workspaceName = workspaceName;
        this.cookies = cookies;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    public LabelRouteVO getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(LabelRouteVO dssLabels) {
        this.dssLabels = dssLabels;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }
}
