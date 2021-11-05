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

package com.webank.wedatasphere.dss.standard.app.development.standard;


import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.core.AppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

public interface DevelopmentIntegrationStandard extends AppIntegrationStandard<SSORequestService> {

    RefCRUDService getRefCRUDService(AppInstance appInstance);

    RefExecutionService getRefExecutionService(AppInstance appInstance);

    RefExportService getRefExportService(AppInstance appInstance);

    RefImportService getRefImportService(AppInstance appInstance);

    RefQueryService getRefQueryService(AppInstance appInstance);


    @Override
    default String getStandardName() {
        return "developmentIntegrationStandard";
    }

    @Override
    default int getGrade() {
        return 3;
    }

    @Override
    default boolean isNecessary() {
        return false;
    }

}
