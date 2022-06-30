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

package com.webank.wedatasphere.dss.standard.app.structure;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.role.RoleService;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusService;
import com.webank.wedatasphere.dss.standard.common.core.AppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

/**
 * DSS 的二级规范，为组织结构规范。组织结构规范主要提供了以下的能力：
 * 1. 工程管理服务能力。工程管理服务用于打通 DSS 与第三方应用的工程体系。
 * 2. 角色管理服务能力。角色管理服务用于打通 DSS 与第三方应用的角色体系。
 * 3. 第三方应用状态管理能力。主要用于确认第三方应用的状态。
 * <br/>
 * 建议用户直接继承 {@code AbstractStructureIntegrationStandard}
 */
public interface StructureIntegrationStandard extends AppIntegrationStandard<SSORequestService> {

    /**
     * 统一角色规范，用于打通DSS与各集成接入系统的角色体系。
     * 该规范为预留规范，DSS 框架层暂未与这两个规范进行对接，用户直接返回 null 即可。
     * @param appInstance AppInstance 实例
     * @return 直接返回 null 即可
     */
    RoleService getRoleService(AppInstance appInstance);

    /**
     * 统一工程规范，用于打通DSS与各集成接入系统的工程体系
     * @param appInstance AppInstance 实例
     * @return ProjectService 实现类
     */
    ProjectService getProjectService(AppInstance appInstance);

    /**
     * 第三方应用状态检查规范。该规范为预留规范，DSS 框架层暂未与这两个规范进行对接，用户直接返回 null 即可。
     * @param appInstance AppInstance 实例
     * @return 直接返回 null 即可
     */
    AppStatusService getAppStateService(AppInstance appInstance);

    @Override
    default String getStandardName() {
        return "structureIntegrationStandard";
    }

    @Override
    default int getGrade() {
        return 2;
    }

    @Override
    default boolean isNecessary() {
        return false;
    }

}
