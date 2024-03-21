package com.webank.wedatasphere.dss.orchestrator.publish.job;

import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorPublishJob;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorJobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommonUpdateConvertJobStatus {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUpdateConvertJobStatus.class);

    @Autowired
    private OrchestratorJobMapper orchestratorJobMapper;

    public void updateConvertJobStatus(OrchestratorPublishJob orchestratorPublishJob) {
        LOGGER.info("Update convert orchestrator job status to {}", orchestratorPublishJob.getStatus());
        orchestratorPublishJob.setUpdateTime(new Date(System.currentTimeMillis()));
        orchestratorJobMapper.updatePublishJob(orchestratorPublishJob);
    }

    public void toRunningStatus(OrchestratorPublishJob orchestratorPublishJob) {
        orchestratorPublishJob.setStatus(JobStatus.Running.getStatus());
        updateConvertJobStatus(orchestratorPublishJob);
    }

    public void toSuccessStatus(OrchestratorPublishJob orchestratorPublishJob) {
        orchestratorPublishJob.setStatus(JobStatus.Success.getStatus());
        updateConvertJobStatus(orchestratorPublishJob);
    }

    public void toFailedStatus(OrchestratorPublishJob orchestratorPublishJob, String errorMsg) {
        orchestratorPublishJob.setStatus(JobStatus.Failed.getStatus());
        if (errorMsg.length() > 2048) {
            orchestratorPublishJob.setErrorMsg(errorMsg.substring(0, 2048));
        } else {
            orchestratorPublishJob.setErrorMsg(errorMsg);
        }
        updateConvertJobStatus(orchestratorPublishJob);
    }
}
