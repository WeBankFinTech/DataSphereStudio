package com.webank.wedatasphere.dss.workflow.dto;


public class NodeMetaDO {
    private Long id;
    private Long orchestratorId;
    private String proxyUser;
    private String resource;

    public NodeMetaDO() {
    }

    public NodeMetaDO(Long id, Long orchestratorId, String proxyUser, String resource) {
        this.id = id;
        this.orchestratorId = orchestratorId;
        this.proxyUser = proxyUser;
        this.resource = resource;
    }

    public NodeMetaDO(Long orchestratorId, String proxyUser, String resource) {
        this.orchestratorId = orchestratorId;
        this.proxyUser = proxyUser;
        this.resource = resource;
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
}
