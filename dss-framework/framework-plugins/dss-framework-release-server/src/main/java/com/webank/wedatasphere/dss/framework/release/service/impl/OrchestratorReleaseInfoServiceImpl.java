package com.webank.wedatasphere.dss.framework.release.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.wedatasphere.dss.framework.release.dao.OrchestratorReleaseInfoMapper;
import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;
import com.webank.wedatasphere.dss.framework.release.service.OrchestratorReleaseInfoService;

@Service
public class OrchestratorReleaseInfoServiceImpl implements OrchestratorReleaseInfoService {

    @Autowired
    private OrchestratorReleaseInfoMapper orchestratorReleaseInfoMapper;

    @Override
    public OrchestratorReleaseInfo getByOrchestratorId(Long orchestratorId) {
        return orchestratorReleaseInfoMapper.getByOrchestratorId(orchestratorId);
    }

    @Override
    public int removeById(Long id) {
        return orchestratorReleaseInfoMapper.removeById(id);
    }

    @Override
    public int removeByOrchestratorId(Long orchestratorId) {
        return orchestratorReleaseInfoMapper.removeByOrchestratorId(orchestratorId);
    }
}
