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

package com.webank.wedatasphere.dss.standard.app.development.standard;


import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.core.AppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

/**
 * DSS 开发流程规范。用于打通并关联 DSS 的 Job 与集成的第三方 AppConn 的一个 Job，并在 DSS 的编排器（如：DSS 工作流）中对第三方
 * AppConn 的 Job 进行统一管理，DSS 编排器会提供通用的从需求 -> 设计 -> 开发 -> 调试 -> 导出 -> 导入 -> 发布 的全流程数据应用
 * 开发管理能力。
 * <br/>
 * 因而，该规范主要是为了使集成到 DSS 的第三方 AppConn 具备 DSS 的数据应用全流程开发管理能力，而要求第三方 AppConn 必须实现的各类能力。
 * <br/>
 * 建议直接继承 AbstractDevelopmentIntegrationStandard，包含了五个需要用户实现的 DevelopmentService。
 */
public interface DevelopmentIntegrationStandard extends AppIntegrationStandard<SSORequestService> {

    /**
     * Job 管理规范，主要用于管理第三方应用工具的 Job（命名为 refJob）。
     * @param appInstance 通过传入 AppInstance 来获取一个 Operation 实例
     * @return RefCRUDService 的实现类
     */
    RefCRUDService getRefCRUDService(AppInstance appInstance);

    /**
     * Job 执行规范，主要用于执行第三方应用工具的 Job。
     * @param appInstance 通过传入 AppInstance 来获取一个 Operation 实例
     * @return RefExecutionService 的实现类
     */
    RefExecutionService getRefExecutionService(AppInstance appInstance);

    /**
     * Job 导出规范，主要用于导出第三方应用工具的 Job。
     * @param appInstance 通过传入 AppInstance 来获取一个 Operation 实例
     * @return RefExportService 的实现类
     */
    RefExportService getRefExportService(AppInstance appInstance);

    /**
     * Job 导入规范，主要用于导入第三方应用工具的 Job。
     * @param appInstance 通过传入 AppInstance 来获取一个 Operation 实例
     * @return RefImportService 的实现类
     */
    RefImportService getRefImportService(AppInstance appInstance);

    /**
     * Job 查询规范，主要用于打开第三方应用工具的 Job 的页面。
     * @param appInstance 通过传入 AppInstance 来获取一个 Operation 实例
     * @return RefQueryService 的实现类
     */
    RefQueryService getRefQueryService(AppInstance appInstance);


    @Override
    default String getStandardName() {
        return "developmentIntegrationStandard";
    }

    @Override
    default int getGrade() {
        return 3;
    }

    @Override
    default boolean isNecessary() {
        return false;
    }

}
