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

package com.webank.wedatasphere.dss.framework.release.job;

import com.webank.wedatasphere.dss.framework.release.entity.export.ExportResult;
import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import com.webank.wedatasphere.dss.framework.release.entity.resource.BmlResource;
import com.webank.wedatasphere.dss.framework.release.entity.task.ReleaseTask;
import com.webank.wedatasphere.dss.standard.common.desc.EnvironmentLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * created by cooperyang on 2020/11/17
 * Description: 一个编排模式发布的任务
 */
public class OrchestratorReleaseJob extends AbstractReleaseJob{


    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorReleaseJob.class);


    private Long orchestratorId;

    private Long orchestratorVersionId;

    private String orchestratorName;


    public Long getOrchestratorVersionId() {
        return orchestratorVersionId;
    }

    public void setOrchestratorVersionId(Long orchestratorVersionId) {
        this.orchestratorVersionId = orchestratorVersionId;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    @Override
    public void setReleaseTask(ReleaseTask releaseTask) {
        super.setReleaseTask(releaseTask);
        this.orchestratorVersionId = releaseTask.getOrchestratorVersionId();
        this.orchestratorId = releaseTask.getOrchestratorId();
        this.orchestratorName = releaseTask.getOrchestratorName();
    }

    @Override
    boolean supportMultiEnv() {
        return true;
    }

    @Override
    public void run() {
        LOGGER.info("begin to do release project {} orchestratorId is {} versionId is {}", projectId, orchestratorId, orchestratorVersionId);
        try{
            this.setStatus(ReleaseStatus.RUNNING);
            this.releaseEnv.getReleaseJobListener().onJobStatusUpdate(this);
            String workspaceName = this.releaseEnv.getProjectService().getWorkspaceName(projectId);
            ProjectInfo projectInfo = this.releaseEnv.getProjectService().getProjectInfoById(projectId);
            //1.进行导出
            ExportResult exportResult = this.releaseEnv.getExportService().export(releaseUser,
                    projectId, orchestratorId, orchestratorVersionId, projectInfo.getProjectName(), workspaceName, dssLabel, workspace);
            List<BmlResource> resourceList = exportResult.getBmlResourceList();
            //2.进行工程级别的封装


            //3.进行导入
            //todo nextLabel要用labelservice来进行获取
            nextLabel = new EnvironmentLabel("PROD");

            List<OrchestratorReleaseInfo> importOrcInfos = this.releaseEnv.getImportService().importOrc(orchestratorName, releaseUser,
                    projectInfo, resourceList, nextLabel, workspaceName, workspace);
            //4.进行同步到调度中心
            this.releaseEnv.getPublishService().publish(releaseUser, projectInfo, importOrcInfos, nextLabel, workspace, supportMultiEnv());
            //5.将新的versionId更新到原来的表中
            if (exportResult.getOrchestratorVersionId() != null && exportResult.getOrchestratorVersionId() > 0){
                this.releaseEnv.getProjectService().updateProjectOrcInfo(projectInfo.getProjectId(), orchestratorId, exportResult.getOrchestratorVersionId());
            }else{
                LOGGER.error("export Result orcVersionId is {}", exportResult.getOrchestratorVersionId());
            }
            //6.如果都没有报错，那么默认任务应该是成功的,那么则将所有的状态进行置为完成
            this.releaseEnv.getReleaseJobListener().onJobSucceed(this);
        }catch(final Exception e){
            LOGGER.error("export for orchestrator {} failed", orchestratorId, e);
            this.releaseEnv.getReleaseJobListener().onJobFailed(this, "export failed" + e.getMessage());
        }
    }

    @Override
    public void close() {
        //ensure to close
    }
}
