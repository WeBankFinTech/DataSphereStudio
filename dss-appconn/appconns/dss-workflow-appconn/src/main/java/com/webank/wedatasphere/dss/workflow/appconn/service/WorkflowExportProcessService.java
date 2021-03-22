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
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.workflow.appconn.opertion.WorkflowRefExportOperation;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowExportRequestRef;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowExportResponseRef;


/**
 * @author allenlliu
 * @date 2020/10/26 04:09 PM
 */
public class WorkflowExportProcessService implements RefExportService {
    private DevelopmentService developmentService;


    @Override
    @SuppressWarnings("unchecked")
    public RefExportOperation<WorkflowExportRequestRef, WorkflowExportResponseRef> createRefExportOperation() {
        WorkflowRefExportOperation operation = new WorkflowRefExportOperation();
        operation.setDevelopmentService(getDevelopmentService());
        return operation;
    }



    @Override
    public DevelopmentService getDevelopmentService() {
        return developmentService;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
