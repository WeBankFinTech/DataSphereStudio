/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.server.publish;


import com.webank.wedatasphere.dss.server.service.DSSProjectService;
import com.webank.wedatasphere.dss.common.entity.project.DSSProjectPublishHistory;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Component
public class PublishManager implements PublishListner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DSSProjectService projectService;

    private Map<Long, PublishProjectCache> cacheMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("定时线程开启...");
        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (cacheMap) {
                    cacheMap.entrySet().stream()
                            .filter(f -> (f.getValue().getStatus() == PublishJobEnum.Succeed || f.getValue().getStatus() == PublishJobEnum.Failed) && f.getValue().timeout())
                            .forEach(f -> {logger.info("开始remove过期记录：{},更新时间{}",f.getKey(),f.getValue().getUpdateTime());cacheMap.remove(f.getKey());});
                }
            }
        }, 1, 5, TimeUnit.MINUTES);
        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (cacheMap) {
                    cacheMap.forEach((k, v) -> {
                        logger.info("cache中projectVersionID为：{}状态为：{}更新时间为：{}",k,v.getStatus(),v.getUpdateTime());
                    });
                }
            }
        }, 1, 10, TimeUnit.MINUTES);
    }

    @Override
    public void onPublishSucceed(Long projectVersionID, String infoMsg) {
        // TODO: 2019/6/21 目前不用加锁
        logger.info(infoMsg);
        Date updateTime = new Date();
        PublishProjectCache cache = cacheMap.get(projectVersionID);
        cache.setUpdateTime(updateTime);
        cache.setStatus(PublishJobEnum.Succeed);
        projectService.updatePublishHistory(projectVersionID, PublishJobEnum.Succeed.ordinal(), updateTime);
    }

    @Override
    public void onPublishFailed(Long projectVersionID, String errorMsg) {
        logger.info(errorMsg);
        Date updateTime = new Date();
        PublishProjectCache cache = cacheMap.get(projectVersionID);
        cache.setUpdateTime(updateTime);
        cache.setStatus(PublishJobEnum.Failed);
        cache.setMsg(errorMsg);
        projectService.updatePublishHistory(projectVersionID, PublishJobEnum.Failed.ordinal(), updateTime);
    }

    @Override
    public void onPublishRunning(Long projectVersionID, String infoMsg) {
        logger.info(infoMsg);
        Date updateTime = new Date();
        PublishProjectCache cache = cacheMap.get(projectVersionID);
        cache.setUpdateTime(updateTime);
        cache.setStatus(PublishJobEnum.Running);
        projectService.updatePublishHistory(projectVersionID, PublishJobEnum.Running.ordinal(), updateTime);
    }

    public void addPublishCache(Long projectVersionID, Long creatorID, String comment) throws DSSErrorException {
        if (cacheMap.get(projectVersionID) == null) {
            synchronized (cacheMap) {
                if (cacheMap.get(projectVersionID) == null) {
                    DSSProjectPublishHistory history = projectService.getPublishHistoryByID(projectVersionID);
                    if (history == null) {
                        logger.info("创建一个发布job" + projectVersionID);
                        projectService.createPublishHistory(comment, creatorID, projectVersionID);
                    } else if (PublishJobEnum.Succeed.ordinal() == history.getState()) {
                        throw new DSSErrorException(90008, "该工程版本已经发布成功，请不要重复发布");
                    } else if (PublishJobEnum.Failed.ordinal() == history.getState()) {
                        logger.info("数据库中的工程发布状态为失败，运行重新发布" + projectVersionID);
                        projectService.updatePublishHistory(projectVersionID, PublishJobEnum.Inited.ordinal(), new Date());
                    }
                    // TODO: 2019/6/21 数据库中running或则inited的
                    PublishProjectCache newCache = new PublishProjectCache();
                    newCache.setStatus(PublishJobEnum.Inited);
                    newCache.setCreateTime(new Date());
                    newCache.setUpdateTime(new Date());
                    newCache.setProjectVersionID(projectVersionID);
                    cacheMap.put(projectVersionID, newCache);
                } else {
                    throw new DSSErrorException(90006, "该工程版本已经发布过，请不要重复发布");
                }
            }
        } else {
            PublishProjectCache cache = cacheMap.get(projectVersionID);
            switch (cache.getStatus()) {
                case Inited:
                case Running:
                case Succeed:
                    logger.info("当前cache状态为：" + cache.getStatus().toString());
                    throw new DSSErrorException(90006, "该工程版本已经发布过，请不要重复发布");
                case Failed:
                    synchronized (cache) {
                        if (cache.getStatus() == PublishJobEnum.Failed) {
                            logger.info("当前cache状态为：Failed，准备转换为Inited");
                            cache.setStatus(PublishJobEnum.Inited);
                            cache.setMsg(null);
                            cache.setUpdateTime(new Date());
                        } else {
                            throw new DSSErrorException(90006, "该工程版本已经发布过，请不要重复发布");
                        }
                    }
                    break;
                default:
                    logger.error("unsupport status");
            }
        }
    }

    public PublishProjectCache getPublishCache(Long projectVersionID) {
        return cacheMap.get(projectVersionID);
    }

    public void checkeIsPublishing(Long projectVersionID) throws DSSErrorException {
        PublishProjectCache publishCache = getPublishCache(projectVersionID);
        if(publishCache != null && publishCache.isPublishing()){
            logger.info("工程正在发布中,projectVersionID:{}，状态为{}",projectVersionID,publishCache.getStatus());
            throw new DSSErrorException(90014,"工程正在发布中，不允许执行当前操作");
        }
    }
}
