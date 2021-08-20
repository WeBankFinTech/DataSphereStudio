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
import com.webank.wedatasphere.dss.standard.common.core.AbstractAppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;

import java.io.IOException;

public abstract class AbstractDevelopmentIntegrationStandard
    extends AbstractAppIntegrationStandard<DevelopmentService, SSORequestService> implements DevelopmentIntegrationStandard {

    @Override
    protected <T extends DevelopmentService> void initService(T service) {
        service.setAppStandard(this);
    }

    @Override
    public RefCRUDService getRefCRUDService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createRefCRUDService, RefCRUDService.class);
    }

    protected abstract RefCRUDService createRefCRUDService();

    @Override
    public RefExecutionService getRefExecutionService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createRefExecutionService, RefExecutionService.class);
    }

    protected abstract RefExecutionService createRefExecutionService();

    @Override
    public RefExportService getRefExportService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createRefExportService, RefExportService.class);
    }

    protected abstract RefExportService createRefExportService();

    @Override
    public RefImportService getRefImportService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createRefImportService, RefImportService.class);
    }

    protected abstract RefImportService createRefImportService();

    @Override
    public RefQueryService getRefQueryService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createRefQueryService, RefQueryService.class);
    }

    protected abstract RefQueryService createRefQueryService();


    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public void close() throws IOException {

    }
}
