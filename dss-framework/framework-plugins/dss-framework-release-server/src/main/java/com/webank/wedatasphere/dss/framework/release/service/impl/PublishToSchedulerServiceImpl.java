/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.release.service.impl;

import com.webank.wedatasphere.dss.framework.release.context.ReleaseContext;
import com.webank.wedatasphere.dss.framework.release.context.ReleaseEnv;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import com.webank.wedatasphere.dss.framework.release.entity.request.ReleaseOrchestratorRequest;
import com.webank.wedatasphere.dss.framework.release.entity.task.PublishStatus;
import com.webank.wedatasphere.dss.framework.release.entity.task.ReleaseTask;
import com.webank.wedatasphere.dss.framework.release.job.OrchestratorPublishJob;
import com.webank.wedatasphere.dss.framework.release.service.PublishToSchedulerService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.CommonDSSLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;

/**
 * created by cooperyang on 2021/2/23
 * Description:
 */
@Service
public class PublishToSchedulerServiceImpl implements PublishToSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishToSchedulerServiceImpl.class);


    @Autowired
    private ReleaseEnv releaseEnv;

    @Autowired
    private ReleaseContext releaseContext;

    @Override
    public Long publish(String releaseUser, ReleaseOrchestratorRequest releaseOrchestratorRequest, Workspace workspace) {

        Long orchestratorId = releaseOrchestratorRequest.getOrchestratorId();
        Long orchestratorVersionId = releaseOrchestratorRequest.getOrchestratorVersionId();
        String dssLabel = releaseOrchestratorRequest.getDssLabel();
        LOGGER.info("received a release orchestrator request releaseUser is {}, orchestratorId is {}", releaseUser, orchestratorId);
        //1.通过orchestratorVersionId获取到project的信息
        ProjectInfo projectInfo = releaseEnv.getProjectService().getProjectInfoByOrchestratorId(orchestratorId);
        String orchestratorName = releaseEnv.getProjectService().getOrchestratorName(orchestratorId, orchestratorVersionId);

        //2.插入数据到数据库
        // TODO: 2021/4/27 判断当前版本是否正在发布或已发布成功
        ReleaseTask releaseTask = releaseEnv.getTaskService().addReleaseTask(releaseUser,
                projectInfo.getProjectId(),
                orchestratorId,
                orchestratorVersionId, orchestratorName);
        //3.生成任务,然后放入到线程池中
        OrchestratorPublishJob job = new OrchestratorPublishJob();
        job.setReleaseTask(releaseTask);
        job.setReleaseEnv(releaseEnv);
        job.setWorkspace(workspace);
        job.setDssLabel(new CommonDSSLabel(dssLabel));
        releaseContext.submitReleaseJob(job);
        //返回具体的jobId
        LOGGER.info("finished to get release job id for releaseUser {} orchestratorId {}, jobId is {}", releaseUser, orchestratorId, job.getJobId());
        return releaseTask.getId();
    }

    @Override
    public PublishStatus getStatus(String username, Long releaseTaskId) {
        Tuple2<String, String> statusTuple = releaseContext.getReleaseJobStatus(releaseTaskId);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("user {} get status of {} is {}, {}", username, releaseTaskId, statusTuple._1, statusTuple._2);
        }
        return new PublishStatus(statusTuple._1, statusTuple._2, releaseTaskId);
    }
}
