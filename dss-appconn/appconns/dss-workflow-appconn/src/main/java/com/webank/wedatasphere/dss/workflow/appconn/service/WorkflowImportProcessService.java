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
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportService;
import com.webank.wedatasphere.dss.workflow.appconn.opertion.WorkflowRefImportOperation;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowImportRequestRef;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowImportResponseRef;

/**
 * @author allenlliu
 * @date 2020/11/17 16:56
 */
public class WorkflowImportProcessService implements RefImportService {

    private  DevelopmentService developmentService;


    @Override
    @SuppressWarnings("unchecked")
    public RefImportOperation<WorkflowImportRequestRef, WorkflowImportResponseRef> createRefImportOperation() {
        return new WorkflowRefImportOperation();
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
