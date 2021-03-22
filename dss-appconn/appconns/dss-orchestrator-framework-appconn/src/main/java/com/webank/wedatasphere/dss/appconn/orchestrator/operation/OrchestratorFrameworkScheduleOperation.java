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

package com.webank.wedatasphere.dss.appconn.orchestrator.operation;

import com.webank.wedatasphere.dss.appconn.orchestrator.stage.OrchestratorPublishToSchedulerStage;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.PublishToSchedulerStage;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.RefScheduleOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

/**
 * @author allenlliu
 * @date 2020/12/15 15:28
 */
public class OrchestratorFrameworkScheduleOperation implements RefScheduleOperation<RequestRef, ResponseRef> {

    private DevelopmentService service;
    @Override
    public PublishToSchedulerStage createPublishToSchedulerStage() {

        OrchestratorPublishToSchedulerStage orchestratorPublishToSchedulerStage = new OrchestratorPublishToSchedulerStage();
        orchestratorPublishToSchedulerStage.setSchedulerRefOperation(this);
        return orchestratorPublishToSchedulerStage;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.service = service;
    }
}
