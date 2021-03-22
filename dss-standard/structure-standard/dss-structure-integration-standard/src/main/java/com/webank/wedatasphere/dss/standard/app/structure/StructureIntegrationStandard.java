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

package com.webank.wedatasphere.dss.standard.app.structure;

import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.role.RoleService;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusService;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;

/**
 * Created by enjoyyin on 2020/8/10.
 */
public interface StructureIntegrationStandard extends AppStandard {

    /**
     * 统一角色规范，用于打通DSS与各集成接入系统的角色体系
     * @return
     */
    RoleService getRoleService();

    /**
     * 统一工程规范，用于打通DSS与各集成接入系统的工程体系
     * @return
     */
    ProjectService getProjectService();

    AppStatusService getAppStateService();

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
