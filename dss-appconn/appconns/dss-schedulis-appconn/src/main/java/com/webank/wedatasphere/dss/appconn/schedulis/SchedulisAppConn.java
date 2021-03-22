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

package com.webank.wedatasphere.dss.appconn.schedulis;

import com.webank.wedatasphere.dss.appconn.schedulis.standard.SchedulisStructureStandard;
import com.webank.wedatasphere.dss.appconn.schedule.core.SchedulerAppConn;
import com.webank.wedatasphere.dss.standard.app.sso.origin.OriginSSOIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * created by cooperyang on 2020/11/11
 * Description:
 */
public class SchedulisAppConn implements SchedulerAppConn {


    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisAppConn.class);

    private final List<AppStandard> standards = new ArrayList<>();

    private AppDesc appDesc;

    public SchedulisAppConn() {
        init();
    }

    public void init() {
        standards.add(OriginSSOIntegrationStandard.getSSOIntegrationStandard());
        standards.add(SchedulisStructureStandard.getInstance(this));
    }


    @Override
    public StructureIntegrationStandard getStructureIntegrationStandard() {
        for (AppStandard appStandard : standards) {
            if (appStandard instanceof SchedulisStructureStandard) {
                return (StructureIntegrationStandard) appStandard;
            }
        }
        return null;
    }

    @Override
    public OriginSSOIntegrationStandard getOriginSSOIntegrationStandard() {
        for (AppStandard appStandard : standards) {
            if (appStandard instanceof OriginSSOIntegrationStandard) {
                return (OriginSSOIntegrationStandard) appStandard;
            }
        }
        return null;
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

    @Override
    public boolean supportFlowSchedule() {
        return false;
    }


}
