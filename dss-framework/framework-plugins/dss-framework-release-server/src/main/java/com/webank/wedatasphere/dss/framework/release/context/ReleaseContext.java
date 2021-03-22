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

package com.webank.wedatasphere.dss.framework.release.context;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.webank.wedatasphere.dss.framework.release.dao.ReleaseTaskMapper;
import com.webank.wedatasphere.dss.framework.release.entity.task.ReleaseTask;
import com.webank.wedatasphere.dss.framework.release.job.AbstractReleaseJob;
import com.webank.wedatasphere.dss.framework.release.job.ReleaseJobDaemon;
import com.webank.wedatasphere.dss.framework.release.job.ReleaseStatus;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.Map;
import java.util.concurrent.*;

/**
 * created by cooperyang on 2020/12/10
 * Description:
 */
@Component
public final class ReleaseContext {


    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseContext.class);

    @Autowired
    private ReleaseTaskMapper taskMapper;

    private static final Long CACHE_DURATION = CommonVars.apply("wds.dss.release.duration.time", 60 * 60L).getValue();

    private final ThreadFactory releaseThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("dss-release-thread-%d")
            .setDaemon(false)
            .build();
    private final ExecutorService releaseThreadPool = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), releaseThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    private final ThreadFactory daemonThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("dss-release-daemon-thread-%d")
            .setDaemon(true)
            .build();

    private final ExecutorService daemonThreadPool = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), daemonThreadFactory, new ThreadPoolExecutor.AbortPolicy());


    private final Map<AbstractReleaseJob, Future<?>>  jobFutureMap = new ConcurrentHashMap<>();

    private Cache<Long, AbstractReleaseJob> releaseJobCache = CacheBuilder.newBuilder()
            .expireAfterAccess(CACHE_DURATION, TimeUnit.SECONDS)
            .build();


    private ReleaseContext(){

    }

    public ExecutorService getReleaseThreadPool(){
        return this.releaseThreadPool;
    }

    public void submitReleaseJob(AbstractReleaseJob releaseJob){
        LOGGER.info("begin to submit release job {} to thread pool", releaseJob);
        Future<?> future = releaseThreadPool.submit(releaseJob);
        jobFutureMap.put(releaseJob, future);
        //此处应该启动一个Daemon线程,用于对已经提交的任务进行监控,方便进行kill，因为可能发布结束不了导致出问题
        ReleaseJobDaemon releaseJobDaemon = new ReleaseJobDaemon(releaseJob, future);
        releaseJobCache.put(releaseJob.getJobId(), releaseJob);
        LOGGER.info("end to submit release job {} to thread pool", releaseJob);
    }

    public void cancelReleaseJob(AbstractReleaseJob releaseJob){

    }



    public Tuple2<String, String> getReleaseJobStatus(Long jobId){
        AbstractReleaseJob releaseJob = releaseJobCache.getIfPresent(jobId);
        if (null != releaseJob){
            return new Tuple2<>(releaseJob.getStatus().getStatus(),  releaseJob.getErrorMsg());
        } else {
            //要从数据库中去捞出来
            ReleaseTask releaseTask = taskMapper.getReleaseTask(jobId);
            if(releaseTask != null){
                return new Tuple2<>(releaseTask.getStatus(), releaseTask.getErrorMsg());
            }else{
                LOGGER.error("{} releaseTask is null", jobId);
                //如果没有这个的jobId,则没有在数据库中存过,直接判Failed
                return new Tuple2<>(ReleaseStatus.FAILED.getStatus(), "releaseTask is null");
            }
        }
    }






}
