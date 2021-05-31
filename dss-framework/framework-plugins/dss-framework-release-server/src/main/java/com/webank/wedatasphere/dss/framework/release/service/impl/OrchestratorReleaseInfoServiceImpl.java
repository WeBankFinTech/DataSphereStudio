package com.webank.wedatasphere.dss.framework.release.service.impl;

import com.webank.wedatasphere.dss.framework.release.dao.OrchestratorReleaseInfoMapper;
import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;
import com.webank.wedatasphere.dss.framework.release.service.OrchestratorReleaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;

public class OrchestratorReleaseInfoServiceImpl implements OrchestratorReleaseInfoService {

    @Autowired
    private OrchestratorReleaseInfoMapper orchestratorReleaseInfoMapper;

    @Override
    public OrchestratorReleaseInfo getByOrchestratorId(Long orchestratorId) {
        return orchestratorReleaseInfoMapper.getByOrchestratorId(orchestratorId);
    }
}
