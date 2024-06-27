package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;

public class OrchestratorRollBackGitVo {
    private DSSOrchestratorVersion oldOrcVersion;
    private DSSOrchestratorVersion dssOrchestratorVersion;
    private DSSOrchestratorInfo dssOrchestratorInfo;
    private String version;

    public DSSOrchestratorVersion getOldOrcVersion() {
        return oldOrcVersion;
    }

    public void setOldOrcVersion(DSSOrchestratorVersion oldOrcVersion) {
        this.oldOrcVersion = oldOrcVersion;
    }

    public DSSOrchestratorVersion getDssOrchestratorVersion() {
        return dssOrchestratorVersion;
    }

    public void setDssOrchestratorVersion(DSSOrchestratorVersion dssOrchestratorVersion) {
        this.dssOrchestratorVersion = dssOrchestratorVersion;
    }

    public DSSOrchestratorInfo getDssOrchestratorInfo() {
        return dssOrchestratorInfo;
    }

    public void setDssOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
        this.dssOrchestratorInfo = dssOrchestratorInfo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
