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

package com.webank.wedatasphere.dss.appconn.schedulis.service;

import com.webank.wedatasphere.dss.appconn.schedulis.operation.SchedulisProjectCreationOperation;
import com.webank.wedatasphere.dss.appconn.schedulis.operation.SchedulisProjectDeletionOperation;
import com.webank.wedatasphere.dss.appconn.schedulis.operation.SchedulisProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created by cooperyang on 2020/11/13
 * Description:
 */
public class SchedulisProjectService implements ProjectService {


    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisProjectService.class);


    private AppInstance appInstance;

    private StructureIntegrationStandard structureIntegrationStandard;

    private AppDesc appDesc;

    private Map<Class<? extends Operation>, Operation<?, ?>> operationMap = new ConcurrentHashMap<>();



    public SchedulisProjectService(AppDesc appDesc, AppInstance appInstance, StructureIntegrationStandard structureIntegrationStandard){
        this.appDesc = appDesc;
        this.appInstance = appInstance;
        this.structureIntegrationStandard = structureIntegrationStandard;
        init();
    }

    private void init(){
        operationMap.put(SchedulisProjectCreationOperation.class, new SchedulisProjectCreationOperation(this));
        operationMap.put(SchedulisProjectUpdateOperation.class, new SchedulisProjectUpdateOperation(this));
        operationMap.put(SchedulisProjectDeletionOperation.class, new SchedulisProjectDeletionOperation(this));
    }



    @Override
    public ProjectCreationOperation createProjectCreationOperation() {
        if (operationMap.containsKey(SchedulisProjectCreationOperation.class)){
            return (SchedulisProjectCreationOperation)operationMap.get(SchedulisProjectCreationOperation.class);
        } else{
            operationMap.put(SchedulisProjectCreationOperation.class, new SchedulisProjectCreationOperation(this));
            return (SchedulisProjectCreationOperation)operationMap.get(SchedulisProjectCreationOperation.class);
        }
    }

    @Override
    public ProjectUpdateOperation createProjectUpdateOperation() {
        if (operationMap.containsKey(SchedulisProjectUpdateOperation.class)){
            return (SchedulisProjectUpdateOperation)operationMap.get(SchedulisProjectUpdateOperation.class);
        } else{
            operationMap.put(SchedulisProjectUpdateOperation.class, new SchedulisProjectUpdateOperation(this));
            return (SchedulisProjectUpdateOperation)operationMap.get(SchedulisProjectUpdateOperation.class);
        }
    }

    @Override
    public ProjectDeletionOperation createProjectDeletionOperation() {
        if (operationMap.containsKey(SchedulisProjectDeletionOperation.class)){
            return (SchedulisProjectDeletionOperation)operationMap.get(SchedulisProjectDeletionOperation.class);
        } else {
            operationMap.put(SchedulisProjectDeletionOperation.class, new SchedulisProjectDeletionOperation(this));
            return (SchedulisProjectDeletionOperation)operationMap.get(SchedulisProjectDeletionOperation.class);
        }
    }

    @Override
    public ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }

    @Override
    public AppInstance getAppInstance() {
        return this.appInstance;
    }

    @Override
    public void setAppInstance(AppInstance appInstance) {
        this.appInstance = appInstance;
    }

    @Override
    public void setSSOService(AppIntegrationService ssoService) {

    }

    @Override
    public AppIntegrationService getSSOService() {
        return null;
    }

    @Override
    public void setAppStandard(StructureIntegrationStandard appStandard) {
        this.structureIntegrationStandard = appStandard;
    }

    @Override
    public StructureIntegrationStandard getAppStandard() {
        return this.structureIntegrationStandard;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appDesc;
    }

    @Override
    public Operation createOperation(Class<? extends Operation> clazz) {
        return this.operationMap.get(clazz);
    }

    @Override
    public boolean isOperationExists(Class<? extends Operation> clazz) {
        return this.operationMap.containsKey(clazz);
    }

    @Override
    public boolean isOperationNecessary(Class<? extends Operation> clazz) {
        return isOperationExists(clazz);
    }
}
