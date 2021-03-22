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

package com.webank.wedatasphere.dss.workflow.appconn;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.workflow.appconn.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allenlliu
 * @date 2020/10/21 11:58 AM
 */
public class WorkflowDevelopmentIntegrationStandard extends AbstractLabelDevelopmentIntegrationStandard {


    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowDevelopmentIntegrationStandard.class);
    private AppConn appConn;


    private  List<RefOperationService> refOperationServices = new ArrayList<>();



    public WorkflowDevelopmentIntegrationStandard(AppConn appConn){
       this.appConn = appConn;
    }


    @Override
    public AppDesc getAppDesc()
     {
            return appConn != null ? appConn.getAppDesc() : null;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
       this.appConn.setAppDesc(appDesc);
    }

    @Override
    public void init() {
        LOGGER.info("class WorkflowDevelopmentIntegrationStandard init");
        refOperationServices.add(new WorkflowCRUDService());
        refOperationServices.add(new WorkflowExportProcessService());
        refOperationServices.add(new WorkflowImportProcessService());
        refOperationServices.add(new WorkflowSchedulerProcessService());
        refOperationServices.add(new WorkflowQueryService());
        refOperationServices.add(new WorkflowVisibleService());
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    protected List<RefOperationService> getRefOperationService() {
        synchronized (this){
            if (refOperationServices.size() == 0){
                synchronized (this){
                    init();
                }
            }
        }

        return refOperationServices;
    }

    @Override
    public String getStandardName() {
        return "WorkflowDevelopmentIntegrationStandard";
    }

    @Override
    public int getGrade() {
        return 0;
    }

    @Override
    public boolean isNecessary() {
        return false;
    }
}
