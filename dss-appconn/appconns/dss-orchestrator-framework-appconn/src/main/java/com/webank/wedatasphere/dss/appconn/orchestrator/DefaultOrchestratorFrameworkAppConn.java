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

package com.webank.wedatasphere.dss.appconn.orchestrator;

import com.webank.wedatasphere.dss.appconn.orchestrator.standard.OrchestratorFrameworkStandard;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorFrameworkAppConn;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * created by cooperyang on 2020/11/19
 * Description:
 * Orchestrator的实现类，需要实现第三规范
 */
public class DefaultOrchestratorFrameworkAppConn implements OrchestratorFrameworkAppConn {


    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOrchestratorFrameworkAppConn.class);


    private AppDesc appDesc;

    private final List<AppStandard> standards = new ArrayList<>();

    public DefaultOrchestratorFrameworkAppConn(){
        init();
    }


    private void init(){
        standards.add(OrchestratorFrameworkStandard.getInstance(this));
    }


    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public List<AppStandard> getAppStandards() {
        return this.standards;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appDesc;
    }
}
