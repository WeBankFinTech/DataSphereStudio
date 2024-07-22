package com.webank.wedatasphere.dss.orchestrator.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class OrchestratorMeta {

    private Long orchestratorId;
    private String proxyUser;
    private String resource;
    private String globalVar;
    private String creator;
    //项目名称
    private String projectName;

    // 项目是否关联git
    private Boolean associateGit;

    // 发布的错误消息
    private String errorMsg;

    // 项目ID
    private Long projectId;

    // 工作空间ID
    private Long workspaceId;

    private String workspaceName;

    // 工作流名称
    private String orchestratorName;

    // 项目描述
    private String description;

    // 工作流状态
    private String status;

    private String statusName;

    // 当前版本
    private  String version;

    // 当前版本更新时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private  Date updateTime;

    // 工作流ID
    private String uuid;

    /**
     * 用途标签，作为描述信息
     */
    private String uses;
    /**
     * 可以支持的实现的appconn节点
     */
    private String appConnName;

    /**
     * 实现的二级类型，比如workflow_DAG
     */
    private String secondaryType;


    private String orchestratorMode;

    private String orchestratorWay;
    /**
     * 编排的重要程度，调度会考察这个重要程度
     */
    private String orchestratorLevel;
    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 是否默认引用资源参数模板
     */
    private String isDefaultReference;


    // 模板名称
    private String templateName;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public OrchestratorMeta() {
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Boolean getAssociateGit() {
        return associateGit;
    }

    public void setAssociateGit(Boolean associateGit) {
        this.associateGit = associateGit;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getGlobalVar() {
        return globalVar;
    }

    public void setGlobalVar(String globalVar) {
        this.globalVar = globalVar;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public String getAppConnName() {
        return appConnName;
    }

    public void setAppConnName(String appConnName) {
        this.appConnName = appConnName;
    }

    public String getSecondaryType() {
        return secondaryType;
    }

    public void setSecondaryType(String secondaryType) {
        this.secondaryType = secondaryType;
    }

    public String getOrchestratorMode() {
        return orchestratorMode;
    }

    public void setOrchestratorMode(String orchestratorMode) {
        this.orchestratorMode = orchestratorMode;
    }

    public String getOrchestratorWay() {
        return orchestratorWay;
    }

    public void setOrchestratorWay(String orchestratorWay) {
        this.orchestratorWay = orchestratorWay;
    }

    public String getOrchestratorLevel() {
        return orchestratorLevel;
    }

    public void setOrchestratorLevel(String orchestratorLevel) {
        this.orchestratorLevel = orchestratorLevel;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
