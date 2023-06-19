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

package com.webank.wedatasphere.dss.appconn.manager.impl;

import com.webank.wedatasphere.dss.appconn.manager.service.AppConnInfoService;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnResourceService;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.utils.ClassUtils;


public class AppConnManagerImpl extends AbstractAppConnManager {

    @Override
    protected AppConnInfoService createAppConnInfoService() {
        try {
            //由于maven不能循环依赖，这里不能返回client端的实现类
            return ClassUtils.getInstance(AppConnInfoService.class);
        } catch (DSSErrorException e) {
            throw new DSSRuntimeException(25000, "Cannot find a useful AppConnInfoService.", e);
        }
    }

    @Override
    protected AppConnResourceService createAppConnResourceService() {
        try {
            return ClassUtils.getInstance(AppConnResourceService.class);
        } catch (DSSErrorException e) {
            throw new DSSRuntimeException(25000, "Cannot find a useful AppConnResourceService.", e);
        }
    }

}
