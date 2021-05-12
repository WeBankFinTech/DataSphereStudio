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

package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by cooperyang on 2020/11/13
 * Description:
 */
public class SchedulisProjectDeletionOperation implements ProjectDeletionOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisProjectDeletionOperation.class);

    private DolphinSchedulerProjectService schedulisProjectService;

    public SchedulisProjectDeletionOperation(DolphinSchedulerProjectService schedulisProjectService) {
        this.schedulisProjectService = schedulisProjectService;
    }

    @Override
    public void setStructureService(StructureService service) {

    }

    @Override
    public ProjectResponseRef deleteProject(ProjectRequestRef projectRef) throws ExternalOperationFailedException {
        return null;
    }
}
