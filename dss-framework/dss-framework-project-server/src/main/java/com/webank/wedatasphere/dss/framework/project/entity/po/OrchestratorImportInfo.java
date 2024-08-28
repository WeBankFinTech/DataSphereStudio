package com.webank.wedatasphere.dss.framework.project.entity.po;

/**
 * Author: xlinliu
 * Date: 2024/8/27
 */
public class OrchestratorImportInfo {
    private String orchestratorName;

    private Long orchestratorId;

    private String orchestratorVersion;



    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getOrchestratorVersion() {
        return orchestratorVersion;
    }

    public void setOrchestratorVersion(String orchestratorVersion) {
        this.orchestratorVersion = orchestratorVersion;
    }

    public static OrchestratorImportInfo newInstance(Long orchestratorId,String orchestratorName, String orchestratorVersion){
        OrchestratorImportInfo orchestratorReleaseInfo = new OrchestratorImportInfo();
        orchestratorReleaseInfo.setOrchestratorId(orchestratorId);
        orchestratorReleaseInfo.setOrchestratorName(orchestratorName);
        orchestratorReleaseInfo.setOrchestratorVersion(orchestratorVersion);
        return orchestratorReleaseInfo;
    }
}
