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

package com.webank.wedatasphere.dss.appconn.qualitis;

import com.webank.wedatasphere.dss.appconn.qualitis.execution.QualitisExecutionService;
import com.webank.wedatasphere.dss.appconn.qualitis.service.QualitisCrudService;
import com.webank.wedatasphere.dss.appconn.qualitis.service.QualitisQueryService;
import com.webank.wedatasphere.dss.appconn.qualitis.service.QualitisRefExportService;
import com.webank.wedatasphere.dss.appconn.qualitis.service.QualitisRefImportService;
import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.development.standard.AbstractDevelopmentIntegrationStandard;

public class QualitisDevelopmentIntegrationStandard extends AbstractDevelopmentIntegrationStandard {

    @Override
    protected RefCRUDService createRefCRUDService() {
        return new QualitisCrudService();
    }

    @Override
    protected RefExecutionService createRefExecutionService() { return new QualitisExecutionService(); }

    @Override
    protected RefExportService createRefExportService() {
        return new QualitisRefExportService();
    }

    @Override
    protected RefImportService createRefImportService() { return new QualitisRefImportService(); }

    @Override
    protected RefQueryService createRefQueryService() {
        return new QualitisQueryService();
    }

}
