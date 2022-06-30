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

package com.webank.wedatasphere.dss.orchestrator.converter.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.RelToOrchestratorConversionService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.core.AppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;


public interface ConversionIntegrationStandard extends AppIntegrationStandard<SSORequestService> {

    /**
     * 用于支持将DSS编排，转换为调度系统的工作流
     * @return 返回具体实现类
     */
    DSSToRelConversionService getDSSToRelConversionService(AppInstance appInstance);

    /**
     * 预留接口，用于支持将调度系统的工作流，转换成DSS编排
     * @return 目前返回 null 即可
     */
    RelToOrchestratorConversionService getRelToDSSConversionService(AppInstance appInstance);

    /**
     * 由于 ConversionIntegration 是一个独立于三级规范之外的插件式规范，所以该规范没有途径可以拿到所需的
     * 三级规范中的一些Operation，这里预留一个接口，允许 ConversionIntegration 调用三级规范的 Operation.
     * @return 用户实现的AppConn
     */
    AppConn getAppConn();

    String getAppConnName();

    @Override
    default String getStandardName() {
        return "conversionIntegrationStandard";
    }

    @Override
    default int getGrade() {
        return 4;
    }

    @Override
    default boolean isNecessary() {
        return false;
    }
}
