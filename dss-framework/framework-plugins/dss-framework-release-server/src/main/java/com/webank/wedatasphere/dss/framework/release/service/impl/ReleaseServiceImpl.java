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
import com.webank.wedatasphere.dss.framework.release.entity.task.ReleaseTask;
import com.webank.wedatasphere.dss.framework.release.job.OrchestratorReleaseJob;
import com.webank.wedatasphere.dss.framework.release.service.ReleaseService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.CommonDSSLabel;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.util.List;

/**
 * created by cooperyang on 2020/11/17
 * Description:
 */
@Service
public class ReleaseServiceImpl implements ReleaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseServiceImpl.class);


    @Autowired
    private ReleaseEnv releaseEnv;

    @Autowired
    private ReleaseContext releaseContext;



    @Override
    public String releaseOrchestratorBatch(String releaseUser, List<Long> orchestratorVersionIds, String dssLabel) throws ErrorException {
        return null;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Long releaseOrchestrator(String releaseUser, Long orchestratorVersionId, Long orchestratorId, String dssLabel, Workspace workspace) throws ErrorException {

        LOGGER.info("received a release orchestrator request releaseUser is {}, orchestratorVersionId is {}", releaseUser, orchestratorVersionId);

        //1.通过orchestratorVersionId获取到project的信息
        ProjectInfo projectInfo = releaseEnv.getProjectService().getProjectInfoByOrchestratorId(orchestratorId);
//        if (projectInfo == null){
//            LOGGER.error("orchestratorVersionId {} projectInfo is null, will not go on", orchestratorVersionId);
//            DSSExceptionUtils.dealErrorException(60032, "projectInfo is null", ReleaseFrameworkErrorException.class);
//        }
        String orchestratorName = releaseEnv.getProjectService().getOrchestratorName(orchestratorId, orchestratorVersionId);

        //2.插入数据到数据库
        ReleaseTask releaseTask = releaseEnv.getTaskService().addReleaseTask(releaseUser,
                projectInfo.getProjectId(),
                orchestratorId,
                orchestratorVersionId, orchestratorName);
        //3.生成任务,然后放入到线程池中
        OrchestratorReleaseJob job = new OrchestratorReleaseJob();
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
    public Tuple2<String, String> getStatus(Long jobId) {
        return releaseContext.getReleaseJobStatus(jobId);
    }
}
