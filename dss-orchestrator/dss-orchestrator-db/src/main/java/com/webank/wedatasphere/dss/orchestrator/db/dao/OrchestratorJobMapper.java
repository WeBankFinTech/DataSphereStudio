package com.webank.wedatasphere.dss.orchestrator.db.dao;

import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorPublishJob;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrchestratorJobMapper {

    OrchestratorPublishJob getPublishJobById(long id);

    OrchestratorPublishJob getPublishJobByJobId(String jobId);

    List<OrchestratorPublishJob> getPublishJobByJobStatuses(List<Integer> statuses);

    long insertPublishJob(OrchestratorPublishJob job);

    void updatePublishJob(OrchestratorPublishJob job);

    void deletePublishJob(OrchestratorPublishJob job);

    void batchUpdatePublishJob(List<OrchestratorPublishJob> jobs);

}
