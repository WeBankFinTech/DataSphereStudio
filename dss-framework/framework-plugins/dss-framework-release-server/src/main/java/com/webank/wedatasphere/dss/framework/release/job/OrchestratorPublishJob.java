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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.wedatasphere.dss.framework.release.entity.export.ExportResult;
import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import com.webank.wedatasphere.dss.framework.release.entity.task.ReleaseTask;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.CommonDSSLabel;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

/**
 * created by cooperyang on 2021/2/23
 * Description: orchestrator的发布，由开发中心直接发布到schedulis等调度系统
 */
public final class OrchestratorPublishJob extends AbstractReleaseJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorPublishJob.class);

    private Long orchestratorId;

    private Long orchestratorVersionId;

    private String orchestratorName;

    private String comment;

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setReleaseTask(ReleaseTask releaseTask) {
        super.setReleaseTask(releaseTask);
        this.orchestratorId = releaseTask.getOrchestratorId();
        this.orchestratorVersionId = releaseTask.getOrchestratorVersionId();
        this.orchestratorName = releaseTask.getOrchestratorName();
    }

    @Override
    boolean supportMultiEnv() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public void run() {
        //1.从编排中心导出一次工作流,进行一次版本升级
        //2.进行发布到schedulis等调度系统
        LOGGER.info("begin to publish orchestratorId {} for user {}", orchestratorId, releaseTask.getReleaseUser());
        try{
            this.setStatus(ReleaseStatus.RUNNING);
            this.releaseEnv.getReleaseJobListener().onJobStatusUpdate(this);

            String workspaceName = this.releaseEnv.getProjectService().getWorkspaceName(projectId);
            ProjectInfo projectInfo = this.releaseEnv.getProjectService().getProjectInfoById(projectId);

            //1.进行同步到调度中心
            List<OrchestratorReleaseInfo> importOrcInfos = new ArrayList<>();
            Long appId = this.getReleaseEnv()
                .getProjectService()
                .getAppIdByOrchestratorVersionId(orchestratorVersionId);
            importOrcInfos.add(OrchestratorReleaseInfo.newInstance(orchestratorId, orchestratorVersionId, appId));
            nextLabel = new CommonDSSLabel("DEV");
            // 临时解决获取空间名称
            Workspace publishWorkspace = new Workspace();
            publishWorkspace.setWorkspaceName(workspaceName);
            this.releaseEnv.getPublishService()
                .publish(releaseUser, projectInfo, importOrcInfos, nextLabel, publishWorkspace, supportMultiEnv());

            //2. 更新发布orchestrator时的描述信息
            this.releaseEnv.getProjectService().updateCommentInOrchestratorInfo(comment, orchestratorId);

            //3.进行导出,用于升级版本,目的是为了复用原来的代码
            ExportResult exportResult = this.releaseEnv.getExportService()
                .export(releaseUser, projectId, orchestratorId, orchestratorVersionId, projectInfo.getProjectName(),
                    workspaceName, dssLabel, workspace);

            //4.如果都没有报错，那么默认任务应该是成功的,那么则将所有的状态进行置为完成
            this.releaseEnv.getReleaseJobListener().onJobSucceed(this);
        } catch (final ErrorException e) {
            LOGGER.error("publish orchestrator {} failed", orchestratorId, e);
            this.releaseEnv.getReleaseJobListener().onJobFailed(this, "发布失败：" + e.getDesc());
        } catch (final Throwable t) {
            LOGGER.error("publish orchestrator {} failed", orchestratorId, t);
            this.releaseEnv.getReleaseJobListener().onJobFailed(this, "发布失败：" + t.getMessage());
        }
    }
}
