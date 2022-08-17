package com.webank.wedatasphere.dss.orchestrator.db.dao;


import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface OrchestratorCopyJobMapper {

    List<DSSOrchestratorCopyInfo> getOrchestratorCopyInfo(Long orchestratorId, Integer currentPage, Integer pageSize);

    String getOrchestratorCopyStatus(Long sourceOrchestratorId);

    void insertOrchestratorCopyInfo(DSSOrchestratorCopyInfo dssOrchestratorCopyInfo);
}
