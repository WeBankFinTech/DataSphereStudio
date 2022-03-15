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

import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationResponseRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.RefOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public interface OrchestrationSearchOperation<R extends RefOrchestrationContentRequestRef<R>>
        extends StructureOperation<R, OrchestrationResponseRef> {

    /**
     * Try to search the orchestration by provide refOrchestrationId or orchestrationName.
     * If refOrchestrationId is not null, then the third-part AppConn should use refOrchestrationId to search the refOrchestration;
     * otherwise, the third-part AppConn should use orchestrationName to search the refOrchestration.
     * <br>
     * If the refOrchestration is exists, please return a OrchestrationResponseRef which has been set in the refOrchestrationId;
     * otherwise, just return an empty succeeded OrchestrationResponseRef.
     * @param requestRef the refOrchestration info
     * @return return a OrchestrationResponseRef contained refOrchestrationId if the refOrchestration is exists, otherwise
     * just return an empty succeeded OrchestrationResponseRef.
     * @throws ExternalOperationFailedException If some error are happened.
     */
    OrchestrationResponseRef searchOrchestration(R requestRef) throws ExternalOperationFailedException;

}
