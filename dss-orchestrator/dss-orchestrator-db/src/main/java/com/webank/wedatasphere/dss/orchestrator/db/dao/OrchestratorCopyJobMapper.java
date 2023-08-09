package com.webank.wedatasphere.dss.orchestrator.db.dao;


import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface OrchestratorCopyJobMapper {

    List<DSSOrchestratorCopyInfo> getOrchestratorCopyInfoList(Long orchestratorId);

    String getOrchestratorCopyStatus(Long sourceOrchestratorId);

    void insertOrchestratorCopyInfo(DSSOrchestratorCopyInfo dssOrchestratorCopyInfo);

    void updateCopyStatus(DSSOrchestratorCopyInfo dssOrchestratorCopyInfo);

    void updateErrorMsgById(DSSOrchestratorCopyInfo dssOrchestratorCopyInfo);

    void batchUpdateCopyJob(@Param("failedJobs") List<DSSOrchestratorCopyInfo> failedJobs);

    DSSOrchestratorCopyInfo getOrchestratorCopyInfoById(String id);

    List<DSSOrchestratorCopyInfo> getRunningJob();

}
