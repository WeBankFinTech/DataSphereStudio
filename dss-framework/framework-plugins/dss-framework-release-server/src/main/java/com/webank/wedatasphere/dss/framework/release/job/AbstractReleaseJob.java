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

import com.google.common.base.Objects;
import com.webank.wedatasphere.dss.framework.release.context.ReleaseEnv;
import com.webank.wedatasphere.dss.framework.release.entity.task.ReleaseTask;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

/**
 * created by cooperyang on 2020/11/17
 * Description:用于发布的任务线程
 */
public abstract class AbstractReleaseJob implements Runnable {


    protected Long jobId;

    protected double progress;

    protected String errorMsg;

    protected ReleaseStatus status;

    protected String releaseUser;

    protected Long projectId;

    protected ReleaseTask releaseTask;

    protected ReleaseEnv releaseEnv;



    /**
     * 一个发布任务
     */
    protected DSSLabel dssLabel;

    protected DSSLabel nextLabel;

    protected Workspace workspace;


    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    public ReleaseStatus getStatus() {
        return status;
    }

    public void setStatus(ReleaseStatus status) {
        this.status = status;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }


    public ReleaseTask getReleaseTask() {
        return releaseTask;
    }

    public void setReleaseTask(ReleaseTask releaseTask) {
        this.releaseTask = releaseTask;
        this.projectId = releaseTask.getProjectId();
        this.releaseUser = releaseTask.getReleaseUser();
        this.jobId = releaseTask.getId();
    }

    public ReleaseEnv getReleaseEnv() {
        return releaseEnv;
    }

    public void setReleaseEnv(ReleaseEnv releaseEnv) {
        this.releaseEnv = releaseEnv;
    }


    public DSSLabel getDssLabel() {
        return dssLabel;
    }

    public void setDssLabel(DSSLabel dssLabel) {
        this.dssLabel = dssLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractReleaseJob that = (AbstractReleaseJob) o;
        return Objects.equal(jobId, that.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jobId);
    }

    abstract public void close();

    abstract boolean supportMultiEnv();

    public DSSLabel getNextLabel() {
        return nextLabel;
    }

    public void setNextLabel(DSSLabel nextLabel) {
        this.nextLabel = nextLabel;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
