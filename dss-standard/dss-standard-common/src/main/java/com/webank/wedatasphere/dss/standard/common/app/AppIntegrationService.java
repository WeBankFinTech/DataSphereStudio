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

package com.webank.wedatasphere.dss.standard.common.app;


import com.webank.wedatasphere.dss.standard.common.service.AppService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

/**
 * Created by enjoyyin on 2020/9/9.
 */
public interface AppIntegrationService extends AppService {

    void setAppDesc(AppDesc appDesc);

    AppDesc getAppDesc();

}
