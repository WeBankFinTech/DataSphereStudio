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

package com.webank.wedatasphere.dss.appconn.eventchecker;

import com.webank.wedatasphere.dss.appconn.core.ext.DevAppConn;
import com.webank.wedatasphere.dss.appconn.eventchecker.standard.EventCheckerDevelopmentStandard;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author allenlliu
 * @date 2021/1/20 15:09
 */
public class EventCheckerAppConn  implements DevAppConn {

    List<AppStandard> appStandardList = new ArrayList<>();

    private AppDesc appDesc;

    public EventCheckerAppConn(){
        init();
    }

    private void init(){
        EventCheckerDevelopmentStandard standard = new EventCheckerDevelopmentStandard(this);
        appStandardList.add(standard);
    }

    @Override
    public DevelopmentIntegrationStandard getDevelopmentIntegrationStandard() {
        return (DevelopmentIntegrationStandard) appStandardList.get(0);
    }

    @Override
    public List<AppStandard> getAppStandards() {
        return appStandardList;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appDesc;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }
}
