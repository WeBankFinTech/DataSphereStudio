package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;

public class OrchestratorStatusVo {


    private String status;
    private String name;


    public OrchestratorStatusVo(String status, String name) {
        this.status = status;
        this.name = name;
    }

    public OrchestratorStatusVo() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
