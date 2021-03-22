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

package com.webank.wedatasphere.dss.framework.release.job.listener;

import com.webank.wedatasphere.dss.framework.release.dao.ReleaseTaskMapper;
import com.webank.wedatasphere.dss.framework.release.job.AbstractReleaseJob;
import com.webank.wedatasphere.dss.framework.release.job.ReleaseStatus;
import com.webank.wedatasphere.dss.framework.release.job.hook.ReleaseHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * created by cooperyang on 2020/12/14
 * Description:
 */
@Component("releaseJobListener")
public class CommonReleaseJobListener implements ReleaseJobListener{



    private static final Logger LOGGER = LoggerFactory.getLogger(CommonReleaseJobListener.class);

    @Autowired
    private ReleaseTaskMapper releaseTaskMapper;

    @Autowired
    private ReleaseHook[] releaseHooks;

    @Override
    public void onJobStatusUpdate(AbstractReleaseJob releaseJob) {
        LOGGER.info("releaseJob {} change state to {}", releaseJob.getJobId(), releaseJob.getStatus().getStatus());
        Date currentTime = new Date(System.currentTimeMillis());
        releaseJob.getReleaseTask().setStatus(releaseJob.getStatus().getStatus());
        releaseJob.getReleaseTask().setUpdateTime(currentTime);
        releaseTaskMapper.updateReleaseTask(releaseJob.getReleaseTask());
    }

    @Override
    public void onJobSucceed(AbstractReleaseJob releaseJob) {
        releaseJob.setStatus(ReleaseStatus.SUCCESS);
        releaseJob.setProgress(1.0);
        onJobStatusUpdate(releaseJob);
        Arrays.stream(releaseHooks).forEach(releaseHook -> releaseHook.postRelease(releaseJob));
    }

    @Override
    public void onJobFailed(AbstractReleaseJob releaseJob, String errorMsg) {
        releaseJob.setStatus(ReleaseStatus.FAILED);
        releaseJob.setErrorMsg(errorMsg);
        releaseJob.setProgress(1.0);
        onJobStatusUpdate(releaseJob);
        Arrays.stream(releaseHooks).forEach(releaseHook -> releaseHook.postRelease(releaseJob));
    }

    @Override
    public void onJobTimeOut(AbstractReleaseJob releaseJob) {
        releaseJob.setStatus(ReleaseStatus.FAILED);
        releaseJob.setErrorMsg("release timeout");
        onJobStatusUpdate(releaseJob);
        Arrays.stream(releaseHooks).forEach(releaseHook -> releaseHook.postRelease(releaseJob));
    }
}
