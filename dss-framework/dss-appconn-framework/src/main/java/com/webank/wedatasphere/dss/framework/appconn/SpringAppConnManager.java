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

package com.webank.wedatasphere.dss.framework.appconn;

import com.webank.wedatasphere.dss.appconn.manager.impl.AbstractAppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnInfoService;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnResourceService;
import com.webank.wedatasphere.linkis.DataWorkCloudApplication;


public class SpringAppConnManager extends AbstractAppConnManager {

    @Override
    protected AppConnInfoService createAppConnInfoService() {
        return DataWorkCloudApplication.getApplicationContext().getBean(AppConnInfoService.class);
    }

    @Override
    protected AppConnResourceService createAppConnResourceService() {
        return DataWorkCloudApplication.getApplicationContext().getBean(AppConnResourceService.class);
    }
}
