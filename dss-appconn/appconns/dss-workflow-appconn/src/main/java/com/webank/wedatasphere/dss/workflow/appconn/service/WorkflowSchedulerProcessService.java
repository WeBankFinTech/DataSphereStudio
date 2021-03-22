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

package com.webank.wedatasphere.dss.workflow.appconn.service;


import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefPublishToSchedulerService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.OperationalRefScheduleOperation;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.PublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.RefScheduleOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.workflow.appconn.opertion.WorkflowRefScheduleOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author allenlliu
 * @date 2020/11/25 10:28
 */
public class WorkflowSchedulerProcessService implements RefPublishToSchedulerService {
    private final static Logger logger = LoggerFactory.getLogger(WorkflowSchedulerProcessService.class);

    private DevelopmentService developmentService;

    @Override
    public RefScheduleOperation createRefScheduleOperation() {
        return new WorkflowRefScheduleOperation();
    }

    @Override
    public OperationalRefScheduleOperation createOperationalRefScheduleOperation(RequestRef requestRef) {
        return null;
    }

    @Override
    public DevelopmentService getDevelopmentService() {
        return this.developmentService;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService =developmentService;
    }
}
