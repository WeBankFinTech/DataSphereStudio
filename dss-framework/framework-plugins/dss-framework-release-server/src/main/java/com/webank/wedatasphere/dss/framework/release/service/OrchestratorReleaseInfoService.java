package com.webank.wedatasphere.dss.framework.release.service;

import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;

public interface OrchestratorReleaseInfoService {

    OrchestratorReleaseInfo getByOrchestratorId(Long orchestratorId);
}
