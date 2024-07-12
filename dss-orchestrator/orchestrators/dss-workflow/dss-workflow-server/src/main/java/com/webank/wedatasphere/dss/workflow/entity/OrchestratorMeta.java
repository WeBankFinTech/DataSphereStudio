package com.webank.wedatasphere.dss.workflow.entity;

import com.webank.wedatasphere.dss.workflow.dto.NodeMetaDO;

public class OrchestratorMeta extends NodeMetaDO {

    //项目名称
    private String projectName;

    // 工作流名称
    private String workflowName;

    // 项目描述
    private String workflowDesc;

    // 工作流状态
    private String status;

    private String statusName;

    // 当前版本
    private  String version;

    // 当前版本更新时间
    private  String updateTime;

    // 当前版本发布者
    private String releaseUser;

    // 默认模板
    private String templateName;

    // 工作流ID
    private String workflowId;



    public OrchestratorMeta(Long id, Long orchestratorId, String proxyUser, String resource, String globalVar, String projectName, String workflowName, String workflowDesc, String status, String statusName, String version, String updateTime, String releaseUser, String templateName, String workflowId) {
        super(id, orchestratorId, proxyUser, resource, globalVar);
        this.projectName = projectName;
        this.workflowName = workflowName;
        this.workflowDesc = workflowDesc;
        this.status = status;
        this.statusName = statusName;
        this.version = version;
        this.updateTime = updateTime;
        this.releaseUser = releaseUser;
        this.templateName = templateName;
        this.workflowId = workflowId;
    }


    public OrchestratorMeta() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }



    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }



    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
