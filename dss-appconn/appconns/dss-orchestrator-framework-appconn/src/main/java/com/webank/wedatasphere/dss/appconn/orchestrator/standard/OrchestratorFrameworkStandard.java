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

package com.webank.wedatasphere.dss.appconn.orchestrator.standard;

import com.webank.wedatasphere.dss.appconn.orchestrator.service.OrchestratorCRUDService;
import com.webank.wedatasphere.dss.appconn.orchestrator.service.OrchestratorExportProcessService;
import com.webank.wedatasphere.dss.appconn.orchestrator.service.OrchestratorImportProcessService;
import com.webank.wedatasphere.dss.appconn.orchestrator.service.OrchestratorQueryService;
import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.development.standard.AbstractDevelopmentIntegrationStandard;

public class OrchestratorFrameworkStandard extends AbstractDevelopmentIntegrationStandard {

    private volatile static OrchestratorFrameworkStandard instance;

    public static OrchestratorFrameworkStandard getInstance(){
        if (instance == null){
            synchronized (OrchestratorFrameworkStandard.class){
                if (instance == null){
                    instance = new OrchestratorFrameworkStandard();
                }
            }
        }
        return instance;
    }

    @Override
    protected RefCRUDService createRefCRUDService() {
        return  new OrchestratorCRUDService();
    }

    @Override
    protected RefExecutionService createRefExecutionService() {
        return null;
    }

    @Override
    protected RefExportService createRefExportService() {
        return  new OrchestratorExportProcessService();
    }

    @Override
    protected RefImportService createRefImportService() {
        return  new OrchestratorImportProcessService();
    }

    @Override
    protected RefQueryService createRefQueryService() {
        return new OrchestratorQueryService();
    }


    @Override
    public String getStandardName() {
        return "OrchestratorFrameworkStandard";
    }


}
