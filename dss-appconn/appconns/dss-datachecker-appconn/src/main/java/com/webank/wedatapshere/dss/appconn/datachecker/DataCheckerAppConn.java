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

package com.webank.wedatapshere.dss.appconn.datachecker;

import com.webank.wedatapshere.dss.appconn.datachecker.standard.DataCheckerDevelopmentStandard;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.DevAppConn;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

import java.util.ArrayList;
import java.util.List;

/**
 * created by cooperyang on 2021/1/12
 * Description:
 */
public class DataCheckerAppConn implements DevAppConn {

    List<AppStandard> appStandardList = new ArrayList<>();

    private AppDesc appDesc;

    public DataCheckerAppConn(){
        init();
    }

    private void init(){
        DataCheckerDevelopmentStandard standard = new DataCheckerDevelopmentStandard(this);
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
