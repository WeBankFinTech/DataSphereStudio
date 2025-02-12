package com.webank.wedatasphere.dss.workflow.dto;


public class NodeMetaDO {
    private Long id;
    private Long orchestratorId;
    private String proxyUser;
    private String metaResource;
    private String globalVar;

    public NodeMetaDO() {
    }

    public NodeMetaDO(Long id, Long orchestratorId, String proxyUser, String metaResource, String globalVar) {
        this.id = id;
        this.orchestratorId = orchestratorId;
        this.proxyUser = proxyUser;
        this.metaResource = metaResource;
        this.globalVar = globalVar;
    }

    public NodeMetaDO(Long orchestratorId, String proxyUser, String metaResource, String globalVar) {
        this.orchestratorId = orchestratorId;
        this.proxyUser = proxyUser;
        this.metaResource = metaResource;
        this.globalVar = globalVar;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMetaResource() {
        return metaResource;
    }

    public void setMetaResource(String metaResource) {
        this.metaResource = metaResource;
    }

    public String getGlobalVar() {
        return globalVar;
    }

    public void setGlobalVar(String globalVar) {
        this.globalVar = globalVar;
    }
}
