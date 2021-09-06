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

package com.webank.wedatasphere.dss.appconn.visualis;

import com.webank.wedatasphere.dss.appconn.visualis.execution.VisualisExecutionService;
import com.webank.wedatasphere.dss.appconn.visualis.service.*;
import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.development.standard.AbstractDevelopmentIntegrationStandard;

public class VisualisDevelopmentIntegrationStandard extends AbstractDevelopmentIntegrationStandard {

    @Override
    protected RefCRUDService createRefCRUDService() {
        return new VisualisCRUDService();
    }

    @Override
    protected RefExecutionService createRefExecutionService() {
        return new VisualisExecutionService();
    }

    @Override
    protected RefExportService createRefExportService() {
        return new VisualisRefExportService();
    }

    @Override
    protected RefImportService createRefImportService() {
        return new VisualisRefImportService();
    }

    @Override
    protected RefQueryService createRefQueryService() {
        return new VisualisQueryService();
    }

}
