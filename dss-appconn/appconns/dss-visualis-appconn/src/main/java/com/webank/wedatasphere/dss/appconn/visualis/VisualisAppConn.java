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

package com.webank.wedatasphere.dss.appconn.visualis;

import com.webank.wedatasphere.dss.appconn.core.ext.AlmightyAppConn;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppDescImpl;

import java.util.ArrayList;
import java.util.List;

public class VisualisAppConn implements AlmightyAppConn {

    private AppDesc appDesc;
    private VisualisDevelopmentIntegrationStandard developmentIntegrationStandard;
    private VisualisStructureIntegrationStandard structureIntegrationStandard;

    List<AppStandard> appStandardList = new ArrayList<>();

    public VisualisAppConn() {
        init();
    }

    private void init() {
        if (developmentIntegrationStandard == null)
            developmentIntegrationStandard = new VisualisDevelopmentIntegrationStandard(this);
        if (structureIntegrationStandard == null)
            structureIntegrationStandard = new VisualisStructureIntegrationStandard(this);
//        developmentIntegrationStandard.setAppDesc(this.appDesc);
//        structureIntegrationStandard.setAppDesc(this.appDesc);
        appStandardList.add(developmentIntegrationStandard);
        appStandardList.add(structureIntegrationStandard);
    }

    @Override
    public StructureIntegrationStandard getStructureIntegrationStandard() {
        if (null != appStandardList) {
            for (AppStandard appStandard : appStandardList) {
                if (appStandard instanceof StructureIntegrationStandard) {
                    return (StructureIntegrationStandard) appStandard;
                }
            }
        }
        return null;
    }

    @Override
    public DevelopmentIntegrationStandard getDevelopmentIntegrationStandard() {
        if (null != appStandardList) {
            for (AppStandard appStandard : appStandardList) {
                if (appStandard instanceof DevelopmentIntegrationStandard) {
                    return (DevelopmentIntegrationStandard) appStandard;
                }
            }
        }
        return null;
    }


    @Override
    public List<AppStandard> getAppStandards() {

        return appStandardList;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appDesc;
    }
}
