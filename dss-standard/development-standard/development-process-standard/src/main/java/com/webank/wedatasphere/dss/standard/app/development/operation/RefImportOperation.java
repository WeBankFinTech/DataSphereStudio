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

import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public interface RefImportOperation<K extends ImportRequestRef<K>>
        extends DevelopmentOperation<K, RefJobContentResponseRef> {

    /**
     * The resourceMap in ImportRequestRef is the content of ExportRequestRef exported.
     * <br>
     * Now, DSS only supports to import Linkis BML resources, so the resourceMap is consisted of `resourceId`
     * and `version`.
     * @return a refJobContent related to a only third appConn refJob which created by the resourceMap.
     */
    RefJobContentResponseRef importRef(K requestRef) throws ExternalOperationFailedException;

}
