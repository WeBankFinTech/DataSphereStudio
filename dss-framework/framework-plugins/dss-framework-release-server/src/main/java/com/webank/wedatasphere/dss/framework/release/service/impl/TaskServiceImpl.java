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

import com.webank.wedatasphere.dss.framework.release.dao.ReleaseTaskMapper;
import com.webank.wedatasphere.dss.framework.release.entity.task.ReleaseTask;
import com.webank.wedatasphere.dss.framework.release.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * created by cooperyang on 2020/12/24
 * Description:
 */
@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private ReleaseTaskMapper releaseTaskMapper;




    @Override
    public ReleaseTask addReleaseTask(String releaseUser, Long projectId, Long orchestratorId, Long orchestratorVersionId, String orchestratorName) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("begin to add release task, user : {}, projectId: {}, orchestratorId: {}", releaseUser, projectId, orchestratorId);
        }
        ReleaseTask task = ReleaseTask.newTask(releaseUser, projectId, orchestratorId, orchestratorVersionId, orchestratorName);
        releaseTaskMapper.insertReleaseTask(task);
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("end to add release task, user : {}, projectId: {}, orchestratorId: {}", releaseUser, projectId, orchestratorId);
        }
        return task;
    }
}
