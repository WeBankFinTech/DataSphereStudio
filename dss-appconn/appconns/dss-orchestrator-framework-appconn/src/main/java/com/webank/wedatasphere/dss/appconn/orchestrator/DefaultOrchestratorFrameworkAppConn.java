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

package com.webank.wedatasphere.dss.appconn.orchestrator;

import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.core.impl.AbstractAppConn;
import com.webank.wedatasphere.dss.appconn.orchestrator.standard.OrchestratorFrameworkStandard;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorFrameworkAppConn;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultOrchestratorFrameworkAppConn extends AbstractAppConn implements OrchestratorFrameworkAppConn, OnlyDevelopmentAppConn {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOrchestratorFrameworkAppConn.class);

    private OrchestratorFrameworkStandard orchestratorFrameworkStandard;
    private AppDesc appDesc;

    private final List<AppStandard> standards = new ArrayList<>();


    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public List<AppStandard> getAppStandards() {
        return this.standards;
    }

    @Override
    protected void initialize() {
        orchestratorFrameworkStandard = OrchestratorFrameworkStandard.getInstance();
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appDesc;
    }

    @Override
    public DevelopmentIntegrationStandard getOrCreateDevelopmentStandard() {
        return OrchestratorFrameworkStandard.getInstance();
    }
}
