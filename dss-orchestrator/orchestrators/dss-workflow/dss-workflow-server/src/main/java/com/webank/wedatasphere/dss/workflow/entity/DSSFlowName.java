package com.webank.wedatasphere.dss.workflow.entity;

import java.util.ArrayList;
import java.util.List;

public class DSSFlowName {


    private List<String> nodeNameList = new ArrayList<>();

    private List<String> orchestratorNameList = new ArrayList<>();

    private List<String> templateNameList = new ArrayList<>();


    public List<String> getNodeNameList() {
        return nodeNameList;
    }

    public void setNodeNameList(List<String> nodeNameList) {
        this.nodeNameList = nodeNameList;
    }

    public List<String> getOrchestratorNameList() {
        return orchestratorNameList;
    }

    public void setOrchestratorNameList(List<String> orchestratorNameList) {
        this.orchestratorNameList = orchestratorNameList;
    }

    public List<String> getTemplateNameList() {
        return templateNameList;
    }

    public void setTemplateNameList(List<String> templateNameList) {
        this.templateNameList = templateNameList;
    }
}
