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

import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;

/**
 * Job 导入规范，主要用于导入第三方应用工具的 Job。
 * 建议直接继承使用 {@code AbstractRefImportService}。
 */
public interface RefImportService extends DevelopmentService {

    /**
     * 通过传入 Linkis BML 物料或 InputStream 字节流，第三方 AppConn 需支持将其转换成一个 refJob。
     * @param <K> ImportRequestRef 实现类
     * @return RefImportOperation 实现类
     */
    <K extends ImportRequestRef<K>> RefImportOperation<K> getRefImportOperation();

}
