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

package com.webank.wedatasphere.dss.workflow.appconn;

import com.webank.wedatasphere.dss.appconn.core.WorkflowAppConn;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppDescImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author allenlliu
 * @date 2020/10/21 11:56 AM
 */

public class DefaultWorkflowAppConn implements WorkflowAppConn {

    private AppDesc appDesc;

    @Override
    public List<AppStandard> getAppStandards() {
        List<AppStandard> appStandardList = new ArrayList<>();
        WorkflowDevelopmentIntegrationStandard standard = new WorkflowDevelopmentIntegrationStandard(this);
        standard.setAppDesc(this.appDesc);
        appStandardList.add(standard);
        return appStandardList;
    }


    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public AppDesc getAppDesc() {
        if (null != appDesc) {
            return this.appDesc;
        }
        AppDescImpl appDesc = new AppDescImpl();
        appDesc.setAppName("WorkflowAppConn");
        appDesc.setId(1000L);
        return appDesc;
    }
}
