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

package com.webank.wedatasphere.dss.appconn.orchestrator.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.orchestrator.service.*;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author allenlliu
 */
public class OrchestratorFrameworkStandard extends AbstractLabelDevelopmentIntegrationStandard {

    private volatile static OrchestratorFrameworkStandard instance;

    private AppConn appConn;

    List<RefOperationService> refOperationServices = new ArrayList<>();



    private OrchestratorFrameworkStandard(AppConn appConn){
        this.appConn = appConn;
    }

    public static OrchestratorFrameworkStandard getInstance(AppConn appConn){
        if (instance == null){
            synchronized (OrchestratorFrameworkStandard.class){
                if (instance == null){
                    instance = new OrchestratorFrameworkStandard(appConn);
                }
            }
        }
        return instance;
    }

    @Override
    public AppDesc getAppDesc() {
        return appConn != null ? appConn.getAppDesc() : null;
    }

    @Override
    public void setAppDesc(AppDesc appDesc){

    }

    @Override
    public void init() {
        refOperationServices.add(new OrchestratorCRUDService());
        refOperationServices.add(new OrchestratorExportProcessService());
        refOperationServices.add(new OrchestratorImportProcessService());
        refOperationServices.add(new OrchestratorPublishToSchedulerService());
        refOperationServices.add(new OrchestratorQueryService());
        refOperationServices.add(new OrchestratorVisibleService());
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public String getStandardName() {
        return "OrchestratorFrameworkStandard";
    }

    @Override
    public int getGrade() {
        return 0;
    }

    @Override
    public boolean isNecessary() {
        return false;
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
}
