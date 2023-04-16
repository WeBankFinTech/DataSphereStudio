package com.webank.wedatasphere.dss.orchestrator.db.dao;

import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorPublishJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrchestratorJobMapper {

    OrchestratorPublishJob getPublishJobByJobId(@Param("jobId") String jobId);

    List<OrchestratorPublishJob> getPublishJobByJobStatuses(@Param("statuses") List<String> statuses);

    long insertPublishJob(OrchestratorPublishJob job);

    void updatePublishJob(OrchestratorPublishJob job);

    void batchUpdatePublishJob(@Param("failedJobs") List<OrchestratorPublishJob> failedJobs);

}
