package com.webank.wedatasphere.dss.orchestrator.db.dao;

import com.webank.wedatasphere.dss.orchestrator.db.entity.OrchestratorPublishJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author alexyang
 * @date 2021/12/2
 * @description
 */
@Mapper
public interface OrchestratorJobMapper {

    OrchestratorPublishJob getPublishJobById(long id);

    OrchestratorPublishJob getPublishJobByJobId(String jobId);

    long insertPublishJob(OrchestratorPublishJob job);

    void updatePublishJob(OrchestratorPublishJob job);

    void deletePublishJob(OrchestratorPublishJob job);

}
