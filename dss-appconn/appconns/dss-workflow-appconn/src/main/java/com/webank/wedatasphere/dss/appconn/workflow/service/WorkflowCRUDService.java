/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appconn.workflow.service;

import com.webank.wedatasphere.dss.appconn.workflow.opertion.WorkflowTaskCopyOperation;
import com.webank.wedatasphere.dss.appconn.workflow.opertion.WorkflowTaskDeletionOperation;
import com.webank.wedatasphere.dss.appconn.workflow.opertion.WorkflowTaskUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefCRUDService;
import com.webank.wedatasphere.dss.appconn.workflow.opertion.WorkflowTaskCreationOperation;


public class WorkflowCRUDService extends AbstractRefCRUDService {

    @Override
    protected RefCreationOperation createRefCreationOperation() {
        return new WorkflowTaskCreationOperation();
    }

    @Override
    protected RefCopyOperation createRefCopyOperation() {
        return new WorkflowTaskCopyOperation();
    }

    @Override
    protected RefUpdateOperation createRefUpdateOperation() {
        return new WorkflowTaskUpdateOperation();
    }

    @Override
    protected RefDeletionOperation createRefDeletionOperation() {
        return new WorkflowTaskDeletionOperation();
    }

}
