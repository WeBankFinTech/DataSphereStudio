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

package com.webank.wedatasphere.dss.workflow.scheduler;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public abstract class DssJobManager implements DssJobListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<Long, DssJobInfo> cacheMap = new ConcurrentHashMap<>();

    private final Map<Long, DssJobInfo> secondCacheMap = new ConcurrentHashMap<>();//执行完成后将放入二级缓存,只有qurey的时候才去掉

    private List<DssJobHook> jobHooks = new ArrayList<>();

    public void addDssJobHook(DssJobHook jobHook) {
        jobHooks.add(jobHook);
    }

    {
        logger.info("定时线程开启...");
        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (secondCacheMap) {
                    secondCacheMap.entrySet().stream()
                            .filter(f -> (f.getValue().getStatus() == DssJobStatus.Succeed || f.getValue().getStatus() == DssJobStatus.Failed) && f.getValue().timeout())
                            .forEach(f -> {
                                logger.info("开始remove second cache过期记录：{},更新时间{}", f.getKey(), f.getValue().getUpdateTime());
                                secondCacheMap.remove(f.getKey());
                            });
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (cacheMap) {
                    cacheMap.forEach((k, v) -> {
                        logger.info("cache中id为：{}状态为：{}更新时间为：{}", k, v.getStatus(), v.getUpdateTime());
                    });
                }
            }
        }, 1, 10, TimeUnit.MINUTES);
    }

    @Override
    public void onJobSucceed(DssJob job, String infoMsg) {
        // TODO: 2019/6/21 目前不用加锁
        job.setStatus(DssJobStatus.Succeed);
        logger.info(infoMsg);
        Date updateTime = new Date();
        DssJobInfo cache = cacheMap.get(job.getId());
        cache.setUpdateTime(updateTime);
        cache.setStatus(DssJobStatus.Succeed);
        synchronized (String.valueOf(job.getId()).intern()) {
            secondCacheMap.put(job.getId(), cacheMap.remove(job.getId()));
        }
        jobHooks.forEach(h -> h.postExecute(job));
    }

    @Override
    public void onJobFailed(DssJob job, String errorMsg) {
        job.setStatus(DssJobStatus.Failed);
        logger.info(errorMsg);
        Date updateTime = new Date();
        DssJobInfo cache = cacheMap.get(job.getId());
        cache.setUpdateTime(updateTime);
        cache.setStatus(DssJobStatus.Failed);
        cache.setMsg(errorMsg);
        synchronized (String.valueOf(job.getId()).intern()) {
            secondCacheMap.put(job.getId(), cacheMap.remove(job.getId()));
        }
        jobHooks.forEach(h -> h.postExecute(job));
    }

    @Override
    public void onJobRunning(DssJob job, String infoMsg) {
        job.setStatus(DssJobStatus.Running);
        logger.info(infoMsg);
        Date updateTime = new Date();
        DssJobInfo cache = cacheMap.get(job.getId());
        cache.setUpdateTime(updateTime);
        cache.setStatus(DssJobStatus.Running);
    }

    protected void addPublishCache(DssJobInfo job) throws DSSErrorException {
        job.setStatus(DssJobStatus.Inited);
        job.setCreateTime(new Date());
        job.setUpdateTime(new Date());
        cacheMap.put(job.getId(), job);
    }

    private DssJobInfo getJobInfoCache(Long id) {
        return cacheMap.get(id);
    }

    private DssJobInfo getSecondJobInfoCache(Long id) {
        return secondCacheMap.get(id);
    }

    public void checkeIsExecuting(Long id) throws DSSErrorException {
        DssJobInfo jobInfo = getJobInfoCache(id);
        DssJobInfo seCondInfo = getSecondJobInfoCache(id);
        if (jobInfo != null && jobInfo.isExecute() || seCondInfo != null) {
            DssJobStatus status = jobInfo == null ? seCondInfo.getStatus() : jobInfo.getStatus();
            logger.info("job执行中,id:{}，状态为{}", id, status);
            throw new DSSErrorException(90014, "job执行中，不允许执行当前操作");
        }
    }

    private DssJobInfo createJobInfo(Long id) {
        DssJobInfo jobInfo = new DssJobInfo();
        jobInfo.setId(id);
        return jobInfo;
    }

    public void submit(DssJob job) throws DSSErrorException {
        synchronized (String.valueOf(job.getId()).intern()) {
            checkeIsExecuting(job.getId());
            addPublishCache(createJobInfo(job.getId()));
        }
        job.setStatus(DssJobStatus.Inited);
        jobHooks.forEach(h -> h.preExecute(job));
        Future<?> submit = DssJobThreadPool.get().submit(job);
        DssJobDeamon deamon = new DssJobDeamon(submit, (int) DSSWorkFlowConstant.PUBLISH_TIMEOUT.getValue());
        deamon.setId(job.getId());
        DssJobThreadPool.getDeamon().execute(deamon);
    }

    public DssJobInfo getJobStatus(Long id) throws DSSErrorException {
        synchronized (String.valueOf(id).intern()) {
            DssJobInfo jobInfo = getJobInfoCache(id);
            if (jobInfo != null) {
                return jobInfo;
            } else {
                DssJobInfo remove = secondCacheMap.remove(id);
                if (remove == null) throw new DSSErrorException(90021, String.format("the id %s is not exist", id));
                return remove;
            }
        }
    }

}
