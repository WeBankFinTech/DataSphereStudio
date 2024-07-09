package com.webank.wedatasphere.dss.workflow.dto;


public class NodeMetaDO {
    private Long id;
    private Long orchestratorId;
    private String proxyUser;
    private String resource;
    private String globalVar;

    public NodeMetaDO() {
    }

    public NodeMetaDO(Long id, Long orchestratorId, String proxyUser, String resource, String globalVar) {
        this.id = id;
        this.orchestratorId = orchestratorId;
        this.proxyUser = proxyUser;
        this.resource = resource;
        this.globalVar = globalVar;
    }

    public NodeMetaDO(Long orchestratorId, String proxyUser, String resource, String globalVar) {
        this.orchestratorId = orchestratorId;
        this.proxyUser = proxyUser;
        this.resource = resource;
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
}
