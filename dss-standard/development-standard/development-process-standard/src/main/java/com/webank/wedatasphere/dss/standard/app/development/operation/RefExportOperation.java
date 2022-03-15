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
 * Now, DSS only supports to export a third-part AppConn job to Linkis BML resources.
 * <br>
 * So, if third-part AppConn want to achieve the RefExportOperation, it is necessary that the third-part AppConn
 * must upload the third-part AppConn jobs to Linkis BML at first, and then return the resourceMap to DSS.
 */
public interface RefExportOperation<K extends RefJobContentRequestRef<K>> extends DevelopmentOperation<K, ExportResponseRef> {

    ExportResponseRef exportRef(K requestRef) throws ExternalOperationFailedException;

}


