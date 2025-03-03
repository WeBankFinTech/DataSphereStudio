package com.webank.wedatasphere.dss.orchestrator.server.entity.request;

import javax.validation.constraints.NotNull;
public class ModifyOrchestratorMetaRequest {

    // 编排ID
    @NotNull(message = "编排Id不能为空")
    private Long orchestratorId;

    // 工作流ID
    @NotNull(message = "工作流Id不能为空")
    private String uuid;

    // 编排名称
    @NotNull(message = "工作流名称不能为空")
    private String  orchestratorName;

    // 描述
    @NotNull(message = "工作流描述不能为空")
    private String description;

    // 命名空间ID
    private Long workspaceId;

    // 命名空间名称
    private String workspaceName;

    // 项目ID
    @NotNull(message = "项目Id不能为空")
    private Long projectId;

    // 项目名称
    private  String projectName;

    // 是否默认引用资源参数模板
    private String isDefaultReference;

    // 代理用户
    private String proxyUser;


    public ModifyOrchestratorMetaRequest() {
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getIsDefaultReference() {
        return isDefaultReference;
    }

    public void setIsDefaultReference(String isDefaultReference) {
        this.isDefaultReference = isDefaultReference;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
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
}
