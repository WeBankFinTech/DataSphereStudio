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

package com.webank.wedatasphere.dss.standard.app.development.process;

import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.execution.RefExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefPublishToSchedulerService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryService;
import com.webank.wedatasphere.dss.standard.common.service.Operation;

import java.util.List;

public class DevProcessService extends AbstractProcessService {


    public DevProcessService(List<RefOperationService> refOperationServices) {
        super(refOperationServices);
    }

    public RefCRUDService getRefCRUDService(){
        return (RefCRUDService) findRefOperationService(RefCRUDService.class);
    }

    public RefExecutionService getRefExecutionService(){
        return (RefExecutionService) findRefOperationService(RefExecutionService.class);
    }

    public RefExportService getRefExportService(){
        return (RefExportService) findRefOperationService(RefExportService.class);
    }

    public RefQueryService getRefQueryService(){
        return (RefQueryService) findRefOperationService(RefQueryService.class);
    }

    public RefPublishToSchedulerService getRefPublishToSchedulerService(){
        return (RefPublishToSchedulerService) findRefOperationService(RefPublishToSchedulerService.class);
    }

    @Override
    public Operation createOperation(Class<? extends Operation> clazz) {
        return null;
    }

    @Override
    public boolean isOperationExists(Class<? extends Operation> clazz) {
        return false;
    }

    @Override
    public boolean isOperationNecessary(Class<? extends Operation> clazz) {
        return false;
    }

}
