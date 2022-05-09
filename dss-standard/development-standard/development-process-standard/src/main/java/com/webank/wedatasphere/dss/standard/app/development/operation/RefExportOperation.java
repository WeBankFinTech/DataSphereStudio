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

package com.webank.wedatasphere.dss.standard.app.development.operation;

import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * 支持将第三方 AppConn 的 Job 导出成 Linkis BML 物料或 InputStream 字节流。
 * @param <K>
 */
public interface RefExportOperation<K extends RefJobContentRequestRef<K>> extends DevelopmentOperation<K, ExportResponseRef> {

    /**
     * Now, DSS only supports to export a third-part AppConn job to Linkis BML resources or {@code InputStream}.
     * <br>
     * So, if third-part AppConn want to achieve the {@code RefExportOperation}, it is necessary that the third-part AppConn
     * must upload the meta and resources of third-part AppConn job to Linkis BML at first, or packages the the meta and
     * resources of third-part AppConn job as a InputStream, then return the resourceMap to DSS.
     * For more detail, please see {@code ExportResponseRef} or {@code ImportRequestRef}.
     */
    ExportResponseRef exportRef(K requestRef) throws ExternalOperationFailedException;

}


