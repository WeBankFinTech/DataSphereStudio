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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * created by cooperyang on 2020/12/14
 * Description:
 * 守护线程，用来进行监控发布的过程，由于发布的时间比较长，如果长时间不响应的话，应该要将发布任务取消掉
 */
public class ReleaseJobDaemon implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseJobDaemon.class);

    private AbstractReleaseJob releaseJob;

    private Future<?> releaseFuture;



    public ReleaseJobDaemon(AbstractReleaseJob releaseJob, Future<?> releaseFuture){
        this.releaseJob = releaseJob;
        this.releaseFuture = releaseFuture;
    }


    @Override
    public void run() {
        LOGGER.info("jobId {} release job daemon starts to run ", releaseJob.getJobId());

    }
}
