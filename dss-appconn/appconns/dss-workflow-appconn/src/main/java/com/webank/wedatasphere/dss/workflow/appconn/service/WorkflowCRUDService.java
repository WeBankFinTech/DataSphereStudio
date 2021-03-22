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
import com.webank.wedatasphere.dss.standard.app.development.crud.*;
import com.webank.wedatasphere.dss.workflow.appconn.opertion.WorkflowTaskCopyOperation;
import com.webank.wedatasphere.dss.workflow.appconn.opertion.WorkflowTaskCreationOperation;
import com.webank.wedatasphere.dss.workflow.appconn.opertion.WorkflowTaskDeletionOperation;
import com.webank.wedatasphere.dss.workflow.appconn.opertion.WorkflowTaskUpdateOperation;


/**
 * @author allenlliu
 * @date 2020/10/21 12:02 PM
 */
public class WorkflowCRUDService implements RefCRUDService {
    private DevelopmentService developmentService;

    @Override
    public RefCreationOperation createTaskCreationOperation() {
        WorkflowTaskCreationOperation workflowTaskCreationOperation = new WorkflowTaskCreationOperation();
        workflowTaskCreationOperation.setDevelopmentService(developmentService);
        return workflowTaskCreationOperation;
    }

    @Override
    public RefCopyOperation createRefCopyOperation() {
        WorkflowTaskCopyOperation workflowTaskCopyOperation = new WorkflowTaskCopyOperation();
        workflowTaskCopyOperation.setDevelopmentService(developmentService);
        return workflowTaskCopyOperation;
    }

    @Override
    public RefUpdateOperation createRefUpdateOperation() {
        WorkflowTaskUpdateOperation workflowTaskUpdateOperation = new WorkflowTaskUpdateOperation();
        workflowTaskUpdateOperation.setDevelopmentService(developmentService);
        return workflowTaskUpdateOperation;
    }

    @Override
    public RefDeletionOperation createRefDeletionOperation() {
        WorkflowTaskDeletionOperation workflowTaskDeletionOperation = new WorkflowTaskDeletionOperation();
        workflowTaskDeletionOperation.setDevelopmentService(developmentService);
        return workflowTaskDeletionOperation;
    }


    @Override
    public DevelopmentService getDevelopmentService() {
         return this.developmentService;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
         this.developmentService = developmentService;
    }
}
