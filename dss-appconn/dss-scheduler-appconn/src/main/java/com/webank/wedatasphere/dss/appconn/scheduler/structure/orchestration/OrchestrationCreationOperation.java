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

import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.DSSOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public interface OrchestrationCreationOperation<R extends DSSOrchestrationContentRequestRef<R>>
        extends StructureOperation<R, OrchestrationResponseRef> {

    /**
     * Try to create the one-to-one related refOrchestration in third-party AppConn.
     * If created successfully, please return a OrchestrationResponseRef contained refOrchestrationId,
     * so DSS can use the refOrchestrationId to operate the related refOrchestration in third-party AppConn.
     * The returned refOrchestrationId is the other OrchestrationOperations which used.
     * @param orchestrationRef contains the DSS orchestration info.
     * @return a OrchestrationResponseRef contained refOrchestrationId
     * @throws ExternalOperationFailedException
     */
    OrchestrationResponseRef createOrchestration(R orchestrationRef) throws ExternalOperationFailedException;

}
