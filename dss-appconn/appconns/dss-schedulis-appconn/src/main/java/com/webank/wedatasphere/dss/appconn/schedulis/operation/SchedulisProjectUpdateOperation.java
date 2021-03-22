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

package com.webank.wedatasphere.dss.appconn.schedulis.operation;

import com.webank.wedatasphere.dss.appconn.schedulis.service.SchedulisProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by cooperyang on 2020/11/13
 * Description:
 */
public class SchedulisProjectUpdateOperation implements ProjectUpdateOperation {


    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisProjectUpdateOperation.class);

    private SchedulisProjectService schedulisProjectService;

    public SchedulisProjectUpdateOperation(SchedulisProjectService projectService){
        this.schedulisProjectService = projectService;
    }

    @Override
    public void setStructureService(StructureService service) {
        this.schedulisProjectService = (SchedulisProjectService) service;
    }

    @Override
    public ProjectResponseRef updateProject(ProjectRequestRef projectRef) throws ExternalOperationFailedException {
        return null;
    }
}
