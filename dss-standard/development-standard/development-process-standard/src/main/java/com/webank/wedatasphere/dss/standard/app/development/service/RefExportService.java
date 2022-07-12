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

package com.webank.wedatasphere.dss.standard.app.development.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;

/**
 * Job 导出规范，主要用于导出第三方应用工具的 Job。
 * 建议直接继承 AbstractRefExportService。
 */
public interface RefExportService extends DevelopmentService {

    /**
     * 支持将第三方 AppConn 的 Job 导出成 Linkis BML 物料或 InputStream 字节流。
     * @param <K> RefJobContentRequestRef 实现类
     * @return RefExportOperation 实现类
     */
    <K extends RefJobContentRequestRef<K>> RefExportOperation<K> getRefExportOperation();
}
