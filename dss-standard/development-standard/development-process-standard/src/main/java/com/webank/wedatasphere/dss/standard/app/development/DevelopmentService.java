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

package com.webank.wedatasphere.dss.standard.app.development;


import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;

public interface DevelopmentService extends AppIntegrationService {

    void setSSOService(AppIntegrationService ssoService);

    AppIntegrationService getSSOService();

    void setAppStandard(DevelopmentIntegrationStandard appStandard);

    DevelopmentIntegrationStandard getAppStandard();

    AppInstance getAppInstance();

    void setAppInstance(AppInstance appInstance);

    List<DSSLabel> getLabels();
}



