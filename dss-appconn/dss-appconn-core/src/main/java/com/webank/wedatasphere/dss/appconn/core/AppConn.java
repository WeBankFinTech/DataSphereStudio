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

package com.webank.wedatasphere.dss.appconn.core;

import com.webank.wedatasphere.dss.appconn.core.exception.AppConnErrorException;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

import java.util.List;

public interface AppConn {

    void init() throws AppConnErrorException;

    /**
     * 1. Get the dssappconnbean table record
     * 2. Do a traversal to get all appinstances under each appconn
     * 3. Instantiate the real appconn interface
     */
    List<AppStandard> getAppStandards();

    AppDesc getAppDesc();

    void setAppDesc(AppDesc appDesc);

}
