package com.webank.wedatasphere.dss.orchestrator.publish.job;

import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorJobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


public class CommonUpdateConvertJobStatus {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUpdateConvertJobStatus.class);

    @Autowired
    private OrchestratorJobMapper orchestratorJobMapper;

    public void updateConvertJobStatus(OrchestratorConversionJob orchestratorConversionJob) {
        LOGGER.info("Update convert orchestrator job status to {}", orchestratorConversionJob
                .getConversionJobEntity().getOrchestratorPublishJob().getStatus());
        orchestratorConversionJob.getConversionJobEntity().getOrchestratorPublishJob().setCreateTime(new Date(System.currentTimeMillis()));
        orchestratorJobMapper.updatePublishJob(orchestratorConversionJob.getConversionJobEntity().getOrchestratorPublishJob());
    }

    public void toRunningStatus(OrchestratorConversionJob orchestratorConversionJob) {
        orchestratorConversionJob.getConversionJobEntity().getOrchestratorPublishJob().setStatus(JobStatus.Running.getStatus());
        updateConvertJobStatus(orchestratorConversionJob);
    }

    public void toSuccessStatus(OrchestratorConversionJob orchestratorConversionJob) {
        orchestratorConversionJob.getConversionJobEntity().getOrchestratorPublishJob().setStatus(JobStatus.Success.getStatus());
        updateConvertJobStatus(orchestratorConversionJob);
    }

    public void toFailedStatus(OrchestratorConversionJob orchestratorConversionJob) {
        orchestratorConversionJob.getConversionJobEntity().getOrchestratorPublishJob().setStatus(JobStatus.Failed.getStatus());
        updateConvertJobStatus(orchestratorConversionJob);
    }

}
