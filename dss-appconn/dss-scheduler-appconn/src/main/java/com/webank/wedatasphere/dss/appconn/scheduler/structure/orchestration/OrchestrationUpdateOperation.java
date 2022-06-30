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

package com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration;

import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public interface OrchestrationUpdateOperation<R extends OrchestrationUpdateRequestRef<R>>
        extends StructureOperation<R, ResponseRef> {

    /**
     * Try to update the related refOrchestration in third-party AppConn.
     * Usually, DSS only want to update the orchestrationName, description and permissions info in third-party refOrchestration,
     * so the refOrchestrationId is always exists and can not be changeable.
     * <br>
     * Notice: do not try to change the refOrchestrationId already related with the third-party refOrchestration.
     * @param orchestrationRef contains the DSS Orchestration info updated.
     * @return the result of update, just success or failure.
     * @throws ExternalOperationFailedException
     */
    ResponseRef updateOrchestration(R orchestrationRef) throws ExternalOperationFailedException;

}
