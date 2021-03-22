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

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;

public abstract class AbstractProcessService implements ProcessService {

    List<RefOperationService> refOperationServices;
    List<DSSLabel> dssLabels;
    AppIntegrationService ssoService;
    DevelopmentIntegrationStandard appStandard;
    AppInstance appInstance;
    AppDesc appDesc;

    public AbstractProcessService(List<RefOperationService> refOperationServices){
        for (RefOperationService refOperationService : refOperationServices) {
            refOperationService.setDevelopmentService(this);
        }
        this.refOperationServices = refOperationServices;
    }

    @Override
    public List<RefOperationService> getRefOperationService() {
        return refOperationServices;
    }

    protected RefOperationService findRefOperationService(Class clazz){
        return getRefOperationService().stream().filter(s -> clazz.isInstance(s)).findAny().get();
    }

    public void setLabels(List<DSSLabel> labels) {
        dssLabels = labels;
    }

    @Override
    public void setSSOService(AppIntegrationService ssoService) {
        this.ssoService = ssoService;
    }

    @Override
    public AppIntegrationService getSSOService() {
        return ssoService;
    }

    @Override
    public void setAppStandard(DevelopmentIntegrationStandard appStandard) {
        this.appStandard = appStandard;
    }

    @Override
    public DevelopmentIntegrationStandard getAppStandard() {
        return appStandard;
    }

    @Override
    public AppInstance getAppInstance() {
        return appInstance;
    }

    @Override
    public void setAppInstance(AppInstance appInstance) {
        this.appInstance = appInstance;
    }

    @Override
    public List<DSSLabel> getLabels() {
        return dssLabels;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public AppDesc getAppDesc() {
        return appDesc;
    }
}
