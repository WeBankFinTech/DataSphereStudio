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

package com.webank.wedatasphere.dss.orchestrator.converter.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.ConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.RelToOrchestratorConversionService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.core.AbstractAppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;

import java.io.IOException;


public abstract class AbstractConversionIntegrationStandard extends AbstractAppIntegrationStandard<ConversionService, SSORequestService>
    implements ConversionIntegrationStandard {

    private AppConn appConn;

    protected abstract DSSToRelConversionService createDSSToRelConversionService();

    /**
     * 预留接口，用于支持将调度系统的工作流，转换成DSS编排
     * @return 目前返回 null 即可
     */
    protected RelToOrchestratorConversionService createRelToDSSConversionService() {
        return null;
    }

    @Override
    public final DSSToRelConversionService getDSSToRelConversionService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createDSSToRelConversionService, DSSToRelConversionService.class);
    }

    @Override
    public final RelToOrchestratorConversionService getRelToDSSConversionService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createRelToDSSConversionService, RelToOrchestratorConversionService.class);
    }

    @Override
    protected <T extends ConversionService> void initService(T service) {
        super.initService(service);
        service.setAppStandard(this);
    }

    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public AppConn getAppConn() {
        return appConn;
    }

    public void setAppConn(AppConn appConn) {
        this.appConn = appConn;
    }

    @Override
    public String getAppConnName() {
        return appConn.getAppDesc().getAppName();
    }

    @Override
    public void close() throws IOException {

    }
}
