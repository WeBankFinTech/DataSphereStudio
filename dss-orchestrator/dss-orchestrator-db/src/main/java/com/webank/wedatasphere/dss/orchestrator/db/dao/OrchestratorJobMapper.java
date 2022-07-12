package com.webank.wedatasphere.dss.orchestrator.db.dao;

import com.webank.wedatasphere.dss.orchestrator.db.entity.OrchestratorPublishJob;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrchestratorJobMapper {

    OrchestratorPublishJob getPublishJobById(long id);

    OrchestratorPublishJob getPublishJobByJobId(String jobId);

    long insertPublishJob(OrchestratorPublishJob job);

    void updatePublishJob(OrchestratorPublishJob job);

    void deletePublishJob(OrchestratorPublishJob job);

}
