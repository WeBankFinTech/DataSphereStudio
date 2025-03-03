package com.webank.wedatasphere.dss.orchestrator.common.entity;

public class OrchestratorTemplateInfo {

    private Long orchestratorId;

    private String templateName;


    public OrchestratorTemplateInfo() {
    }

    public OrchestratorTemplateInfo(Long orchestratorId, String templateName) {
        this.orchestratorId = orchestratorId;
        this.templateName = templateName;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
